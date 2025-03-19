import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.flyapp.data.database.FlyDAO
import com.example.flyapp.data.database.FlyDatabase
import com.example.flyapp.data.model.AirportTable
import com.example.flyapp.data.model.FavoriteTable
import com.example.flyapp.data.model.dto.AirportSelected
import com.example.flyapp.domain.model.Flight
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class FlyDaoTest {

    companion object {
        private const val AIRPORT_NAME_FCO = "Leonardo Da Vinci"
        private const val AIRPORT_NAME_BCN = "Barcelona El Prat"
        private const val AIRPORT_NAME_MAD = "Aeropuerto de Barajas"
        private const val AIRPORT_CODE_FCO = "FCO"
        private const val AIRPORT_CODE_BCN = "BCN"
        private const val AIRPORT_CODE_MAD = "MAD"
    }

    private lateinit var flyDAO: FlyDAO
    private lateinit var flyDatabase: FlyDatabase

    @Before
    fun createDB() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // se usa una base de datos en la memoria y no se conserva en el disco
        // "inMemoryDatabaseBuilder" para los test
        flyDatabase = Room.inMemoryDatabaseBuilder(context, FlyDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        flyDAO = flyDatabase.flyDAO()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        flyDatabase.close()
    }


    // Datos para BBDD
    private val airportFlight1 = AirportTable(1, AIRPORT_CODE_FCO, AIRPORT_NAME_FCO, 2000)
    private val airportFlight2 = AirportTable(2, AIRPORT_CODE_BCN, AIRPORT_NAME_BCN, 1500)
    private val airportFlight3 = AirportTable(3, AIRPORT_CODE_MAD, AIRPORT_NAME_MAD, 5211)
    private var favoriteFlight1 = FavoriteTable(1, AIRPORT_CODE_FCO, AIRPORT_CODE_MAD)
    private var favoriteFlight2 = FavoriteTable(2, AIRPORT_CODE_BCN, AIRPORT_CODE_FCO)

    // Datos para prueba de DAO.getSelectedFly()
    private val expectedAirportSelected1 =
        AirportSelected(AIRPORT_NAME_MAD, AIRPORT_CODE_MAD, false)
    private val expectedAirportSelected2 =
        AirportSelected(AIRPORT_NAME_FCO, AIRPORT_CODE_FCO, true)

    // Datos para prueba DAO.getFavoriteFlights()
    private val expectedFavoriteFlight1 =
        Flight(AIRPORT_CODE_FCO, AIRPORT_NAME_FCO, AIRPORT_CODE_MAD, AIRPORT_NAME_MAD, true)
    private val expectedFavoriteFlight2 =
        Flight(AIRPORT_CODE_BCN, AIRPORT_NAME_BCN, AIRPORT_CODE_FCO, AIRPORT_NAME_FCO, true)

    //Fun auxiliar para insertar datos en AirportTable
    private suspend fun addThreeFlightsToDb() {
        flyDAO.insertAirportToTest(airportFlight1)
        flyDAO.insertAirportToTest(airportFlight2)
        flyDAO.insertAirportToTest(airportFlight3)
    }

    // Fun auxiliar para insertar datos en FavoriteTable
    private suspend fun addTwoFavoriteFLightsToDb() {
        flyDAO.insertFavorite(favoriteFlight1)
        flyDAO.insertFavorite(favoriteFlight2)
    }

    // Fun auxiliar para borrar datos de FavoriteTable
    private suspend fun deleteOneFavoriteFlightToDb() {
        flyDAO.deleteFavorite(favoriteFlight1.departureCode, favoriteFlight1.destinationCode)
    }

    /*Este test no tiene métodos DAO que usa la APP, por lo tanto no es vital su comprobación.
    Se usa para insertar datos en la BBDD temporal y comprobar que los tiene*/
    @Test
    @Throws(Exception::class)
    fun insertsFlightsIntoDb() = runBlocking {
        addThreeFlightsToDb()
        val listFlight = flyDAO.getAllFlightToTest()
        assertTrue(
            listFlight.containsAll(
                listOf(airportFlight1, airportFlight2, airportFlight3)
            )
        )
    }

    /* Para comparar los resultados que se han insertado, se usa un DAO que no usa la APP
    "getFavoritesFlightToTest()" */
    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsFavoriteFlightsIntoDb() = runBlocking {
        addTwoFavoriteFLightsToDb()
        val listFlight = flyDAO.getFavoritesFlightToTest()
        assertTrue(
            listFlight.containsAll(
                listOf(favoriteFlight1, favoriteFlight2)
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun daoGet_getSearchFly() = runBlocking {
        addThreeFlightsToDb()
        val listFlight = flyDAO.getSearchFly("C").first()

        //Solo airportFlight1 y airportFlight2, contienen la letra C en "iATATCode" or "name"
        assertTrue(
            listFlight.containsAll(
                listOf(airportFlight1, airportFlight2)
            )
        )
    }

    /* expectedAirportSelected1.isFavorite es false, por que no está en favoritos, para que muestre
    * el Icon.start en Color.Gray*/
    @Test
    @Throws(Exception::class)
    fun daoGet_getSelectedFlightFromDb() = runBlocking {
        addThreeFlightsToDb()
        addTwoFavoriteFLightsToDb()
        val listFlight = flyDAO.getSelectedFly("BCN").first()

        //Verifica tamaño
        assertEquals(2, listFlight.size)
        //Verifica expectedAirportSelected1
        assertTrue(listFlight.containsAll(listOf(expectedAirportSelected1)))
        //Verifica expectedAirportSelected2
        assertTrue(listFlight.containsAll(listOf(expectedAirportSelected2)))
    }

    @Test
    @Throws(Exception::class)
    fun daoGet_getFavoriteFlightsFromDb() = runBlocking {
        addThreeFlightsToDb()
        addTwoFavoriteFLightsToDb()
        val listFlight = flyDAO.getFavoriteFlights().first()

        //Verifica que hay 2
        assertEquals(2, listFlight.size)
        //Verifica expectedFavoriteFlight1
        assertTrue(listFlight.containsAll(listOf(expectedFavoriteFlight1)))
        //Verifica expectedFavoriteFlight2
        assertTrue(listFlight.containsAll(listOf(expectedFavoriteFlight2)))
    }

    @Test
    @Throws(Exception::class)
    fun daoDelete_deleteFavoriteFromDb() = runBlocking {
        addThreeFlightsToDb()
        addTwoFavoriteFLightsToDb()

        //Comprobamos que hay 2 favoritos
        val listWhitTwoFavorites = flyDAO.getFavoritesFlightToTest()
        assertEquals(2, listWhitTwoFavorites.size)

        //Se borra favoriteFligth1
        deleteOneFavoriteFlightToDb()

        //Comprobamos que hay 1 favorito y que sigue favoritFligth2, por que se ha borrado
        //1(favoriteFligth1)
        val listWhitOneFlightToDb = flyDAO.getFavoritesFlightToTest()
        assertEquals(1, listWhitOneFlightToDb.size)
        assertEquals(favoriteFlight2, favoriteFlight2)

    }
}