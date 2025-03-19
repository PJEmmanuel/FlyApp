package com.example.flyapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.flyapp.data.datastore.UserPreference
import com.example.flyapp.di.AppContainer
import com.example.flyapp.di.AppDataContainer

private const val SEARCH_KEY = "save_search_flight"
private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(SEARCH_KEY)

class FlyApplication : Application() {

    lateinit var container: AppContainer
    lateinit var userPreference: UserPreference

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        userPreference = UserPreference(dataStore)
    }
}