package com.example.flyapp.domain.usecase

import com.example.flyapp.data.repository.FlightRepository
import com.example.flyapp.domain.model.Flight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class GetFavoriteFlightsUseCase(private val flightRepository: FlightRepository) {
    operator fun invoke(): Flow<List<Flight>> {
        return flightRepository.getFavoriteFlights().distinctUntilChanged()
    }
}