package com.example.flyapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "airport")
data class AirportTable(
    @PrimaryKey
    val id : Int,
    @ColumnInfo("iata_code")
    val iATACode : String,
    val name : String,
    val passengers : Int,
)