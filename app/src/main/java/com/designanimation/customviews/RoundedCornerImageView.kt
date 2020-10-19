package com.designanimation.customviews

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.designanimation.R
import com.designanimation.d


open class RoundedCornerImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var maskCanvas: Canvas? = null

    private var maskBitmap: Bitmap? = null

    private var maskPaint: Paint? = null

    private var drawableCanvas: Canvas? = null

    private var drawableBitmap: Bitmap? = null

    private var drawablePaint: Paint? = null

    private var invalidated = true

    private var shape: Drawable? = null

    private var mat: Matrix? = null

    private var drawMatrix: Matrix? = null

    init {
        if (attrs != null) {
            val typedArray =
                context.obtainStyledAttributes(
                    attrs,
                    R.styleable.RoundedCornerImageView,
                    defStyleAttr,
                    0
                )
            try {
                shape = typedArray.getDrawable(R.styleable.RoundedCornerImageView_roundedDrawable)
            } finally {
                typedArray.recycle()
            }
        }
        if (scaleType == ScaleType.FIT_CENTER) {
            scaleType = ScaleType.CENTER_CROP
        }
        maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        maskPaint?.color = Color.BLACK
        mat = Matrix()
    }

    fun setShape(drawable: Drawable?) {
        this.shape = drawable
    }

    override fun invalidate() {
        invalidated = true
        super.invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createMaskCanvas(w, h, oldw, oldh)
    }

    private fun createMaskCanvas(width: Int, height: Int, oldw: Int, oldh: Int) {
        val sizeChanged = width != oldw || height != oldh
        val isValid = width > 0 && height > 0
        if (isValid && (maskCanvas == null || sizeChanged)) {
            maskCanvas = Canvas()
            maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            maskCanvas?.setBitmap(maskBitmap)
            maskPaint!!.reset()
            paintMaskCanvas(maskCanvas, width, height)
            drawableCanvas = Canvas()
            drawableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            drawableCanvas?.setBitmap(drawableBitmap)
            drawablePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            invalidated = true
        }
    }

    @Suppress("deprecation")
    override fun onDraw(canvas: Canvas) {
        if (!isInEditMode) {
            val saveCount = canvas.saveLayer(
                0.0f,
                0.0f,
                width.toFloat(),
                height.toFloat(),
                null,
                Canvas.ALL_SAVE_FLAG
            )
            try {
                if (invalidated) {
                    val drawable = drawable
                    if (drawable != null) {
                        invalidated = false
                        val imageMatrix: Matrix? = imageMatrix
                        if (imageMatrix == null) {
                            drawable.draw(drawableCanvas!!)
                        } else {
                            val drawableSaveCount = drawableCanvas!!.saveCount
                            drawableCanvas!!.save()
                            drawableCanvas!!.concat(imageMatrix)
                            drawable.draw(drawableCanvas!!)
                            drawableCanvas!!.restoreToCount(drawableSaveCount)
                        }
                        drawablePaint!!.reset()
                        drawablePaint!!.isFilterBitmap = false
                        drawablePaint!!.xfermode = PORTER_DUFF_XCODE
                        drawableCanvas!!.drawBitmap(maskBitmap!!, 0.0f, 0.0f, drawablePaint)
                    }
                }
                if (!invalidated) {
                    drawablePaint!!.xfermode = null
                    canvas.drawBitmap(drawableBitmap!!, 0.0f, 0.0f, drawablePaint)
                }
            } catch (e: Exception) {
                d(e.message.toString())
            } finally {
                canvas.restoreToCount(saveCount)
            }
        } else {
            super.onDraw(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        val dimen = width.coerceAtMost(height)
        setMeasuredDimension(dimen, dimen)
    }

    private fun paintMaskCanvas(maskCanvas: Canvas?, width: Int, height: Int) {
        maskCanvas?.let { canvas ->
            shape?.let { shape ->
                if (shape is BitmapDrawable) {
                    configureBitmapBounds(width, height)
                    if (drawMatrix != null) {
                        val drawableSaveCount = canvas.saveCount
                        canvas.save()
                        canvas.concat(matrix)
                        shape.draw(canvas)
                        canvas.restoreToCount(drawableSaveCount)
                        return
                    }
                }
                shape.setBounds(0, 0, width, height)
                shape.draw(canvas)
            }
        }
    }

    private fun configureBitmapBounds(viewWidth: Int, viewHeight: Int) {
        shape?.let { shape ->
            drawMatrix = null
            val drawableWidth = shape.intrinsicWidth
            val drawableHeight = shape.intrinsicHeight
            val fits = viewWidth == drawableWidth && viewHeight == drawableHeight
            if (drawableWidth > 0 && drawableHeight > 0 && !fits) {
                shape.setBounds(0, 0, drawableWidth, drawableHeight)
                val widthRatio = viewWidth.toFloat() / drawableWidth.toFloat()
                val heightRatio = viewHeight.toFloat() / drawableHeight.toFloat()
                val scale = widthRatio.coerceAtMost(heightRatio)
                val dx: Float = ((viewWidth - drawableWidth * scale) * 0.5f + 0.5f)
                val dy: Float = ((viewHeight - drawableHeight * scale) * 0.5f + 0.5f)
                matrix.setScale(scale, scale)
                matrix.postTranslate(dx, dy)
            }
        }
    }

    companion object {

        @JvmStatic
        private val PORTER_DUFF_XCODE = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }
}