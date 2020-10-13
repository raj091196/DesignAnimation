package com.designanimation.customviews.explosion

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.designanimation.customviews.PixelUtils
import java.util.*
import kotlin.collections.ArrayList

class ExplosionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var listener: ExplosionListener? = null

    private val mPopViews: ArrayList<ExplosionAnimator?> = ArrayList()

    private val mExpandInset = IntArray(2)

    init {
        Arrays.fill(mExpandInset, PixelUtils.dp2Px(32))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (popView in mPopViews) {
            canvas?.let {
                popView?.draw(it)
            }
        }
    }

    @Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
    private fun explodeView(
        bitmap: Bitmap?, bound: Rect, startDelay: Long, duration: Long
    ) {
        val pop = bitmap?.let {
            ExplosionAnimator(this, it, bound)
        }
        pop?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                listener?.onFinish()
                mPopViews.remove(animation)
            }
        })
        pop?.startDelay = startDelay
        pop?.duration = duration
        mPopViews.add(pop)
        pop?.start()
    }

    fun explode(view: View?) {
        view?.let {
            val r = Rect()
            it.getGlobalVisibleRect(r)
            val location = IntArray(2)
            getLocationOnScreen(location)
            r.offset(-location[0], -location[1])
            val startDelay = 0
            it.animate().setDuration(150).setStartDelay(startDelay.toLong())
                .scaleX(0f).scaleY(0f).alpha(0f).start()
            explodeView(
                PixelUtils.createBitmapFromView(it),
                r,
                startDelay.toLong(),
                ExplosionAnimator.DEFAULT_DURATION
            )
        }
    }

    fun setExplosionListener(listener: ExplosionListener) {
        this.listener = listener
    }
}

interface ExplosionListener {
    fun onFinish()
}