package com.example.flyapp.domain.usecase

import com.example.flyapp.data.model.dto.AirportSelected
import com.example.flyapp.data.repository.FlightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class GetSelectedFlightUseCase(private val flightRepository: FlightRepository) {
    operator fun invoke(departureCode: String): Flow<List<AirportSelected>> {
        return flightRepository.getSelectedFly(departureCode).distinctUntilChanged()
    }
}




