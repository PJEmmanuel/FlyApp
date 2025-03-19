package com.example.flyapp.domain.usecase

import com.example.flyapp.data.repository.FlightRepository

class InsertFavoriteFlightUseCase(private val flightRepository: FlightRepository) {
    suspend operator fun invoke(departureCode: String, destinationCode: String) {
        flightRepository.insertFavorite(departureCode, destinationCode)
    }
}