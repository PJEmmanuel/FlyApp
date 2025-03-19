package com.example.flyapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class FavoriteTable(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    @ColumnInfo("departure_code")
    val departureCode : String,
    @ColumnInfo("destination_code")
    val destinationCode : String,
)