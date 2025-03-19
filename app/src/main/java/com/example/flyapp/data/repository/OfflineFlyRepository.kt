package com.example.flyapp.data.repository

import com.example.flyapp.data.model.dto.AirportSelected
import com.example.flyapp.data.database.FlyDAO
import com.example.flyapp.data.model.AirportTable
import com.example.flyapp.data.model.FavoriteTable
import com.example.flyapp.domain.model.Flight
import kotlinx.coroutines.flow.Flow

/*DAO se le pasa por parámetro desde AppContainer. El DAO se llama con
flyDAO.getSearchFly, insertFavorite..., este se llama a través de FlyRespository{}
 y las consultas, insert, etc... Se le piden a FlyRepository*/

class OfflineFlyRepository(private val flyDAO: FlyDAO) : FlightRepository {
    override fun getSearchFlyStream(currentSearch: String): Flow<List<AirportTable>> =
        flyDAO.getSearchFly(currentSearch)

    override fun getSelectedFly(codeFly: String): Flow<List<AirportSelected>> =
        flyDAO.getSelectedFly(codeFly)

    override suspend fun insertFavorite(departureCode: String, destinationCode: String) =
        flyDAO.insertFavorite(
            FavoriteTable(
                departureCode = departureCode,
                destinationCode = destinationCode
            )
        )

    override fun getFavoriteFlights(): Flow<List<Flight>> =
        flyDAO.getFavoriteFlights()

    override suspend fun deleteFavorite(departureCode: String, destinationCode: String) =
        flyDAO.deleteFavorite(departureCode, destinationCode)
}
