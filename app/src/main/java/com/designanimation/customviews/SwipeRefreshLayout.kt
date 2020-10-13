package com.designanimation.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.Transformation
import android.widget.AbsListView
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.designanimation.R
import com.designanimation.isNull
import kotlin.math.abs
import kotlin.math.pow


@Suppress("DEPRECATION")
class SwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var animationDuration: String? = "4000"

    private var beatingRate: Int = 50

    private var isAttached: Boolean = false

    private var refreshProgress: Drawable? = null

    private var mTarget: View? = null

    private var refreshView: BeatingImageView? = null

    private var mDecelerateInterpolator: Interpolator? = null

    private var mTouchSlop = 0

    private var mTotalDragDistance = 0

    private var mCurrentDragPercent = 0f

    private var mCurrentOffsetTop = 0

    private var mRefreshing = false

    private var mActivePointerId = 0

    private var mIsBeingDragged = false

    private var mInitialMotionY = 0f

    private var mFrom = 0

    private var mFromDragPercent = 0f

    private var mNotify = false

    private var mListener: OnRefreshListener? = null

    private var mTargetPaddingTop = 0

    private var mTargetPaddingBottom = 0

    private var mTargetPaddingRight = 0

    private var mTargetPaddingLeft = 0

    init {
        getAttrs(context, attrs)
        mDecelerateInterpolator = DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mTotalDragDistance = PixelUtils.convertDpToPixel(context, DRAG_MAX_DISTANCE)
        setRefreshing(false)
        setView(BeatingImageView(context))
        setWillNotDraw(false)
        ViewCompat.setChildrenDrawingOrderEnabled(this, true)
    }

    private fun setView(view: BeatingImageView) {
        if (refreshView != null) {
            if (refreshView === view) return
            removeView(refreshView)
        }
        refreshView = view
        setBeatingImageViewParams()
        val params = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(refreshView, params)
        isAttached = true
    }

    private fun setBeatingImageViewParams() {
        refreshView?.setImageDrawable(refreshProgress)
        refreshView?.setDurationBasedOnBPM(beatingRate)
    }

    fun setProgressDrawable(refreshProgress: Drawable) {
        this.refreshProgress = refreshProgress
    }

    private fun getAttrs(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SwipeRefreshLayout,
            0, 0
        )
        try {
            refreshProgress = a.getDrawable(R.styleable.SwipeRefreshLayout_refreshDrawable)
            beatingRate = a.getInt(R.styleable.SwipeRefreshLayout_beatingRate, 50)
            animationDuration = a.getString(R.styleable.SwipeRefreshLayout_animationDuration)
        } finally {
            a.recycle()
        }

    }

    fun getTotalDragDistance(): Int {
        return mTotalDragDistance
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        ensureTarget()
        if (mTarget == null) return

        var lp = refreshView?.layoutParams as MarginLayoutParams
        val headViewLeft = paddingLeft + lp.leftMargin
        val headViewTop: Int = mCurrentOffsetTop - refreshView?.measuredHeight!! +
                paddingTop + lp.topMargin
        val headViewRight: Int = headViewLeft + refreshView?.measuredWidth!!
        val headViewBottom: Int = headViewTop + refreshView?.measuredHeight!!
        refreshView?.layout(headViewLeft, headViewTop, headViewRight, headViewBottom)

        lp = mTarget!!.layoutParams as MarginLayoutParams
        val childLeft = paddingLeft + lp.leftMargin
        val childTop: Int = mCurrentOffsetTop + paddingTop + lp.topMargin
        val childRight = childLeft + mTarget!!.measuredWidth
        val childBottom = childTop + mTarget!!.measuredHeight
        mTarget?.layout(childLeft, childTop, childRight, childBottom)
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateDefaultLayoutParams(): LayoutParams? {
        return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams? {
        return MarginLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams? {
        return MarginLayoutParams(context, attrs)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!isEnabled || canChildScrollUp() || mRefreshing) {
            return false
        }
        when (ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                setTargetOffsetTop(0, true)
                mActivePointerId = ev.getPointerId(0)
                mIsBeingDragged = false
                val initialMotionY = getMotionEventY(ev, mActivePointerId)
                if (initialMotionY == -1f) {
                    return false
                }
                mInitialMotionY = initialMotionY
            }
            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == ViewDragHelper.INVALID_POINTER) {
                    return false
                }
                val y = getMotionEventY(ev, mActivePointerId)
                if (y == -1f) {
                    return false
                }
                val yDiff = y - mInitialMotionY
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
                mActivePointerId = ViewDragHelper.INVALID_POINTER
            }
            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
        }
        return mIsBeingDragged
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!mIsBeingDragged) {
            return super.onTouchEvent(ev)
        }
        when (ev.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }
                val y = ev.getY(pointerIndex)
                val yDiff = y - mInitialMotionY
                val scrollTop = yDiff * DRAG_RATE
                mCurrentDragPercent = scrollTop / mTotalDragDistance
                if (mCurrentDragPercent < 0) {
                    return false
                }
                val boundedDragPercent = 1f.coerceAtMost(abs(mCurrentDragPercent))
                val extraOS = abs(scrollTop) - mTotalDragDistance
                val slingshotDist = mTotalDragDistance.toFloat()
                val tensionSlingshotPercent =
                    0f.coerceAtLeast(extraOS.coerceAtMost(slingshotDist * 2) / slingshotDist)
                val tensionPercent =
                    (tensionSlingshotPercent / 4 - (tensionSlingshotPercent / 4).toDouble()
                        .pow(2.0)).toFloat() * 2f
                val extraMove = slingshotDist * tensionPercent / 2
                val targetY = (slingshotDist * boundedDragPercent + extraMove).toInt()
                setTargetOffsetTop(targetY - mCurrentOffsetTop, true)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = ev.actionIndex
                mActivePointerId = ev.getPointerId(index)
            }
            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mActivePointerId == ViewDragHelper.INVALID_POINTER) {
                    return false
                }
                val pointerIndex = ev.findPointerIndex(mActivePointerId)
                val y = ev.getY(pointerIndex)
                val overScrollTop = (y - mInitialMotionY) * DRAG_RATE
                mIsBeingDragged = false
                if (overScrollTop > mTotalDragDistance) {
                    setRefreshing(refreshing = true, notify = true)
                } else {
                    mRefreshing = false
                    animateOffsetToStartPosition()
                }
                mActivePointerId = ViewDragHelper.INVALID_POINTER
                return false
            }
        }
        return true
    }

    private val mAnimateToCorrectPosition: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            val targetTop: Int
            val endTarget: Int = mTotalDragDistance
            targetTop = mFrom + ((endTarget - mFrom) * interpolatedTime).toInt()
            val offset: Int = targetTop - mTarget?.top!!
            mCurrentDragPercent = mFromDragPercent - (mFromDragPercent - 1.0f) * interpolatedTime
            setTargetOffsetTop(offset, false)
        }
    }

    private val mAnimateToStartPosition: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            moveToStart(interpolatedTime)
        }
    }

    private val refreshCompleteRunnable: Runnable = Runnable {
        setRefreshing(false)
    }

    private val mToStartListener: AnimationListener = object : AnimationListener {
        override fun onAnimationStart(animation: Animation) {}

        override fun onAnimationRepeat(animation: Animation) {}

        override fun onAnimationEnd(animation: Animation) {
            refreshView?.stop()
            mCurrentOffsetTop = mTarget?.top!!
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = widthMeasureSpec
        var height = heightMeasureSpec
        super.onMeasure(width, height)
        ensureTarget()
        if (mTarget == null) return
        width = MeasureSpec.makeMeasureSpec(
            measuredWidth - paddingRight - paddingLeft,
            MeasureSpec.EXACTLY
        )
        height = MeasureSpec.makeMeasureSpec(
            measuredHeight - paddingTop - paddingBottom,
            MeasureSpec.EXACTLY
        )
        mTarget?.measure(width, height)
        measureChildWithMargins(refreshView, widthMeasureSpec, 0, heightMeasureSpec, 0)
    }

    private fun ensureTarget() {
        if (mTarget != null) return
        if (childCount > 0) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child != refreshView) {
                    mTarget = child
                    mTargetPaddingBottom = mTarget?.paddingBottom!!
                    mTargetPaddingLeft = mTarget?.paddingLeft!!
                    mTargetPaddingRight = mTarget?.paddingRight!!
                    mTargetPaddingTop = mTarget?.paddingTop!!
                }
            }
        }
    }

    private fun animateOffsetToStartPosition() {
        mFrom = mCurrentOffsetTop
        mFromDragPercent = mCurrentDragPercent
        val animationDuration =
            abs((MAX_OFFSET_ANIMATION_DURATION * mFromDragPercent).toLong())
        mAnimateToStartPosition.reset()
        mAnimateToStartPosition.duration = animationDuration
        mAnimateToStartPosition.interpolator = mDecelerateInterpolator
        mAnimateToStartPosition.setAnimationListener(mToStartListener)
        refreshView?.clearAnimation()
        refreshView?.startAnimation(mAnimateToStartPosition)
    }

    private fun animateOffsetToCorrectPosition() {
        mFrom = mCurrentOffsetTop
        mFromDragPercent = mCurrentDragPercent
        mAnimateToCorrectPosition.reset()
        mAnimateToCorrectPosition.duration = MAX_OFFSET_ANIMATION_DURATION.toLong()
        mAnimateToCorrectPosition.interpolator = mDecelerateInterpolator
        refreshView?.clearAnimation()
        refreshView?.startAnimation(mAnimateToCorrectPosition)
        refresh()
        mCurrentOffsetTop = mTarget!!.top
        mTarget!!.setPadding(
            mTargetPaddingLeft,
            mTargetPaddingTop,
            mTargetPaddingRight,
            mTotalDragDistance
        )
    }

    private fun refresh() {
        if (mRefreshing) {
            refreshView?.start()
            if (mNotify) {
                if (mListener != null) {
                    mListener!!.onRefresh()
                }
                startRefreshCompletedHandler()
            }
        } else {
            refreshView?.stop()
            animateOffsetToStartPosition()
        }
    }

    private fun startRefreshCompletedHandler() {
        refreshView?.postDelayed(refreshCompleteRunnable, animationDuration.isNull("4000").toLong())
    }

    private fun moveToStart(interpolatedTime: Float) {
        val targetTop = mFrom - (mFrom * interpolatedTime).toInt()
        val targetPercent = mFromDragPercent * (1.0f - interpolatedTime)
        val offset = targetTop - mTarget!!.top
        mCurrentDragPercent = targetPercent
        mTarget!!.setPadding(
            mTargetPaddingLeft,
            mTargetPaddingTop,
            mTargetPaddingRight,
            mTargetPaddingBottom + targetTop
        )
        setTargetOffsetTop(offset, false)
    }

    fun setRefreshing(refreshing: Boolean) {
        if (mRefreshing != refreshing) {
            setRefreshing(refreshing, false)
        }
    }

    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (mRefreshing != refreshing) {
            mNotify = notify
            ensureTarget()
            mRefreshing = refreshing
            if (mRefreshing) {
                animateOffsetToCorrectPosition()
            } else {
                animateOffsetToStartPosition()
            }
        }
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = ev.getPointerId(newPointerIndex)
        }
    }

    private fun getMotionEventY(ev: MotionEvent, activePointerId: Int): Float {
        val index = ev.findPointerIndex(activePointerId)
        return if (index < 0) {
            (-1).toFloat()
        } else ev.getY(index)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setTargetOffsetTop(offset: Int, requiresUpdate: Boolean) {
        mTarget!!.offsetTopAndBottom(offset)
        refreshView?.offsetTopAndBottom(offset)
        mCurrentOffsetTop = mTarget!!.top
        if (requiresUpdate && Build.VERSION.SDK_INT < 11) {
            invalidate()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun canChildScrollUp(): Boolean {
        return if (Build.VERSION.SDK_INT < 14) {
            if (mTarget is AbsListView) {
                val absListView = mTarget as AbsListView
                (absListView.childCount > 0
                        && (absListView.firstVisiblePosition > 0 || absListView.getChildAt(0)
                    .top < absListView.paddingTop))
            } else {
                mTarget!!.scrollY > 0
            }
        } else {
            ViewCompat.canScrollVertically(mTarget, -1)
        }
    }


    fun setOnRefreshListener(listener: OnRefreshListener) {
        mListener = listener
    }

    companion object {

        private const val MAX_OFFSET_ANIMATION_DURATION = 1000

        private const val DRAG_MAX_DISTANCE = 100

        private const val DRAG_RATE = .5f

        private const val DECELERATE_INTERPOLATION_FACTOR = 1f
    }
}

interface OnRefreshListener {
    fun onRefresh()
}
