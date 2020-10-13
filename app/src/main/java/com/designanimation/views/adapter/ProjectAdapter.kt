package com.designanimation.views.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.designanimation.R
import com.designanimation.customviews.PixelUtils
import com.designanimation.databinding.ItemProjectBinding
import com.designanimation.isEmpty
import com.designanimation.model.Project

class ProjectAdapter(private var list: ArrayList<Project>) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    private var count: Int = 0

    init {
        count = list.size
    }

    fun setItem(list: ArrayList<Project>) {
        this.list = list
        this.count = list.size
        notifyDataSetChanged()
    }

    fun setLoading(count: Int) {
        this.count = count
        list.clear()
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
        holder.bind(list, position)
    }

    override fun getItemCount(): Int {
        return count
    }

    inner class ProjectViewHolder(private val binding: ItemProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(list: ArrayList<Project>, position: Int) {
            list.isEmpty({
                val item = list[position]
                val context = binding.root.context
                clearBackground()
                binding.projectImage1.setImageBitmap(
                    PixelUtils.getRoundedCornerBitmap(
                        item.list[0],
                        720,
                        context
                    )
                )
                binding.projectImage2.setImageBitmap(
                    PixelUtils.getRoundedCornerBitmap(
                        item.list[1],
                        720,
                        context
                    )
                )
                binding.projectImage3.setImageBitmap(
                    PixelUtils.getRoundedCornerBitmap(
                        item.list[2],
                        720,
                        context
                    )
                )
                binding.projectImage4.setImageBitmap(
                    PixelUtils.getRoundedCornerBitmap(
                        item.list[3],
                        720,
                        context
                    )
                )
                binding.subTitle.text = item.title2
                binding.title.text = item.title1
                binding.projectItem.text = item.title3
            }, {
                clearImages()
                val imgDrawable =
                    getDrawable(binding.root.context, R.drawable.ic_round_corner_drawable)
                val txtDrawable =
                    getDrawable(binding.root.context, R.drawable.text_view_shimmer_bg)
                binding.projectImage1.background = imgDrawable
                binding.projectImage2.background = imgDrawable
                binding.projectImage3.background = imgDrawable
                binding.projectImage4.background = imgDrawable
                binding.subTitle.background = txtDrawable
                binding.title.background = txtDrawable
                binding.projectItem.background = txtDrawable
            })
        }

        private fun clearBackground() {
            binding.projectImage1.background = null
            binding.projectImage2.background = null
            binding.projectImage3.background = null
            binding.projectImage4.background = null
            binding.subTitle.background = null
            binding.title.background = null
            binding.projectItem.background = null
        }

        private fun clearImages() {
            binding.projectImage1.setImageBitmap(null)
            binding.projectImage2.setImageBitmap(null)
            binding.projectImage3.setImageBitmap(null)
            binding.projectImage4.setImageBitmap(null)
            binding.subTitle.text = ""
            binding.title.text = ""
            binding.projectItem.text = ""
        }
    }
}