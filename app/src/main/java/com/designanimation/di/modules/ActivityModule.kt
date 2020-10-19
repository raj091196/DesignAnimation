package com.designanimation.di.modules

import com.designanimation.views.LauncherActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    internal abstract fun contributeLauncherActivity(): LauncherActivity
}