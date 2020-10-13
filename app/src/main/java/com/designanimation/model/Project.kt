package com.designanimation.model

import androidx.annotation.DrawableRes
import com.designanimation.R

data class Project(

    @DrawableRes
    val list: ArrayList<Int>,

    val title1: String,

    val title2: String,

    val title3: String
) {
    companion object {

        fun formProjects(count: Int): ArrayList<Project> {
            val project = ArrayList<Project>()
            for (i in 1 until count) {
                project.add(Project(getDrawable(), "Planet Earth", "Alpha Studio", "21 items"))
            }
            return project
        }

        private fun getDrawable(): ArrayList<Int> {
            val list = ArrayList<Int>()
            list.add(R.drawable.index)
            list.add(R.drawable.index1)
            list.add(R.drawable.index2)
            list.add(R.drawable.index3)
            return list
        }
    }
}