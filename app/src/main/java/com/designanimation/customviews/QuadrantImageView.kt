package com.designanimation.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.designanimation.R


class QuadrantImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var view: View? = null

    private var fourQuadImage1: AppCompatImageView? = null

    private var fourQuadImage2: AppCompatImageView? = null

    private var fourQuadImage3: AppCompatImageView? = null

    private var fourQuadImage4: AppCompatImageView? = null

    private val imageView = RoundedCornerImageView(context)

    private val defaultDrawable =
        TextDrawable(context, ContextCompat.getColor(context, R.color.saffron))

    init {
        initView()
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.quadrant_view, null)
        imageView.setShape(ContextCompat.getDrawable(context, R.drawable.ic_round_corner_drawable))
        fourQuadImage1 = view?.findViewById(R.id.image1)
        fourQuadImage2 = view?.findViewById(R.id.image2)
        fourQuadImage3 = view?.findViewById(R.id.image3)
        fourQuadImage4 = view?.findViewById(R.id.image4)
    }

    fun loadImages(drawableList: List<Drawable?>) {
        if (this.childCount >= 1) this.removeAllViews()
        when (drawableList.size) {
            1 -> loadSingleImageView(drawableList[0])
            2 -> {
            }
            3 -> loadTripleImageView(drawableList)
            4 -> loadQuadrantImageView(drawableList)
            else -> loadMoreImages(drawableList)
        }
    }

    private fun loadMoreImages(drawableList: List<Drawable?>) {
        addView(view)
        fourQuadImage1?.setImageDrawable(drawableList[0])
        fourQuadImage2?.setImageDrawable(drawableList[1])
        fourQuadImage3?.setImageDrawable(drawableList[2])
        defaultDrawable.setText("+${drawableList.size - 3}")
        fourQuadImage4?.setImageDrawable(defaultDrawable)
    }

    private fun loadQuadrantImageView(drawableList: List<Drawable?>) {
        addView(view)
        fourQuadImage1?.setImageDrawable(drawableList[0])
        fourQuadImage2?.setImageDrawable(drawableList[1])
        fourQuadImage3?.setImageDrawable(drawableList[2])
        fourQuadImage4?.setImageDrawable(drawableList[3])
    }

    private fun loadTripleImageView(drawableList: List<Drawable?>) {
        addView(view)
        fourQuadImage1?.setImageDrawable(drawableList[0])
        fourQuadImage2?.setImageDrawable(drawableList[1])
        fourQuadImage3?.setImageDrawable(drawableList[2])
    }

    private fun loadSingleImageView(drawable: Drawable?) {
        imageView.setImageDrawable(drawable)
        val params = LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        params.setMargins(10, 10, 10, 10)
        addView(imageView, params)
    }
}