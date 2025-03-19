package com.example.flyapp.domain.model

data class Flight(
    val departureCode: String,
    val departureName: String,
    val destinationCode: String,
    val destinationName: String,
    val isFavorite: Boolean = false
)
