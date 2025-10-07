package com.msa.adadyar

import android.app.Application
import com.msa.adadyar.core.storage.di.storageModule
import com.msa.adadyar.data.content.di.contentDataModule
import com.msa.adadyar.data.progress.di.progressDataModule
import com.msa.adadyar.domain.usecase.CompleteLessonUseCase
import com.msa.adadyar.domain.usecase.GetAllLessonsUseCase
import com.msa.adadyar.domain.usecase.GetLessonProgressUseCase
import com.msa.adadyar.domain.usecase.GetLessonUseCase
import com.msa.adadyar.domain.usecase.ObserveActiveProfileUseCase
import com.msa.adadyar.domain.usecase.ObserveProgressUseCase
import com.msa.adadyar.domain.usecase.RecordExerciseResultUseCase
import com.msa.adadyar.domain.usecase.SetActiveProfileUseCase
import com.msa.adadyar.domain.usecase.SetOnboardingDoneUseCase
import com.msa.adadyar.domain.usecase.SyncContentUseCase
import com.msa.adadyar.domain.usecase.UpdateRubricUseCase
import com.msa.adadyar.features.home.HomeViewModel
import com.msa.adadyar.features.lesson.LessonViewModel
import com.msa.adadyar.features.practice.PracticeViewModel
import com.msa.adadyar.features.progress.ProgressViewModel
import timber.log.Timber
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class AdadyarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@AdadyarApp)
            modules(appModule, storageModule, contentDataModule, progressDataModule)
        }
    }
}

private val appModule = module {
    factory { SyncContentUseCase(get(), get()) }
    factory { GetAllLessonsUseCase(get()) }
    factory { GetLessonUseCase(get()) }
    factory { GetLessonProgressUseCase(get()) }
    factory { CompleteLessonUseCase(get()) }
    factory { UpdateRubricUseCase(get()) }
    factory { ObserveProgressUseCase(get()) }
    factory { ObserveActiveProfileUseCase(get()) }
    factory { SetActiveProfileUseCase(get()) }
    factory { RecordExerciseResultUseCase(get()) }
    factory { SetOnboardingDoneUseCase(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { LessonViewModel(get(), get(), get(), get()) }
    viewModel { PracticeViewModel(get(), get()) }
    viewModel { ProgressViewModel(get()) }
}