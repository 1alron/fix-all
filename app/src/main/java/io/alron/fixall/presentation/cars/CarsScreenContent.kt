package io.alron.fixall.presentation.cars

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.alron.fixall.BuildConfig
import io.alron.fixall.R
import io.alron.fixall.domain.model.Car
import io.alron.fixall.presentation.components.MainToolbar

@Composable
fun CarsScreenContent(
    onAccountIconClick: () -> Unit,
    onBurgerIconClick: () -> Unit,
    cars: List<Car>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        MainToolbar(
            text = stringResource(R.string.my_cars),
            onBurgerIconClick,
            onAccountIconClick = onAccountIconClick
        )
        Spacer(Modifier.height(20.dp))
        if (cars.isNotEmpty()) {
            CarsContent(
                cars = cars
            )
        } else {
            Column(Modifier.fillMaxSize()) {
                Text(
                    stringResource(R.string.you_have_no_cars),
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {}
                ) {
                    Text("Добавить автомобиль")
                }
            }
        }
    }
}

@Composable
fun CarsContent(
    cars: List<Car>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        cars.forEach { car -> CarItem(car) }
    }
}

@Composable
fun CarItem(car: Car) {
    Text(car.toString())
    AsyncImage(
        model = "${BuildConfig.BASE_URL}${car.photoUrl}",
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}