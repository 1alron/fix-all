package io.alron.fixall.presentation.profile.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.alron.fixall.R
import io.alron.fixall.presentation.components.MainToolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onBack: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MainToolbar(
                title = stringResource(R.string.stats_title),
                onNavigationIconClick = onBack,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage!!,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                state.stats != null -> {
                    val stats = state.stats!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatOverviewCard(
                                label = stringResource(R.string.total_appointments),
                                value = stats.totalAppointments.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            StatOverviewCard(
                                label = stringResource(R.string.spent_r),
                                value = "${stats.totalSpent.toInt()} ₽",
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        StatCard(title = stringResource(R.string.visits_by_day)) {
                            val days = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
                            val max = stats.byWeekday.maxOrNull()?.coerceAtLeast(1) ?: 1
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                stats.byWeekday.forEachIndexed { index, count ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier.height(20.dp),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            if (count > 0) {
                                                Text(
                                                    text = count.toString(),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .width(24.dp)
                                                .fillMaxHeight(
                                                    (count.toFloat() / max).coerceAtLeast(
                                                        0.05f
                                                    )
                                                )
                                                .clip(
                                                    RoundedCornerShape(
                                                        topStart = 4.dp,
                                                        topEnd = 4.dp
                                                    )
                                                )
                                                .background(
                                                    brush = Brush.verticalGradient(
                                                        colors = listOf(
                                                            MaterialTheme.colorScheme.primary,
                                                            MaterialTheme.colorScheme.primary.copy(
                                                                alpha = 0.4f
                                                            )
                                                        )
                                                    )
                                                )
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            days[index],
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }

                        StatCard(title = stringResource(R.string.visits_by_hour)) {
                            val maxHour = stats.byHour.maxOrNull()?.coerceAtLeast(1) ?: 1
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    stats.byHour.forEach { count ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight(
                                                    (count.toFloat() / maxHour).coerceAtLeast(
                                                        0.05f
                                                    )
                                                )
                                                .clip(RoundedCornerShape(1.dp))
                                                .background(
                                                    if (count > 0) MaterialTheme.colorScheme.secondary
                                                    else MaterialTheme.colorScheme.secondary.copy(
                                                        alpha = 0.1f
                                                    )
                                                )
                                        )
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    listOf("00", "06", "12", "18", "23").forEach {
                                        Text(
                                            it,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }

                        StatCard(title = stringResource(R.string.popular_services)) {
                            val maxCount =
                                stats.topServices.firstOrNull()?.count?.coerceAtLeast(1) ?: 1
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                stats.topServices.take(3).forEach { service ->
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                service.name,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                "${service.count}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { service.count.toFloat() / maxCount },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(CircleShape),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatOverviewCard(
                                label = stringResource(R.string.finished),
                                value = stats.completedAppointments.toString(),
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.weight(1f)
                            )
                            StatOverviewCard(
                                label = stringResource(R.string.stat_cancelled),
                                value = stats.cancelledAppointments.toString(),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatOverviewCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
    }
}

@Composable
fun StatCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.3f
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}
