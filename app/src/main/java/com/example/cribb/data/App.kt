package com.example.cribb.data

import android.app.Application
import android.content.Context
import android.content.Intent
import com.example.cribb.Main2Activity
import com.example.cribb.data.PreferenceRepository
import com.google.firebase.auth.FirebaseAuth

class App : Application() {

    lateinit var preferenceRepository: PreferenceRepository

    override fun onCreate() {
        super.onCreate()
        preferenceRepository = PreferenceRepository(
            getSharedPreferences(DEFAULT_PREFERENCES, Context.MODE_PRIVATE)
        )

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            val intent = Intent(this, Main2Activity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    companion object {
        const val DEFAULT_PREFERENCES = "default_preferences"
    }
}