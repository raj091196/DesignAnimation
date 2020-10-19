package com.designanimation.model

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.designanimation.R

data class Project(

    val list: ArrayList<Drawable?>,

    val title1: String,

    val title2: String,

    val title3: String
) {

    companion object {

        fun formProjects(count: Int, context: Context): ArrayList<Project> {
            val project = ArrayList<Project>()
            for (i in 1 until count) {
                project.add(
                    Project(
                        getDrawable(i, context),
                        "Planet Earth",
                        "Alpha Studio",
                        "21 items"
                    )
                )
            }
            return project
        }

        private fun getDrawable(i: Int, context: Context): ArrayList<Drawable?> {
            val list = formDrawableList(context)
            return when (i) {
                1 -> ArrayList(list.take(1))
                2 -> list.take(3) as ArrayList<Drawable?>
                3 -> list.take(3) as ArrayList<Drawable?>
                else -> list.plus(list) as ArrayList<Drawable?>
            }
        }

        private fun formDrawableList(context: Context): java.util.ArrayList<Drawable?> {
            val list = ArrayList<Drawable?>()
            list.add(ContextCompat.getDrawable(context, R.drawable.index))
            list.add(ContextCompat.getDrawable(context, R.drawable.index1))
            list.add(ContextCompat.getDrawable(context, R.drawable.index2))
            list.add(ContextCompat.getDrawable(context, R.drawable.index3))
            return list
        }
    }
}