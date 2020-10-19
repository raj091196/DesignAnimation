package com.designanimation.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.designanimation.R

class ShimmerRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var shimmerAdapter: ShimmerAdapter? = null

    private var shimmerLayout = -1

    private var itemAdapter: Adapter<*>? = null

    private var bindEvent: ShimmerBindEvent? = null

    init {
        if (attrs != null) {
            val typedArray =
                context.obtainStyledAttributes(
                    attrs,
                    R.styleable.ShimmerRecyclerView,
                    defStyleAttr,
                    0
                )
            try {
                shimmerLayout =
                    typedArray.getResourceId(R.styleable.ShimmerRecyclerView_shimmerLayout, -1)
            } finally {
                typedArray.recycle()
            }
        }
        shimmerAdapter = ShimmerAdapter(shimmerLayout)
    }

    fun showLoader(itemCount: Int) {
        shimmerAdapter?.setLoading(itemCount)
        layoutManager = this.layoutManager
        adapter = shimmerAdapter
    }

    fun hideLoader() {
        layoutManager = this.layoutManager
        adapter = itemAdapter
    }

    fun setShimmerBindEvent(bindEvent: ShimmerBindEvent) {
        this.bindEvent = bindEvent
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        if (adapter == null) {
            itemAdapter = null
        } else if (adapter != shimmerAdapter) {
            itemAdapter = adapter
        }
        super.setAdapter(adapter)
    }

    private inner class ShimmerAdapter(
        @LayoutRes private val layoutRes: Int = -1,
        private var count: Int = 10,
    ) : Adapter<ShimmerViewHolder>() {

        init {
            if (layoutRes == -1)
                throw Exception("Shimmer Layout attribute not set")
        }

        fun setLoading(count: Int) {
            this.count = count
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShimmerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
            return ShimmerViewHolder(view)
        }

        override fun onBindViewHolder(holder: ShimmerViewHolder, position: Int) {
            holder.bind(holder, position)
        }

        override fun getItemCount(): Int {
            return count
        }
    }

    inner class ShimmerViewHolder(itemView: View) : ViewHolder(itemView) {
        fun bind(holder: ShimmerViewHolder, position: Int) {
            bindEvent?.onBind(itemView, holder, position)
        }
    }
}

interface ShimmerBindEvent {
    fun onBind(itemView: View, holder: ShimmerRecyclerView.ShimmerViewHolder, position: Int)
}

