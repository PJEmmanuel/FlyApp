package com.example.flyapp.di

import android.content.Context
import com.example.flyapp.data.database.FlyDatabase
import com.example.flyapp.data.repository.FlightRepository
import com.example.flyapp.data.repository.OfflineFlyRepository

interface AppContainer {
    val flyRepository : FlightRepository
}
//Inyectamos la instancia de DAO a OfflineFly...
// Hay que pasarle un context y se le manda desde FlyApplication
class AppDataContainer(private val context: Context) : AppContainer{
    override val flyRepository: FlightRepository by lazy {
        OfflineFlyRepository(FlyDatabase.getDatabase(context).flyDAO())
    }
}