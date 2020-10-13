package com.designanimation.customviews.explosion

import android.graphics.Bitmap
import android.graphics.Rect
import java.util.*

data class ExplosionParticle(

    var color: Int = 0,

    var random: Random? = null,

    var radius: Float = 0f,

    var randSpeed: Float = 0f,

    var initialX: Float = 0f,

    var initialY: Float = 0f,

    var x: Float = 0f,

    var y: Float = 0f,

    var alpha: Float = 0f
) {

    fun advance(factor: Float, bound: Rect, bitmap: Bitmap, moveFactor: Int) {
        radius *= (1f - factor / 40f)
        alpha = 1f - factor
        x += randSpeed * ((initialX - (bound.left + bitmap.width / 2)) / (bitmap.width / 2)) * moveFactor
        y += randSpeed * ((initialY - (bound.top + bitmap.height / 2)) / (bitmap.height / 2)) * moveFactor
    }
}