package com.thienhd.noteapp.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CircularProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0
    private var maxProgress = 100

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 50f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun setMaxProgress(maxProgress: Int) {
        this.maxProgress = maxProgress
        invalidate()
    }

    fun setProgressColor(color: Int) {
        paint.color = color
        invalidate()
    }

    fun setTextColor(color: Int) {
        textPaint.color = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val radius = (width.coerceAtMost(height) / 2) - paint.strokeWidth

        // Draw background circle
        paint.color = Color.LTGRAY
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)

        // Draw progress arc
        paint.color = Color.BLUE
        val sweepAngle = (360 * progress / maxProgress).toFloat()
        canvas.drawArc(
            paint.strokeWidth, paint.strokeWidth,
            width - paint.strokeWidth, height - paint.strokeWidth,
            -90f, sweepAngle, false, paint
        )

        // Draw percentage text
        val percentage = (100 * progress / maxProgress).toString()
        canvas.drawText(
            percentage,
            (width / 2).toFloat(),
            (height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2),
            textPaint
        )
    }
}
