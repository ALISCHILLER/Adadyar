package com.msa.adadyar.core.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "adadyar_preferences")