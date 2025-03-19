package com.example.flyapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flyapp.data.model.AirportTable
import com.example.flyapp.data.model.FavoriteTable

@Database(entities = [AirportTable::class, FavoriteTable::class], version = 1, exportSchema = false)
abstract class FlyDatabase : RoomDatabase() {

    abstract fun flyDAO(): FlyDAO

    companion object {
        @Volatile
        private var Instance: FlyDatabase? = null

        fun getDatabase(context: Context): FlyDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, FlyDatabase::class.java, "flight_search")
                    .createFromAsset("database/flight_search.db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}