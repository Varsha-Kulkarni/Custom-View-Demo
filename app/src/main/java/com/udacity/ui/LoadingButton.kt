package com.udacity.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.udacity.R
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var animationProgressValue = 0

    private val textNormal = context.getString(R.string.button_name)
    private val textLoading = context.getString(R.string.button_loading)
    private var textDefault = textNormal

    var bounds: Rect = Rect()

    private var textColor = context.getColor(R.color.white)
    private var defaultColor = context.getColor(R.color.colorPrimary)
    private var progressColor = context.getColor(R.color.colorPrimaryDark)
    private var circularProgressColor = context.getColor(R.color.colorAccent)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 50.0f
        typeface = Typeface.create("", Typeface.BOLD)
        color = textColor
    }

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if(new ==  ButtonState.Loading ) {
            paint.color = progressColor
            valueAnimator.apply {
                setIntValues(0, 360)
                duration = 2000L
                addUpdateListener {
                    animationProgressValue = animatedValue as Int
                    invalidate()
                }
                repeatCount = ValueAnimator.INFINITE
                start()
            }
        }
        else{
            paint.color = defaultColor
        }
        invalidate()
    }

    init {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.LoadingButton,
                0, 0).apply {

            try {
                textColor = getColor(R.styleable.LoadingButton_textColor, textColor)
                defaultColor = getColor(R.styleable.LoadingButton_defaultColor, defaultColor)
                progressColor = getColor(R.styleable.LoadingButton_progressColor, progressColor)
                circularProgressColor = getColor(R.styleable.LoadingButton_circularProgressColor, circularProgressColor)

            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let{
            it.drawColor(defaultColor)
            paint.getTextBounds(textDefault, 0, textDefault.length, bounds)

            if(buttonState == ButtonState.Loading){
                textDefault = textLoading

                paint.color = progressColor
                it.drawRect(0f,0f,(animationProgressValue * widthSize / 360).toFloat() , heightSize.toFloat(), paint)

                paint.color = circularProgressColor
                val arcX = widthSize / 2f + bounds.width()/2
                val arcY = heightSize / 2f + ((paint.ascent() + paint.descent()) / 2)
                val arcRadius = 50f
                it.drawArc(arcX, arcY, arcX + arcRadius, arcY + arcRadius, 0f, animationProgressValue.toFloat(), true, paint)
            }
            else{
                textDefault = textNormal
            }

            paint.color = textColor
            val textX = widthSize / 2f
            val textY = heightSize / 2f - ((paint.ascent() + paint.descent()) / 2)
            it.drawText(textDefault, textX, textY , paint)

        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setLoadingButtonState(state: ButtonState){
        buttonState = state
    }

}