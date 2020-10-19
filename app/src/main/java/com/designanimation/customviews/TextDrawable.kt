package com.designanimation.customviews

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.Nullable
import androidx.appcompat.content.res.AppCompatResources
import com.designanimation.R


class TextDrawable(context: Context, @ColorInt colorCode: Int) : Drawable() {

    private var text: String? = null

    private var paint: Paint? = null

    private var intrinsicSize = 0

    private var drawable: Drawable? = null

    init {
        initPaint()
        intrinsicSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DRAWABLE_SIZE.toFloat(),
            context.resources.displayMetrics
        ).toInt()
        drawable = AppCompatResources.getDrawable(context, R.drawable.ic_round_corner_drawable)
        drawable?.setBounds(0, 0, intrinsicSize, intrinsicSize)
        @Suppress("deprecation")
        drawable?.setColorFilter(colorCode, PorterDuff.Mode.LIGHTEN)
    }

    private fun initPaint() {
        paint = Paint()
        paint?.color = Color.WHITE
        paint?.textSize = DEFAULT_TEXT_SIZE.toFloat()
        paint?.isAntiAlias = true
        paint?.isFakeBoldText = true
        paint?.setShadowLayer(20f, 0F, 0F, Color.GRAY)
        paint?.style = Paint.Style.FILL
        paint?.textAlign = Paint.Align.CENTER
    }

    fun setText(text: String?, @ColorInt colorCode: Int = Color.WHITE) {
        this.paint?.color = colorCode
        this.text = text
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        paint?.let { paint ->
            text?.let { text ->
                val bounds: Rect = bounds
                drawable!!.draw(canvas)
                canvas.drawText(
                    text.toCharArray(),
                    0,
                    text.length,
                    bounds.centerX().toFloat(),
                    (bounds.centerY() + paint.getFontMetricsInt(null) / 3).toFloat(),
                    paint
                )
            }
        }
    }

    override fun setAlpha(alpha: Int) {
        paint?.alpha = alpha
    }

    override fun setColorFilter(@Nullable colorFilter: ColorFilter?) {
        paint?.colorFilter = colorFilter
    }

    override fun getIntrinsicWidth(): Int {
        return intrinsicSize
    }

    override fun getIntrinsicHeight(): Int {
        return intrinsicWidth
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    companion object {

        private const val DRAWABLE_SIZE = 130

        private const val DEFAULT_TEXT_SIZE = 120
    }
}