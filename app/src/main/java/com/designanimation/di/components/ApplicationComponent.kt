package com.designanimation.di.components

import com.designanimation.Application
import com.designanimation.di.modules.ActivityModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [(ActivityModule::class), (AndroidInjectionModule::class)])
interface ApplicationComponent : AndroidInjector<Application> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ApplicationComponent
    }

    override fun inject(instance: Application)
}