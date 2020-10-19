package com.designanimation.views.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.designanimation.R
import com.designanimation.databinding.ItemProjectBinding
import com.designanimation.model.Project

class ProjectAdapter(private var list: ArrayList<Project>) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    fun setItem(list: ArrayList<Project>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding: ItemProjectBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_project,
            parent,
            false
        )
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ProjectViewHolder(private val binding: ItemProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(list: Project, position: Int) {
            binding.quadrantView.loadImages(list.list)
            binding.title.text = list.title1
            binding.subTitle.text = list.title2
            binding.projectItem.text = list.title3
        }
    }
}