package com.example.flyapp.data.repository

import com.example.flyapp.data.model.dto.AirportSelected
import com.example.flyapp.data.model.AirportTable
import com.example.flyapp.domain.model.Flight
import kotlinx.coroutines.flow.Flow

/* A flyrepository se le hacen las consultas y el coge la información de donde le digamos,
bien sea offline, online, bbdd, etc.
 */
interface FlightRepository {

    fun getSearchFlyStream(currentSearch : String) : Flow<List<AirportTable>>

    fun getSelectedFly(codeFly : String) : Flow<List<AirportSelected>>

   // suspend fun getFlightToCompare() : List<AirportSelected>

    fun getFavoriteFlights(): Flow<List<Flight>>

    suspend fun insertFavorite(departureCode: String, destinationCode: String)

    suspend fun deleteFavorite(departureCode: String, destinationCode: String)

}