package com.androidtest.journal

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
//用来全局获得Context 但可能导致内存泄漏
class MyApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}