package com.designanimation.views

import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.designanimation.BaseActivity
import com.designanimation.R
import com.designanimation.customviews.OnRefreshListener
import com.designanimation.d
import com.designanimation.databinding.LayoutLauncherBinding
import com.designanimation.model.Project
import com.designanimation.views.adapter.ProjectAdapter

@Suppress("UNCHECKED_CAST")
class LauncherActivity : BaseActivity(), OnRefreshListener {

    private lateinit var binding: LayoutLauncherBinding

    private lateinit var adapter: ProjectAdapter

    private lateinit var project: ArrayList<Project>

    private val handler = Handler(Looper.getMainLooper())

    private val loadingRunnable: Runnable = Runnable {
        adapter.setItem(project.clone() as ArrayList<Project>)
    }

    override fun initListeners() {
        project = Project.formProjects(10)
        adapter = ProjectAdapter(project.clone() as ArrayList<Project>)
        val linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
        binding.swipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.layout_launcher)
    }

    override fun onRefresh() {
        d("OnRefresh")
    }

    override fun onRefreshCompleted() {
        adapter.setLoading(project.size)
        handler.postDelayed(loadingRunnable, 2000)
    }
}