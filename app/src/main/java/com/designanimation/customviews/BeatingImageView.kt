package com.designanimation.customviews

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.roundToInt


class BeatingImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var heartBeating = false

    private var scaleFactor = DEFAULT_SCALE_FACTOR

    private var reductionScaleFactor = -scaleFactor

    private var duration = DEFAULT_DURATION

    fun toggle() {
        if (heartBeating) {
            stop()
        } else {
            start()
        }
    }

    fun start() {
        heartBeating = true
        animate().scaleXBy(scaleFactor).scaleYBy(scaleFactor).setDuration(duration.toLong())
            .setListener(scaleUpListener)
    }

    fun stop() {
        heartBeating = false
        clearAnimation()
    }

    fun setDurationBasedOnBPM(bpm: Int) {
        if (bpm > 0) {
            duration = (milliInMinute / bpm / 3f).roundToInt()
        }
    }

    fun setDuration(duration: Int) {
        this.duration = duration
    }

    fun setScaleFactor(scaleFactor: Float) {
        this.scaleFactor = scaleFactor
        reductionScaleFactor = -scaleFactor
    }

    private val scaleUpListener: AnimatorListener = object : AnimatorListener {

        override fun onAnimationStart(animation: Animator) {}

        override fun onAnimationRepeat(animation: Animator) {}

        override fun onAnimationEnd(animation: Animator) {
            animate().scaleXBy(reductionScaleFactor).scaleYBy(reductionScaleFactor)
                .setDuration(duration.toLong()).setListener(scaleDownListener)
        }

        override fun onAnimationCancel(animation: Animator) {}
    }


    private val scaleDownListener: AnimatorListener = object : AnimatorListener {

        override fun onAnimationStart(animation: Animator) {}

        override fun onAnimationRepeat(animation: Animator) {}

        override fun onAnimationEnd(animation: Animator) {
            if (heartBeating) {
                animate().scaleXBy(scaleFactor).scaleYBy(scaleFactor)
                    .setDuration(duration * 2.toLong()).setListener(scaleUpListener)
            }
        }

        override fun onAnimationCancel(animation: Animator) {}
    }

    companion object {

        private const val DEFAULT_SCALE_FACTOR = 0.2f

        private const val DEFAULT_DURATION = 50

        private const val milliInMinute = 60000
    }
}