package com.example.flyapp.domain.usecase

import com.example.flyapp.data.repository.FlightRepository

class DeleteFavoriteFlightUseCase(private val flightRepository: FlightRepository) {
    suspend operator fun invoke(departureCode: String, destinationCode: String) {
        flightRepository.deleteFavorite(departureCode, destinationCode)
    }
}