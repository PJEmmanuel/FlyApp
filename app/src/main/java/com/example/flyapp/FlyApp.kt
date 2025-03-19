package com.example.flyapp

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flyapp.ui.Screen.FlyAppViewModel
import com.example.flyapp.ui.Screen.MainScreen

@Composable
fun FlyApp(viewModel: FlyAppViewModel = viewModel(factory = FlyAppViewModel.factory)) {

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            FlyAppTopAppBar()
        }
    ) { innerPadding ->
        MainScreen(
            currentListFly = viewModel.favoriteOrSelected(),
            insertOrDeleteFavorite = viewModel::insertOrDelete,
            textToShow = viewModel.textToShow(),
            airportCodeSelected = viewModel::convertAirportSelectedToCommon,
            setAutoCompleteVisibility = { viewModel.setAutoCompleteVisibility(it) },
            isAutoCompleteVisible = uiState.isAutoCompleteVisible,
            airportList = uiState.listFlightsAutocomplete,
            onValueChange = {
                viewModel.convertAutocompleteToCommon(it)
                viewModel.saveSearchFlight(it)
            },
            currentSearch = uiState.currentSearch,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlyAppTopAppBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name), color = Color.White) },
        colors = TopAppBarDefaults.topAppBarColors(Color.DarkGray)
    )
}