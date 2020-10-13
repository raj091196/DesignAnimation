package com.designanimation.views

import androidx.databinding.DataBindingUtil
import com.designanimation.BaseActivity
import com.designanimation.R
import com.designanimation.customviews.OnRefreshListener
import com.designanimation.databinding.LayoutLauncherBinding

class LauncherActivity : BaseActivity() {

    private lateinit var binding: LayoutLauncherBinding

    override fun initListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(object :OnRefreshListener{
            override fun onRefresh() {
                //todo
            }
        })
    }

    override fun setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.layout_launcher)
    }
}