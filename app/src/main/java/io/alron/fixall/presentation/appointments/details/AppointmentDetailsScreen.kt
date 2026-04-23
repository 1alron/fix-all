package io.alron.fixall.presentation.appointments.details

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.alron.fixall.R
import io.alron.fixall.domain.model.Appointment
import io.alron.fixall.domain.model.LoyaltyInfo
import io.alron.fixall.presentation.appointments.getStatusColor
import io.alron.fixall.presentation.components.MainToolbar
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailsScreen(
    onBack: () -> Unit,
    viewModel: AppointmentDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.events) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is AppointmentDetailsEvent.OpenPaymentUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                    context.startActivity(intent)
                }
                is AppointmentDetailsEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                AppointmentDetailsEvent.AppointmentCancelled -> {
                    onBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            MainToolbar(
                title = "Детали записи",
                onNavigationIconClick = onBack,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            isRefreshing = state.isLoading && state.appointment != null,
            onRefresh = { state.appointment?.id?.let { viewModel.getDetails(it) } }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading && state.appointment == null) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (state.appointment != null) {
                    val appointment = state.appointment!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = getStatusColor(appointment.status).copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = getStatusColor(appointment.status))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Статус записи", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        appointment.statusDisplay,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = getStatusColor(appointment.status)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        if (appointment.status != "CANCELLED") {
                            if (appointment.isPaid) {
                                PaidStatusCard(appointment = appointment)
                            } else if (appointment.status == "SCHEDULED") {
                                PaymentBlock(
                                    appointment = appointment,
                                    loyaltyInfo = state.loyaltyInfo,
                                    onPayClick = { bonus -> viewModel.initiatePayment(bonus) },
                                    isPaying = state.isPaying
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        DetailItem(Icons.Default.Build, "Услуга", appointment.serviceType.name)
                        DetailItem(Icons.Default.Place, "Филиал", appointment.serviceCenter.address)
                        DetailItem(Icons.Default.DateRange, "Дата и время", "${appointment.scheduledDate} в ${appointment.scheduledTime}")
                        DetailItem(Icons.Default.Settings, "Автомобиль", "${appointment.car.brandName} ${appointment.car.modelName}")

                        if (!appointment.notes.isNullOrBlank()) {
                            DetailItem(Icons.Default.Info, "Комментарий", appointment.notes)
                        }

                        Spacer(Modifier.height(32.dp))

                        if (appointment.status == "SCHEDULED") {
                            OutlinedButton(
                                onClick = { viewModel.cancelAppointment() },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                enabled = !state.isCancelling
                            ) {
                                if (state.isCancelling) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.error)
                                } else {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                    Spacer(Modifier.width(12.dp))
                                    Text("Отменить запись", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PaidStatusCard(appointment: Appointment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFF2E7D32).copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Оплачено онлайн",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "${appointment.totalPrice} ₽",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1B5E20),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            appointment.paymentDate?.let {
                Text(
                    text = "Платеж выполнен $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1B5E20).copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PaymentBlock(
    appointment: Appointment,
    loyaltyInfo: LoyaltyInfo?,
    onPayClick: (Double) -> Unit,
    isPaying: Boolean
) {
    var bonusInput by remember { mutableStateOf("0") }
    val basePrice = appointment.totalPrice.toDoubleOrNull() ?: 0.0
    val discountPercent = loyaltyInfo?.totalDiscount ?: 0.0
    val discountAmount = basePrice * (discountPercent / 100.0)
    val priceAfterDiscount = basePrice - discountAmount
    
    val availableBonuses = loyaltyInfo?.bonusBalance ?: 0.0
    val maxUsableBonuses = minOf(availableBonuses, priceAfterDiscount * 0.7)
    
    val currentBonusUsed = bonusInput.replace(',', '.').toDoubleOrNull() ?: 0.0
    val finalPrice = maxOf(0.0, priceAfterDiscount - currentBonusUsed)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
            Text("Оплата онлайн", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(16.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                PriceRow("Базовая цена:", String.format(Locale.getDefault(), "%.0f ₽", basePrice))
                
                if (discountPercent > 0) {
                    PriceRow(
                        label = "Скидка (${loyaltyInfo?.statusDisplay}: ${discountPercent.toInt()}%):",
                        value = String.format(Locale.getDefault(), "-%.2f ₽", discountAmount),
                        color = Color(0xFF2E7D32)
                    )
                }
                
                PriceRow("Доступно бонусов:", String.format(Locale.getDefault(), "%.2f ₽", availableBonuses), color = MaterialTheme.colorScheme.primary)
                PriceRow("Можно использовать (макс 70%):", String.format(Locale.getDefault(), "до %.2f ₽", maxUsableBonuses), isSmall = true)
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Итого к оплате:", fontWeight = FontWeight.Bold)
                    Text(String.format(Locale.getDefault(), "%.2f ₽", finalPrice), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = bonusInput,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*[.,]?\\d*$"))) {
                        val value = input.replace(',', '.').toDoubleOrNull() ?: 0.0
                        if (value <= maxUsableBonuses) {
                            bonusInput = input
                        }
                    }
                },
                label = { Text("Использовать бонусы") },
                placeholder = { Text("0") },
                suffix = { Text("₽") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = { bonusInput = String.format(Locale.US, "%.2f", maxUsableBonuses) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Использовать максимум (${String.format(Locale.getDefault(), "%.2f", maxUsableBonuses)} ₽)")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { onPayClick(currentBonusUsed) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                enabled = !isPaying
            ) {
                if (isPaying) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text("Оплатить ${String.format(Locale.getDefault(), "%.2f", finalPrice)} ₽", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PriceRow(label: String, value: String, color: Color = Color.Unspecified, isSmall: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = if (isSmall) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium)
        Text(value, style = if (isSmall) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun DetailItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}
