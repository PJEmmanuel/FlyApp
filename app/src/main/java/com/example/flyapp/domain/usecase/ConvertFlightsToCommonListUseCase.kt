package com.example.flyapp.domain.usecase

import com.example.flyapp.data.model.AirportTable
import com.example.flyapp.data.model.dto.AirportSelected
import com.example.flyapp.domain.model.Flight

object ConvertFlightsToCommonListUseCase {
    fun autocomplete(airport: AirportTable): Flight {
        return Flight(
            departureName = "",
            departureCode = "",
            destinationCode = airport.iATACode,
            destinationName = airport.name,
            isFavorite = false
        )
    }

    fun selected(airport: AirportSelected, departureCode: String, departureName: String): Flight {
        return Flight(
            departureCode = departureCode,
            departureName = departureName,
            destinationCode = airport.destinationCode,
            destinationName = airport.name,
            isFavorite = airport.isFavorite
        )
    }
}