package com.justai.aimybox.components.launchfab

import android.animation.Animator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.justai.aimybox.components.R
import kotlin.math.sqrt

class ExtendableFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val inkView: View = View(context)
    private val actionButton = FloatingActionButton(context)

    private var isRevealed = false

    private val revealDurationMs = context.resources.getInteger(R.integer.assistant_reveal_time_ms).toLong()

    private var animator: ViewPropertyAnimator? = null

    private var contentViews = emptyList<View>()

    private var buttonExtendedColor: Int = Color.TRANSPARENT
    private var buttonCollapsedColor: Int = Color.TRANSPARENT

    private var buttonSize: Int = 0
    private var buttonMarginStart: Int = 0
    private var buttonMarginEnd: Int = 0
    private var buttonMarginBottom: Int = 0
    private var buttonGravity: Int = 0

    companion object {
        private const val SCALE = 8f
    }

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.ExtendableFloatingActionButton,
            defStyleAttr,
            R.style.BaseAssistantTheme
        ) {
            inkView.background = ShapeDrawable(OvalShape()).apply {
                paint.color = getColor(R.styleable.ExtendableFloatingActionButton_background_color, Color.TRANSPARENT)
            }

            buttonExtendedColor =
                getColor(R.styleable.ExtendableFloatingActionButton_button_extended_color, Color.TRANSPARENT)
            buttonCollapsedColor =
                getColor(R.styleable.ExtendableFloatingActionButton_button_collapsed_color, Color.TRANSPARENT)
            buttonSize = getDimension(R.styleable.ExtendableFloatingActionButton_button_size, 0F).toInt()
            buttonMarginStart = getDimension(R.styleable.ExtendableFloatingActionButton_button_margin_start, 0F).toInt()
            buttonMarginEnd = getDimension(R.styleable.ExtendableFloatingActionButton_button_margin_end, 0F).toInt()
            buttonMarginBottom =
                getDimension(R.styleable.ExtendableFloatingActionButton_button_margin_bottom, 0F).toInt()
            buttonGravity = getInteger(
                R.styleable.ExtendableFloatingActionButton_button_gravity,
                Gravity.BOTTOM or Gravity.END
            )


            setButtonColor(buttonCollapsedColor)
            actionButton.setImageDrawable(getDrawable(R.styleable.ExtendableFloatingActionButton_image_start))
            actionButton.customSize = buttonSize
        }
        addView(inkView)
        inkView.isInvisible = true
        addView(actionButton)
    }

    fun show(duration: Long = revealDurationMs) {
        //TODO refactor
        animator?.cancel()
        setButtonColor(buttonExtendedColor)

        val p = getLocationInView(this, actionButton)
        val x = p.x
        val y = p.y
        val startRadius = actionButton.height / 2

        inkView.isVisible = true

        val startScale = startRadius * 2f / inkView.height
        val finalScale = calculateScale(x, y) * SCALE

        prepareView(inkView, x, y, startScale)
        animator = inkView.animate().startInkAnimation(finalScale, duration) {
            contentViews.forEach { it.isVisible = true }
        }

        isRevealed = true
    }

    fun hide(duration: Long = revealDurationMs) {
        //TODO refactor
        animator?.cancel()

        val p = getLocationInView(this, actionButton)

        val x = p.x
        val y = p.y
        val endRadius = 0

        inkView.isVisible = true

        val startScale = calculateScale(x, y) * SCALE
        val finalScale = endRadius * SCALE / inkView.width

        prepareView(inkView, x, y, startScale)
        contentViews.forEach { it.isVisible = false }
        animator = inkView.animate().startInkAnimation(finalScale, duration) {
            setButtonColor(buttonCollapsedColor)
        }

        isRevealed = false
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        contentViews = children.filter { it != actionButton && it != inkView }.toList()
        contentViews.forEach { it.isVisible = false }
        actionButton.bringToFront()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        actionButton.setOnClickListener(l)
    }

    private fun setButtonColor(color: Int) {
        actionButton.backgroundTintList = ColorStateList.valueOf(color)
    }

    private fun ViewPropertyAnimator.startInkAnimation(
        scale: Float,
        duration: Long,
        onFinish: () -> Unit = {}
    ): ViewPropertyAnimator {
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
        return this
    }

    private fun prepareView(view: View, x: Int, y: Int, scale: Float) {
        //TODO refactor
        val centerX = view.width / 2
        val centerY = view.height / 2
        view.translationX = (x - centerX).toFloat()
        view.translationY = (y - centerY).toFloat()
        view.pivotX = centerX.toFloat()
        view.pivotY = centerY.toFloat()
        view.scaleX = scale
        view.scaleY = scale
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //TODO refactor
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)

        val circleSize = sqrt((width * width + height * height).toDouble()).toFloat() * 2f
        val size = (circleSize / SCALE).toInt()
        val sizeSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        inkView.measure(sizeSpec, sizeSpec)

        MeasureSpec.makeMeasureSpec(buttonSize, MeasureSpec.AT_MOST).let { buttonSize ->
            actionButton.measure(buttonSize, buttonSize)
        }

        actionButton.updateLayoutParams<FrameLayout.LayoutParams> {
            gravity = buttonGravity
            marginStart = buttonMarginStart
            marginEnd = buttonMarginEnd
            bottomMargin = buttonMarginBottom
        }
        contentViews.forEach { it.measure(widthMeasureSpec, heightMeasureSpec) }
    }

    /**
     * calculates the required scale of the ink-view to fill the whole view
     *
     * @param x circle center x
     * @param y circle center y
     * @return
     */
    private fun calculateScale(x: Int, y: Int): Float {
        //TODO refactor
        val centerX = width / 2f
        val centerY = height / 2f
        val maxDistance = sqrt((centerX * centerX + centerY * centerY).toDouble()).toFloat()

        val deltaX = centerX - x
        val deltaY = centerY - y
        val distance = sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toFloat()
        return 0.5f + distance / maxDistance * 0.5f
    }

    private fun getLocationInView(src: View, target: View): Point {
        //TODO refactor
        val l0 = IntArray(2)
        src.getLocationOnScreen(l0)

        val l1 = IntArray(2)
        target.getLocationOnScreen(l1)

        l1[0] = l1[0] - l0[0] + target.width / 2
        l1[1] = l1[1] - l0[1] + target.height / 2

        return Point(l1[0], l1[1])
    }
}