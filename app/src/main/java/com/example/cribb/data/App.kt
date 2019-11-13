package com.example.cribb.data

import android.app.Application
import android.content.Context
import com.example.cribb.data.PreferenceRepository

class App : Application() {

    lateinit var preferenceRepository: PreferenceRepository

    override fun onCreate() {
        super.onCreate()
        preferenceRepository = PreferenceRepository(
            getSharedPreferences(DEFAULT_PREFERENCES, Context.MODE_PRIVATE)
        )
    }

    companion object {
        const val DEFAULT_PREFERENCES = "default_preferences"
    }
}