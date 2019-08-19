package com.justai.aimybox.components.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.FrameLayout
import androidx.core.animation.doOnCancel
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.justai.aimybox.components.R
import com.justai.aimybox.components.extensions.dpToPx
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

internal class AimyboxButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val RECORDING_VIEW_ELEVATION_DP = 5
    }

    private var isExpanded: Boolean = false

    private val inkView: View = View(context)
    private val recordingView: View = View(context)
    private val actionButton = FloatingActionButton(context)
    private var contentViews = emptyList<View>()

    private val revealDurationMs = context.resources.getInteger(R.integer.assistant_reveal_time_ms).toLong()

    private var buttonExtendedColor: Int = Color.TRANSPARENT
    private var buttonCollapsedColor: Int = Color.TRANSPARENT
    private var buttonDrawableExtendedColor: Int = Color.TRANSPARENT
    private var buttonDrawableCollapsedColor: Int = Color.TRANSPARENT

    private var buttonSize: Float = 0F
    private var buttonMarginStart: Int = 0
    private var buttonMarginEnd: Int = 0
    private var buttonMarginBottom: Int = 0
    private var buttonGravity: Int = 0

    private var inkViewRadiusCollapsed: Float = 0F
    private var inkViewRadiusExpanded: Float = 0F

    private var inkAnimator: ViewPropertyAnimator? = null
    private var recordingAnimator: ValueAnimator? = null
    /**
     * The flag is automatically set to true when [onRecordingVolumeChanged] is called.
     * If the flag is false, then default simple repeating recording animation will be played during recording.
     * */
    private var isVolumeInformationAvailable: Boolean = false
    private var maxSoundVolume: Float = Float.MIN_VALUE
    private var minSoundVolume: Float = Float.MAX_VALUE
    /**
     * This value will be calculated based on sound volume samples interval, no need to set it manually
     * */
    private var soundVolumeAnimationDuration = 50L
    private var lastVolumeSampleTime: Long? = null

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.AimyboxButton,
            defStyleAttr,
            R.style.BaseAssistantTheme
        ) {
            getColor(R.styleable.AimyboxButton_background_color, Color.TRANSPARENT)
                .let(::createCircleShape)
                .let(inkView::setBackground)

            getColor(R.styleable.AimyboxButton_recording_color, Color.TRANSPARENT)
                .let(::createCircleShape)
                .let(recordingView::setBackground)

            buttonExtendedColor =
                getColor(R.styleable.AimyboxButton_button_extended_color, Color.TRANSPARENT)
            buttonCollapsedColor =
                getColor(R.styleable.AimyboxButton_button_collapsed_color, Color.TRANSPARENT)

            buttonDrawableExtendedColor =
                getColor(R.styleable.AimyboxButton_button_drawable_extended_color, Color.TRANSPARENT)
            buttonDrawableCollapsedColor =
                getColor(R.styleable.AimyboxButton_button_drawable_collapsed_color, Color.TRANSPARENT)

            buttonSize = getDimension(R.styleable.AimyboxButton_button_size, 0F)
            buttonMarginStart = getDimension(R.styleable.AimyboxButton_button_margin_start, 0F).toInt()
            buttonMarginEnd = getDimension(R.styleable.AimyboxButton_button_margin_end, 0F).toInt()
            buttonMarginBottom =
                getDimension(R.styleable.AimyboxButton_button_margin_bottom, 0F).toInt()
            buttonGravity = getInteger(
                R.styleable.AimyboxButton_button_gravity,
                Gravity.BOTTOM or Gravity.END
            )

            setButtonColors(false)
            actionButton.setImageDrawable(getDrawable(R.styleable.AimyboxButton_image_start))
            actionButton.customSize = buttonSize.toInt()
        }

        recordingView.elevation = RECORDING_VIEW_ELEVATION_DP.dpToPx(context)
        recordingView.outlineProvider = null

        addView(inkView)
        addView(recordingView)
        addView(actionButton)
    }

    fun expand(duration: Long = revealDurationMs) {
        if (isExpanded) return

        inkAnimator?.cancel()
        inkView.isVisible = true
        inkView.isVisible = true
        recordingView.isVisible = true

        setButtonColors(true)

        val targetScale = inkViewRadiusExpanded / inkViewRadiusCollapsed

        inkAnimator = inkView.startInkAnimation(targetScale, duration) {
            contentViews.forEach { it.isVisible = true }
        }

        isExpanded = true
    }

    fun collapse(duration: Long = revealDurationMs) {
        if (!isExpanded) return

        inkAnimator?.cancel()
        recordingAnimator?.cancel()
        inkView.isVisible = true
        recordingView.isVisible = false

        contentViews.forEach { it.isVisible = false }

        inkAnimator = inkView.startInkAnimation(1.0F, duration) {
            setButtonColors(false)
            inkView.isInvisible = true
        }

        isExpanded = false
    }
;
    fun onRecordingStopped() {
        recordingAnimator?.cancel()
        setRecordingViewScale(1F)
    }

    fun onRecordingStarted() {
        recordingAnimator?.cancel()
        if (!isVolumeInformationAvailable) {
            recordingAnimator = startSimpleRecordingAnimation()
        }
    }

    fun onRecordingVolumeChanged(volume: Float) {
        recordingAnimator?.cancel()
        isVolumeInformationAvailable = true

        maxSoundVolume = max(maxSoundVolume, volume)
        minSoundVolume = min(minSoundVolume, volume)

        val currentTime = System.currentTimeMillis()

        lastVolumeSampleTime?.let {
            soundVolumeAnimationDuration = currentTime - it
        }
        lastVolumeSampleTime = currentTime

        val soundInterval = maxSoundVolume - minSoundVolume

        // From 0 to 1
        val soundVolumeRelative = if (soundInterval == 0F) {
            0F
        } else {
            (volume - minSoundVolume) / soundInterval
        }

        val scale = soundVolumeRelative + 1F

        recordingAnimator = smoothSetRecordingViewScale(recordingView.scaleX, scale)
    }

    private fun setButtonColors(extended: Boolean) {
        actionButton.backgroundTintList = ColorStateList
            .valueOf(if (extended) buttonExtendedColor else buttonCollapsedColor)
        actionButton.imageTintList = ColorStateList
            .valueOf(if (extended) buttonDrawableExtendedColor else buttonDrawableCollapsedColor)
    }

    private fun setRecordingViewScale(scale: Float) = recordingView.apply {
        pivotX = width / 2F
        pivotY = height / 2F
        scaleX = scale
        scaleY = scale
    }

    private fun smoothSetRecordingViewScale(fromScale: Float, toScale: Float) = ValueAnimator().apply {
        setFloatValues(fromScale, toScale)
        duration = soundVolumeAnimationDuration
        addUpdateListener { animator -> setRecordingViewScale(animator.animatedValue as Float) }
        start()
    }

    private fun startSimpleRecordingAnimation() = ValueAnimator().apply {
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
        setFloatValues(1F, 2F)
        duration = 500
        addUpdateListener { animator -> setRecordingViewScale(animator.animatedValue as Float) }
        doOnCancel { setRecordingViewScale(1F) }
        start()
    }

    private fun View.startInkAnimation(
        scale: Float,
        duration: Long,
        onFinish: () -> Unit = {}
    ) = animate().apply {
        scaleX(scale)
        scaleY(scale)
        setDuration(duration)
        setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) = onFinish()
        })
        start()
    }

    private fun calculateExpandedRadius(
        parentWidth: Float,
        parentHeight: Float,
        x: Float,
        y: Float,
        radiusCollapsed: Float
    ): Float {
        val viewCenterX = x + radiusCollapsed
        val viewCenterY = y + radiusCollapsed

        val expandHorizontal = max(parentWidth - viewCenterX, parentWidth - (parentWidth - viewCenterX))
        val expandVertical = max(parentHeight - viewCenterY, parentHeight - (parentHeight - viewCenterY))

        return sqrt(expandHorizontal.pow(2) + expandVertical.pow(2))
    }

    private fun createCircleShape(color: Int) = ShapeDrawable(OvalShape()).apply {
        paint.color = color
    }

    override fun setOnClickListener(l: OnClickListener?) {
        actionButton.setOnClickListener(l)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        contentViews = children.filter { it != actionButton && it != inkView && it != recordingView }.toList()
        contentViews.forEach { it.isVisible = false }
        actionButton.bringToFront()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)

        inkViewRadiusCollapsed = buttonSize / 2
        inkViewRadiusExpanded = calculateExpandedRadius(
            width.toFloat(),
            height.toFloat(),
            inkView.x,
            inkView.y,
            buttonSize / 2F
        )

        MeasureSpec.makeMeasureSpec(buttonSize.toInt(), MeasureSpec.AT_MOST).let { measureSpec ->
            actionButton.measure(measureSpec, measureSpec)
            inkView.measure(measureSpec, measureSpec)
            recordingView.measure(measureSpec, measureSpec)
        }

        actionButton.updateLayoutParams<LayoutParams> {
            gravity = buttonGravity
            marginStart = buttonMarginStart
            marginEnd = buttonMarginEnd
            bottomMargin = buttonMarginBottom
        }
        contentViews.forEach { it.measure(widthMeasureSpec, heightMeasureSpec) }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        inkView.x = actionButton.x
        inkView.y = actionButton.y
        recordingView.x = actionButton.x
        recordingView.y = actionButton.y
    }

}