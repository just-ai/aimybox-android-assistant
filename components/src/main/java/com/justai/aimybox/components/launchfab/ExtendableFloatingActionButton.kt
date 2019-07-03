package com.justai.aimybox.components.launchfab

import android.animation.Animator
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
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.justai.aimybox.components.R
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class ExtendableFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var isExpanded: Boolean = false

    private val inkView: View = View(context)
    private val actionButton = FloatingActionButton(context)
    private var contentViews = emptyList<View>()

    private val revealDurationMs = context.resources.getInteger(R.integer.assistant_reveal_time_ms).toLong()

    private var buttonExtendedColor: Int = Color.TRANSPARENT
    private var buttonCollapsedColor: Int = Color.TRANSPARENT

    private var buttonSize: Float = 0F
    private var buttonMarginStart: Int = 0
    private var buttonMarginEnd: Int = 0
    private var buttonMarginBottom: Int = 0
    private var buttonGravity: Int = 0

    private var inkViewRadiusCollapsed: Float = 0F
    private var inkViewRadiusExpanded: Float = 0F

    private var animator: ViewPropertyAnimator? = null


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
            buttonSize = getDimension(R.styleable.ExtendableFloatingActionButton_button_size, 0F)
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
            actionButton.customSize = buttonSize.toInt()
        }

        addView(inkView)
        addView(actionButton)
    }

    fun expand(duration: Long = revealDurationMs) {
        if (isExpanded) return

        animator?.cancel()
        inkView.isVisible = true

        setButtonColor(buttonExtendedColor)


        val targetScale = inkViewRadiusExpanded / inkViewRadiusCollapsed

        animator = inkView.startInkAnimation(targetScale, duration) {
            contentViews.forEach { it.isVisible = true }
        }

        isExpanded = true
    }

    fun collapse(duration: Long = revealDurationMs) {
        if (!isExpanded) return

        animator?.cancel()
        inkView.isVisible = true

        contentViews.forEach { it.isVisible = false }

        animator = inkView.startInkAnimation(1.0F, duration) {
            setButtonColor(buttonCollapsedColor)
            inkView.isInvisible = true
        }

        isExpanded = false
    }

    private fun setButtonColor(color: Int) {
        actionButton.backgroundTintList = ColorStateList.valueOf(color)
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

    override fun setOnClickListener(l: OnClickListener?) {
        actionButton.setOnClickListener(l)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        contentViews = children.filter { it != actionButton && it != inkView }.toList()
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
            buttonSize / 2.0F
        )

        MeasureSpec.makeMeasureSpec(buttonSize.toInt(), MeasureSpec.AT_MOST).let { measureSpec ->
            actionButton.measure(measureSpec, measureSpec)
            inkView.measure(measureSpec, measureSpec)
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
    }

}