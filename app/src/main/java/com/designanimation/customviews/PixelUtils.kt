package com.designanimation.customviews

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import kotlin.math.roundToInt


class PixelUtils {

    companion object {

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
        fun convertDpToFloatPixel(context: Context, dp: Float): Float {
            val r: Resources = context.resources
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.displayMetrics)
        }

        @JvmStatic
        @Suppress("Deprecation")
        fun getDisplayMetrics(context: Context): DisplayMetrics {
            val metrics = DisplayMetrics()
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            return metrics
        }
    }
}