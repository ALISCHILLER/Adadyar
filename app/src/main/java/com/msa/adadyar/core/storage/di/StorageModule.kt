package com.msa.adadyar.core.storage.di

import android.content.Context
import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import coil.request.CachePolicy
import kotlinx.serialization.json.Json
import com.msa.adadyar.core.common.DefaultDispatchersProvider
import com.msa.adadyar.core.common.DispatchersProvider
import com.msa.adadyar.core.storage.datastore.appDataStore
import com.msa.adadyar.core.storage.json.AssetJsonReader
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    single { DefaultDispatchersProvider as DispatchersProvider }
    single { AssetJsonReader(androidContext()) }
    single { Json { ignoreUnknownKeys = true; prettyPrint = true } }
    single { androidContext().appDataStore }
    single { provideImageLoader(androidContext()) }
}

private fun provideImageLoader(context: Context): ImageLoader =
    ImageLoader.Builder(context)
        .dispatcher(Dispatchers.IO)
        .respectCacheHeaders(false)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .components { add(ImageDecoderDecoder.Factory()) }
        .build()