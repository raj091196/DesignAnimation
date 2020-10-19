package com.designanimation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView()
        initListeners()
    }

    private fun inject() {
        AndroidInjection.inject(this)
    }

    abstract fun initListeners()

    abstract fun setContentView()
}
