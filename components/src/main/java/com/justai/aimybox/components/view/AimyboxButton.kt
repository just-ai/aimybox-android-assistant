package com.justai.aimybox.components.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.animation.doOnCancel
import androidx.core.content.withStyledAttributes
import androidx.core.os.postDelayed
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.justai.aimybox.components.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

internal class AimyboxButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = R.attr.aimybox_assistantButtonTheme,
    defStyleRes: Int = R.style.DefaultAssistantTheme_AssistantButton
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var isExpanded: Boolean = false
    private var isRecording: Boolean = false

    /* Inner views */

    private val inkView: View = View(context)
    private val recordingView: View = View(context)
    private val actionButton = FloatingActionButton(context)
    private var contentViews = emptyList<View>()

    private val revealDurationMs = context.resources
        .getInteger(R.integer.assistant_reveal_time_ms).toLong()

    /* Button */

    @ColorInt
    private var buttonExpandedColor: Int = Color.TRANSPARENT
    @ColorInt
    private var buttonCollapsedColor: Int = Color.TRANSPARENT

    private var buttonSize: Float = 0F
    private var buttonMarginStart: Int = 0
    private var buttonMarginEnd: Int = 0
    private var buttonMarginBottom: Int = 0
    private var buttonGravity: Int = 0
    private var buttonElevation: Float = 0F

    private var buttonStartDrawable: Drawable? = null
    private var buttonStopDrawable: Drawable? = null

    @ColorInt
    private var buttonDrawableExpandedColor: Int = Color.TRANSPARENT
    @ColorInt
    private var buttonDrawableCollapsedColor: Int = Color.TRANSPARENT

    /* Background Ink View */

    @ColorInt
    private var inkViewBackgroundColor: Int = Color.TRANSPARENT
    private var inkViewBackground: Drawable? = null
    private var inkViewRadiusCollapsed: Float = 0F
    private var inkViewRadiusExpanded: Float = 0F

    private var inkAnimator: ViewPropertyAnimator? = null

    /* Recording view */

    @ColorInt
    private var recordingViewBackgroundColor: Int = Color.TRANSPARENT
    private var recordingViewBackground: Drawable? = null

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
        context.withStyledAttributes(attrs, R.styleable.AimyboxButton, defStyleAttr, defStyleRes) {
            buttonExpandedColor =
                getColor(R.styleable.AimyboxButton_aimybox_buttonExpandedColor, Color.TRANSPARENT)
            buttonCollapsedColor =
                getColor(R.styleable.AimyboxButton_aimybox_buttonCollapsedColor, Color.TRANSPARENT)

            buttonStartDrawable =
                getDrawable(R.styleable.AimyboxButton_aimybox_startRecordingDrawable)
            buttonStopDrawable =
                getDrawable(R.styleable.AimyboxButton_aimybox_stopRecordingDrawable)

            buttonDrawableExpandedColor =
                getColor(
                    R.styleable.AimyboxButton_aimybox_buttonExpandedIconTint,
                    Color.TRANSPARENT
                )
            buttonDrawableCollapsedColor =
                getColor(
                    R.styleable.AimyboxButton_aimybox_buttonCollapsedIconTint,
                    Color.TRANSPARENT
                )

            buttonSize =
                getDimension(R.styleable.AimyboxButton_aimybox_buttonSize, 0F)
            buttonMarginStart =
                getDimension(R.styleable.AimyboxButton_aimybox_buttonMarginStart, 0F).toInt()
            buttonMarginEnd =
                getDimension(R.styleable.AimyboxButton_aimybox_buttonMarginEnd, 0F).toInt()
            buttonMarginBottom =
                getDimension(R.styleable.AimyboxButton_aimybox_buttonMarginBottom, 0F).toInt()
            buttonElevation =
                getDimension(R.styleable.AimyboxButton_aimybox_buttonElevation, 0F)

            buttonGravity = getInteger(
                R.styleable.AimyboxButton_aimybox_buttonGravity,
                Gravity.BOTTOM or Gravity.END
            )

            inkViewBackgroundColor =
                getColor(R.styleable.AimyboxButton_aimybox_backgroundColor, Color.TRANSPARENT)
            inkViewBackground = createCircleShape(inkViewBackgroundColor)

            recordingViewBackgroundColor =
                getColor(
                    R.styleable.AimyboxButton_aimybox_recordingAnimationColor,
                    Color.TRANSPARENT
                )

            recordingViewBackground = createCircleShape(recordingViewBackgroundColor)
        }

        actionButton.customSize = buttonSize.toInt()
        actionButton.elevation = buttonElevation

        inkView.background = inkViewBackground

        recordingView.background = recordingViewBackground
        recordingView.elevation = buttonElevation
        recordingView.outlineProvider = null

        addView(inkView)
        addView(recordingView)
        addView(actionButton)

        setButtonColor(false)
    }

    fun expand(duration: Long = revealDurationMs) {
        if (isExpanded) return

        inkAnimator?.cancel()
        inkView.isVisible = true
        inkView.isVisible = true
        recordingView.isVisible = true

        setButtonColor(true)

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
        handler.postDelayed(300) {
            inkAnimator?.cancel()
            recordingAnimator?.cancel()
            inkAnimator = inkView.startInkAnimation(1.0F, duration) {
                setButtonColor(false)
                inkView.isInvisible = true
            }
        }


        isExpanded = false
    }

    fun onRecordingStarted() {
        isRecording = true
        recordingAnimator?.cancel()
        if (!isVolumeInformationAvailable) recordingAnimator = startSimpleRecordingAnimation()
        actionButton.setImageDrawable(buttonStopDrawable)
    }

    fun onRecordingStopped() {
        isRecording = false
        recordingAnimator?.cancel()
        setRecordingViewScale(1F)
        actionButton.setImageDrawable(buttonStartDrawable)
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

    private fun setButtonColor(isExpanded: Boolean) {
        actionButton.backgroundTintList = ColorStateList
            .valueOf(if (isExpanded) buttonExpandedColor else buttonCollapsedColor)
        actionButton.imageTintList = ColorStateList
            .valueOf(if (isExpanded) buttonDrawableExpandedColor else buttonDrawableCollapsedColor)
    }

    private fun setRecordingViewScale(scale: Float) = recordingView.apply {
        pivotX = width / 2F
        pivotY = height / 2F
        scaleX = scale
        scaleY = scale
    }

    private fun smoothSetRecordingViewScale(fromScale: Float, toScale: Float) =
        ValueAnimator().apply {
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

        val expandHorizontal =
            max(parentWidth - viewCenterX, parentWidth - (parentWidth - viewCenterX))
        val expandVertical =
            max(parentHeight - viewCenterY, parentHeight - (parentHeight - viewCenterY))

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
        contentViews =
            children.filter { it != actionButton && it != inkView && it != recordingView }.toList()
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