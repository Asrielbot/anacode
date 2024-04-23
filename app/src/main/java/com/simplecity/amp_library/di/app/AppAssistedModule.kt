package com.simplecity.amp_library.di.app

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_AppAssistedModule::class])
interface AppAssistedModule {
    fun someFunction()
}

// The abstract class is replaced with an interface
class AppAssistedModuleImpl : AppAssistedModule {
    override fun someFunction() {
    }
}
