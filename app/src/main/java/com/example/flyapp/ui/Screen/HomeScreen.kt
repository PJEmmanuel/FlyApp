package com.example.flyapp.ui.Screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.flyapp.R
import com.example.flyapp.domain.model.Flight

@Composable
fun MainScreen(
    currentListFly: List<Flight>,
    insertOrDeleteFavorite: (Boolean, String, String) -> Unit,
    textToShow: String,
    airportCodeSelected: (String, String) -> Unit,
    setAutoCompleteVisibility: (Boolean) -> Unit,
    isAutoCompleteVisible: Boolean,
    airportList: List<Flight>,
    onValueChange: (String) -> Unit,
    currentSearch: String,
    modifier: Modifier = Modifier
) {

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SearchBarFlyApp(
                currentSearch = currentSearch,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
            AutocompleteFlyApp(
                airportList = airportList,
                isAutoCompleteVisible = isAutoCompleteVisible,
                setAutoCompleteVisibility = setAutoCompleteVisibility,
                airportCodeSelected = airportCodeSelected,
                modifier = Modifier.padding(16.dp)
            )
            ShowTextCurrentScreen(textToShow = textToShow, Modifier.padding(16.dp))

            ListFlyApp(
                currentListFly = currentListFly,
                insertOrDeleteFavorite = insertOrDeleteFavorite,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun ListFlyApp(
    currentListFly: List<Flight>,
    insertOrDeleteFavorite: (Boolean, String, String) -> Unit,
    modifier: Modifier
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier,
        state = rememberLazyGridState(),
    ) {
        items(currentListFly) { list ->
            FlyAppCard(
                itemSelectedFly = list,
                onClickStar = {
                    insertOrDeleteFavorite(
                        list.isFavorite,
                        list.departureCode,
                        list.destinationCode
                    )
                },
                selectedFavorite = list.isFavorite,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun FlyAppCard(
    itemSelectedFly: Flight,
    onClickStar: () -> Unit,
    selectedFavorite: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        shape = AbsoluteCutCornerShape(
            topRight = 16.dp,
            bottomRight = 0.dp,
            topLeft = 0.dp,
            bottomLeft = 0.dp
        ),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.depart))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(itemSelectedFly.departureCode)
                    }
                    append("  ${itemSelectedFly.departureName}")
                })
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.arrive))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(itemSelectedFly.destinationCode)
                        }
                        append("  ${itemSelectedFly.destinationName}")
                    }
                )
            }

            IconButton(
                onClick = onClickStar,
                modifier = Modifier
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (selectedFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}


@Composable
fun SearchBarFlyApp(
    currentSearch: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    OutlinedTextField(
        value = currentSearch,
        onValueChange = { onValueChange(it) },
        modifier = modifier,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
        shape = RoundedCornerShape(32.dp)
    )
}

@Composable
fun AutocompleteFlyApp(
    isAutoCompleteVisible: Boolean,
    setAutoCompleteVisibility: (Boolean) -> Unit,
    airportList: List<Flight>,
    airportCodeSelected: (String, String) -> Unit,
    modifier: Modifier,
) {
    AnimatedVisibility(
        visible = isAutoCompleteVisible,
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            items(airportList) { airport ->
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(airport.destinationCode)
                        }
                        append("  ${airport.destinationName}")
                    },
                    modifier = Modifier.clickable {
                        airportCodeSelected(
                            airport.destinationCode,
                            airport.destinationName
                        )
                        setAutoCompleteVisibility(false)
                    }
                )
            }
        }
    }
}

@Composable
fun ShowTextCurrentScreen(textToShow: String, modifier: Modifier) {
    Text(text = textToShow, fontWeight = FontWeight.Bold, modifier = modifier)
}