package com.designanimation.customviews

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.TypedValue
import android.view.View
import kotlin.math.roundToInt


class PixelUtils {

    companion object {

        private val DENSITY = Resources.getSystem().displayMetrics.density

        private val sCanvas = Canvas()

        @JvmStatic
        fun convertDpToPixel(context: Context, dp: Int): Int {
            val r: Resources = context.resources
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r.displayMetrics
            )
            return px.roundToInt()
        }

        @JvmStatic
        fun dp2Px(dp: Int): Int {
            return (dp * DENSITY).roundToInt()
        }

        @JvmStatic
        fun createBitmapFromView(view: View): Bitmap? {
            if (view.measuredHeight <= 0) {
                val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                view.measure(spec, spec)
                view.layout(0, 0, view.measuredWidth, view.measuredHeight)
                val bitmap = createBitmapSafely(
                    view.measuredWidth,
                    view.measuredHeight,
                    Bitmap.Config.ARGB_8888,
                    1
                )
                if (bitmap != null) {
                    synchronized(sCanvas) {
                        val canvas = sCanvas
                        canvas.setBitmap(bitmap)
                        view.draw(canvas)
                        canvas.setBitmap(null)
                    }
                }
                return bitmap
            }
            view.clearFocus()
            val bitmap = createBitmapSafely(
                view.width,
                view.height, Bitmap.Config.ARGB_8888, 1
            )
            if (bitmap != null) {
                synchronized(sCanvas) {
                    val canvas = sCanvas
                    canvas.setBitmap(bitmap)
                    view.draw(canvas)
                    canvas.setBitmap(null)
                }
            }
            return bitmap
        }

        @JvmStatic
        private fun createBitmapSafely(
            width: Int,
            height: Int,
            config: Bitmap.Config?,
            retryCount: Int
        ): Bitmap? {
            return try {
                Bitmap.createBitmap(width, height, config!!)
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
                if (retryCount > 0) {
                    System.gc()
                    return createBitmapSafely(width, height, config, retryCount - 1)
                }
                null
            }
        }
    }
}