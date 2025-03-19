package com.example.flyapp.ui.Screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flyapp.FlyApplication
import com.example.flyapp.data.datastore.UserPreference
import com.example.flyapp.domain.model.Flight
import com.example.flyapp.domain.usecase.ConvertFlightsToCommonListUseCase
import com.example.flyapp.domain.usecase.DeleteFavoriteFlightUseCase
import com.example.flyapp.domain.usecase.GetFavoriteFlightsUseCase
import com.example.flyapp.domain.usecase.GetFlightsSearchCurrentUseCase
import com.example.flyapp.domain.usecase.GetSelectedFlightUseCase
import com.example.flyapp.domain.usecase.InsertFavoriteFlightUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class FlyAppViewModel(
    private val getFavoriteFlightsUseCase: GetFavoriteFlightsUseCase,
    private val insertFavoriteFlightUseCase: InsertFavoriteFlightUseCase,
    private val deleteFavoriteFlightUseCase: DeleteFavoriteFlightUseCase,
    private val getFlightsSearchCurrentUseCase: GetFlightsSearchCurrentUseCase,
    private val getSelectedFlightUseCase: GetSelectedFlightUseCase,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userPreference.readSearchFlight
                .debounce(300L)
                .distinctUntilChanged()
                .collect {
                convertAutocompleteToCommon(it)
            }
        }
        convertFavoriteToCommon()
    }

    fun saveSearchFlight(query: String) {
        viewModelScope.launch {
            userPreference.saveSearchFlight(query)
        }
    }

    // ****** AUTOCOMPLETAR ******   Recoge datos de la BBDD
    private fun getSearchCurrentFlight(currentSearch: String) =
        getFlightsSearchCurrentUseCase(currentSearch)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /* ****** AUTOCOMPLETAR ******   Recoge datos de la BBDD para mostrarlo en el autocompletar
     según la palabra introducida. Además pone true la vista autoCompleteVisible */
    fun convertAutocompleteToCommon(query: String) {
        val isAutoCompleteVisible = query.isNotBlank() // Respuesta Boolean
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getSearchCurrentFlight(query).collect { list ->
                    val mappedToFlights = list.map {
                        ConvertFlightsToCommonListUseCase
                            .autocomplete(it)
                    }
                    _uiState.update {
                        it.copy(
                            currentSearch = query,
                            listFlightsAutocomplete = mappedToFlights,
                            isAutoCompleteVisible = isAutoCompleteVisible, // si NO está vacío, TRUE
                            showFavoriteList = !isAutoCompleteVisible // si está vacío, TRUE
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /* ******* LISTA DE VUELOS *******  Se añade los datos del aeropuerto seleccionado a uiState,
    para mostrarlos directamente como DEPART y se recoge lista de vuelos de la BBDD menos el
     seleccionado para mostrarlos en ARRIVE */
    private fun getSelectedFlight(departureCode: String) =
        getSelectedFlightUseCase(departureCode)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun convertAirportSelectedToCommon(departureCode: String, departureName: String) {
        viewModelScope.launch {
            try {
                getSelectedFlight(departureCode).collect { list ->
                    val mappedToFlight = list.map {
                        ConvertFlightsToCommonListUseCase
                            .selected(it, departureCode, departureName)
                    }
                    _uiState.update {
                        it.copy(
                            listFlightsSelected = mappedToFlight,
                            currentDepartureCode = departureCode,
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* ******** FAVORITOS ******* RECOGE vuelvos favoritos de la BBDD con nombres de vuelos
     insertados desde el DAO */
    private fun getFavoriteFlight() =
        getFavoriteFlightsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /* ******** FAVORITOS ******* CONVIERTEN vuelvos favoritos de la BBDD */
    private fun convertFavoriteToCommon() {
        viewModelScope.launch {
            try {
                getFavoriteFlight().collect { list ->
                    _uiState.update {
                        it.copy(listFlightsFavorite = list)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* ******** FAVORITOS ******* se AÑADEN y BORRAN los vuelvos favoritos a la BBDD */
    fun insertOrDelete(isFavorite: Boolean, departureCode: String, destinationCode: String) {
        viewModelScope.launch {
            try {
                if (isFavorite) deleteFavoriteFlightUseCase(departureCode, destinationCode)
                else insertFavoriteFlightUseCase(departureCode, destinationCode)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Muestra una la lista según corresponda
    fun favoriteOrSelected() =
        if (_uiState.value.showFavoriteList) _uiState.value.listFlightsFavorite
        else _uiState.value.listFlightsSelected

    // Cambio de vistas entre Autocomplete y vuelos
    fun setAutoCompleteVisibility(isVisible: Boolean) {
        _uiState.update {
            it.copy(isAutoCompleteVisible = isVisible, showFavoriteList = isVisible)
        }
    }

    //Selector de texto para tipo de lista
    fun textToShow(): String = when {
        _uiState.value.showFavoriteList -> "Favorite routes"
        else -> "Flights from ${_uiState.value.currentDepartureCode}"
    }

    // Factory
    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlyApplication)
                FlyAppViewModel(
                    GetFavoriteFlightsUseCase(application.container.flyRepository),
                    InsertFavoriteFlightUseCase(application.container.flyRepository),
                    DeleteFavoriteFlightUseCase(application.container.flyRepository),
                    GetFlightsSearchCurrentUseCase(application.container.flyRepository),
                    GetSelectedFlightUseCase(application.container.flyRepository),
                    application.userPreference
                )
            }
        }

    }
}

data class UiState(
    val showFavoriteList: Boolean = true,
    val isAutoCompleteVisible: Boolean = false,
    val listFlightsAutocomplete: List<Flight> = emptyList(),
    val listFlightsSelected: List<Flight> = emptyList(),
    val listFlightsFavorite: List<Flight> = emptyList(),
    val currentSearch: String = "",
    val currentDepartureCode: String = "",
)

