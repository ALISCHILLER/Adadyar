package com.msa.adadyar.core.storage.json

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AssetJsonReader(private val context: Context) {
    suspend fun read(path: String): String = withContext(Dispatchers.IO) {
        context.assets.open(path).bufferedReader().use { it.readText() }
    }
}