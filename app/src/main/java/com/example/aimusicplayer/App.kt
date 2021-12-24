package com.example.aimusicplayer

import android.app.Application
import com.singhajit.sherlock.core.Sherlock

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Sherlock.init(this)
    }
}