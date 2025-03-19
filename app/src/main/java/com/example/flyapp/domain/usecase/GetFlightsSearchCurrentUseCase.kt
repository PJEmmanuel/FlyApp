package com.example.flyapp.domain.usecase

import com.example.flyapp.data.model.AirportTable
import com.example.flyapp.data.repository.FlightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class GetFlightsSearchCurrentUseCase(private val flightRepository: FlightRepository) {
    operator fun invoke(query: String): Flow<List<AirportTable>> {
        return flightRepository.getSearchFlyStream(query).distinctUntilChanged()
    }
}