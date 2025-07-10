package com.oldogz.core.alarm.di

import androidx.work.ListenableWorker
import com.oldogz.core.alarm.workermanager.factory.ChildWorkerFactory
import com.oldogz.core.alarm.workermanager.factory.RescheduleAlarmWorkerFactory
import com.oldogz.core.alarm.workermanager.worker.RescheduleAlarmWorker
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@InstallIn(SingletonComponent::class)
@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(RescheduleAlarmWorker::class)
    abstract fun bindRescheduleAlarmWorker(rescheduleAlarmWorkerFactory: RescheduleAlarmWorkerFactory): ChildWorkerFactory
}
