package com.designanimation.customviews.explosion

import android.animation.ValueAnimator
import android.graphics.*
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import java.util.*

class ExplosionAnimator(
    container: View,
    bitmap: Bitmap,
    bound: Rect
) : ValueAnimator() {

    private var defaultRadius = 0

    private var mPaint: Paint? = null

    private var mBound: Rect? = null

    private var mParticles: Array<Array<ExplosionParticle>>

    private var mBitmap: Bitmap? = null

    private var mContainer: View? = null

    private var isFirstTime = true

    private var totalNoOfDrawsAdvance: Long = DEFAULT_DURATION * 60

    private var noOfDrawsAdvance: Long = 0

    init {
        mPaint = Paint()
        mBound = Rect(bound)
        val random = Random(System.currentTimeMillis())
        val noOfParticlesX = (bitmap.width.toFloat() / bitmap.width
                * PARTICLE_COUNT_FACTOR.toFloat()).toInt()
        val noOfParticlesY = (bitmap.height.toFloat() / bitmap.width
                * PARTICLE_COUNT_FACTOR.toFloat()).toInt()
        val bitMapWidth: Int = bitmap.width
        val bitMapHeight: Int = bitmap.height
        mParticles = Array(noOfParticlesY) { Array(noOfParticlesX) { ExplosionParticle() } }
        defaultRadius =
            (bitMapWidth / (2 * noOfParticlesX) + bitMapHeight / (2 * noOfParticlesY)) / 2
        formParticles(noOfParticlesX, noOfParticlesY, bitmap, bitMapHeight, bitMapWidth, random)
        mBitmap = bitmap
        mContainer = container
        setFloatValues(0f, 1f)
        interpolator = DEFAULT_INTERPOLATOR
        duration = DEFAULT_DURATION
    }

    private fun formParticles(
        noOfParticlesX: Int,
        noOfParticlesY: Int,
        bitmap: Bitmap,
        bitMapHeight: Int,
        bitMapWidth: Int,
        random: Random
    ) {
        for (i in 0 until noOfParticlesY) {
            for (j in 0 until noOfParticlesX) {
                mParticles[i][j] = generateParticle(
                    bitmap.getPixel(
                        j * bitMapWidth / noOfParticlesX,
                        i * bitMapHeight / noOfParticlesY
                    ),
                    (mBound!!.left + j * defaultRadius * 2).toFloat(),
                    (mBound!!.top + i * defaultRadius * 2).toFloat(),
                    random
                )!!
            }
        }
    }

    fun draw(canvas: Canvas): Boolean {
        if (!isStarted || mBound == null || mBitmap == null) {
            return false
        }
        for (i in mParticles.indices) {
            for (j in mParticles[i].indices) {
                noOfDrawsAdvance++
                if (noOfDrawsAdvance > totalNoOfDrawsAdvance) {
                    mContainer?.invalidate()
                    return true
                }
                mParticles[i][j].advance(
                    animatedValue as Float,
                    mBound!!,
                    mBitmap!!,
                    PARTICLE_MOVE_FACTOR
                )
                if (mParticles[i][j].alpha > 0f) {
                    mPaint?.color = mParticles[i][j].color
                    mPaint?.alpha = (Color.alpha(
                        mParticles[i][j].color
                    ) * mParticles[i][j].alpha).toInt()
                    canvas.drawCircle(
                        mParticles[i][j].x,
                        mParticles[i][j].y,
                        mParticles[i][j].radius,
                        mPaint!!
                    )
                }
            }
        }
        isFirstTime = false
        mContainer?.invalidate()
        return true
    }

    private fun generateParticle(
        color: Int,
        initialX: Float,
        initialY: Float,
        random: Random
    ): ExplosionParticle? {
        val particle = ExplosionParticle()
        particle.color = color
        particle.initialX = initialX
        particle.initialY = initialY
        particle.random = random
        particle.x = initialX
        particle.y = initialY
        val randRadius = random.nextFloat()
        val randSpeed = random.nextFloat() * 5
        particle.randSpeed = randSpeed
        particle.radius = randRadius * defaultRadius
        return particle
    }

    @Suppress("deprecation")
    override fun start() {
        super.start()
        mContainer?.invalidate(mBound)
    }

    companion object {

        var DEFAULT_DURATION: Long = 0x500

        private const val PARTICLE_COUNT_FACTOR = 30

        private const val PARTICLE_MOVE_FACTOR = 10

        private val DEFAULT_INTERPOLATOR: Interpolator = DecelerateInterpolator(0.8f)

    }
}