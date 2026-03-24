package io.alron.fixall.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info

object StepProvider {
    fun provideSteps() = listOf(
        Step(
            vect = Icons.Default.Info,
            title = "Выберите филиал",
            description = "Найдите удобный адрес и время"
        ),
        Step(
            vect = Icons.Default.Info,
            title = "Запишитесь онлайн",
            description = "Выберите услугу, дату и время"
        ),
        Step(
            vect = Icons.Default.Info,
            title = "Приезжайте вовремя",
            description = "Мы все подготовим заранее"
        )
    )
}