package com.example.flyapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.flyapp.data.model.AirportTable
import com.example.flyapp.data.model.FavoriteTable
import com.example.flyapp.data.model.dto.AirportSelected
import com.example.flyapp.domain.model.Flight
import kotlinx.coroutines.flow.Flow

@Dao
interface FlyDAO {

    /********** AUTOCOMPLETAR **********/
    // Para la lista autocompletar
    @Query("SELECT * FROM AIRPORT WHERE iata_code LIKE '%' || :currentSearch || '%' OR name LIKE '%' || :currentSearch || '%'")
    fun getSearchFly(currentSearch: String): Flow<List<AirportTable>>


    /********** LISTA DE VUELOS *********/
    /*
    Recoge todos los aeropuertos de destino menos el elegido, en orden descendente por pasajeros
    añadiendo true o false si el vuelo está en favoritos, para poder mostrar star del color correcto
     */
    @Query(
        """
    SELECT 
        a.name, 
        a.iata_code AS destinationCode, 
        CASE 
            WHEN f.id IS NOT NULL THEN 1 
            ELSE 0 
        END AS isFavorite
    FROM AIRPORT a
    LEFT JOIN favorite f 
        ON a.iata_code = f.destination_code AND f.departure_code = :codeFly
    WHERE a.iata_code != :codeFly 
    ORDER BY a.passengers DESC
"""
    )
    fun getSelectedFly(codeFly: String): Flow<List<AirportSelected>>


    /********* FAVORITOS *************/
    /*Recoger la tabla airport. Se comparan la lista recogida aquí y la de favoritos para insertar
     los nombres de los vuelos a la lista de vuelos favoritos.

    @Transaction: Esta anotación indica que la consulta debe ejecutarse dentro de una transacción.
     Esto es útil cuando la consulta implica múltiples operaciones o cuando se necesita asegurar la
      consistencia de los datos. En este caso, aunque la consulta es única, la anotación asegura que
      la operación de lectura de las dos tablas se haga de forma atómica.*/
    @Transaction
    @Query(
        """
        SELECT 
            f.departure_code AS departureCode,
            dep.name AS departureName,
            f.destination_code AS destinationCode,
            dest.name AS destinationName,
            1 AS isFavorite
        FROM favorite f
        INNER JOIN airport dep ON f.departure_code = dep.iata_code
        INNER JOIN airport dest ON f.destination_code = dest.iata_code
    """
    )
    fun getFavoriteFlights(): Flow<List<Flight>>


    /* Al insert de le pasa una instancia de FavoriteTable para que ROOM reconozca la entidad
        que se le está pasando en el DAO. Luego cuando se llama a al insert (desde OfflineFly...)
        ahí si puedes crear el objeto favoritetable(parametro = ..., parametro = ...) para asignar los
        parámetros a cada variable del Entity.
        */
    // Inserta los IATA en la lista de favoritos
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: FavoriteTable)

    @Query(
        """
        DELETE FROM favorite WHERE
            departure_code = :departureCode AND destination_code = :destinationCode"""
    )
    suspend fun deleteFavorite(departureCode: String, destinationCode: String)

    //Solo para los test
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAirportToTest(airport: AirportTable)

    //Solo para los test
    @Query("SELECT * FROM airport")
    suspend fun getAllFlightToTest(): List<AirportTable>

    //Solo para los test
    @Query("SELECT * FROM FAVORITE")
    fun getFavoritesFlightToTest(): List<FavoriteTable>
}

