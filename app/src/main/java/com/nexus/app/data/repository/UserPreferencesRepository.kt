package com.nexus.app.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs = context.getSharedPreferences("nexus_prefs", Context.MODE_PRIVATE)

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean("onboarding_completed", false)

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("onboarding_completed", completed).apply()
    }

    fun getGeminiApiKey(): String? = prefs.getString("gemini_api_key", null)

    fun setGeminiApiKey(key: String) {
        prefs.edit().putString("gemini_api_key", key).apply()
    }

    fun getUserName(): String? = prefs.getString("user_name", null)

    fun setUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
    }
}
