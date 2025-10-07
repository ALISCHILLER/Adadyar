package com.msa.adadyar.core.storage.datastore


import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AppPreferencesKeys {
    val Theme = stringPreferencesKey("ui.theme")
    val Difficulty = stringPreferencesKey("ui.difficulty")
    val LastProfileId = stringPreferencesKey("user.last_profile_id")
    val LastLessonId = stringPreferencesKey("user.last_lesson_id")
    val ContentVersion = stringPreferencesKey("content.version")
    val OnboardingDone = booleanPreferencesKey("onboarding.done")
}