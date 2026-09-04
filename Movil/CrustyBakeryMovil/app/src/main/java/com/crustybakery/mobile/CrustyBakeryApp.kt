package com.crustybakery.mobile

import android.content.Context
import com.crustybakery.mobile.data.local.SessionManager
import com.crustybakery.mobile.data.remote.ApiClient
import com.crustybakery.mobile.data.repository.AppRepository

object CrustyBakeryApp {

    lateinit var repository: AppRepository
        private set

    lateinit var sessionManager: SessionManager
        private set

    fun init(context: Context) {
        if (!::repository.isInitialized) {
            repository = AppRepository(ApiClient.service)
            sessionManager = SessionManager(context.applicationContext)
        }
    }
}
