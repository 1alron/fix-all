package io.alron.fixall.presentation.admin.appointments.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.alron.fixall.presentation.admin.appointments.getStatusColor
import io.alron.fixall.presentation.util.DateTimeUtils
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAppointmentDetailScreen(
    onBack: () -> Unit,
    viewModel: AdminAppointmentDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var showStatusDialog by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали записи") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refreshAppointment() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading && state.appointment == null) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (state.error != null && state.appointment == null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    state.appointment?.let { appointment ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Status Card
                            StatusCard(
                                status = appointment.status,
                                statusDisplay = appointment.statusDisplay,
                                onEditClick = { showStatusDialog = true }
                            )

                            // Info Blocks
                            InfoBlock(title = "Клиент") {
                                DetailItem(Icons.Default.Person, "${appointment.clientName ?: "Не указано"}")
                                DetailItem(Icons.Default.Email, "${appointment.clientEmail ?: "Почта не указана"}")
                            }

                            InfoBlock(title = "Автомобиль") {
                                DetailItem(Icons.Default.ShoppingCart, appointment.carInfo)
                                DetailItem(Icons.Default.Info, "VIN: ${appointment.vin ?: "не указан"}")
                            }

                            InfoBlock(title = "Услуга") {
                                DetailItem(Icons.Default.Build, "${appointment.serviceName} (${appointment.serviceDuration} мин.)")
                                DetailItem(Icons.Default.DateRange, "${appointment.scheduledDate} в ${appointment.scheduledTime}")
                                DetailItem(Icons.Default.Info, "Стоимость: ${String.format(Locale.getDefault(), "%.2f", appointment.totalPrice)} руб.")
                            }

                            // Payment Block
                            InfoBlock(title = "Оплата") {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (appointment.isPaid) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (appointment.isPaid) "Оплачено" else "Ожидает оплаты",
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = if (appointment.isPaid) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                    if (appointment.isPaid && appointment.paymentInfo != null) {
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = DateTimeUtils.formatFullDateTime(appointment.paymentInfo.paidAt),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Notes Block
                            InfoBlock(
                                title = "Примечания",
                                action = {
                                    TextButton(onClick = { showNoteDialog = true }) {
                                        Text("Добавить")
                                    }
                                }
                            ) {
                                Text(
                                    text = appointment.notes ?: "Нет примечаний",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            // Footer (Metadata)
                            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                Text(
                                    text = "Создана: ${DateTimeUtils.formatFullDateTime(appointment.createdAt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Обновлена: ${DateTimeUtils.formatFullDateTime(appointment.updatedAt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showStatusDialog) {
        StatusSelectionDialog(
            currentStatus = state.appointment?.status ?: "",
            onDismiss = { showStatusDialog = false },
            onConfirm = { 
                viewModel.changeStatus(it)
                showStatusDialog = false
            }
        )
    }

    if (showNoteDialog) {
        AddNoteDialog(
            onDismiss = { showNoteDialog = false },
            onConfirm = { 
                viewModel.addNote(it)
                showNoteDialog = false
            }
        )
    }
}

@Composable
fun InfoBlock(
    title: String,
    action: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                action?.invoke()
            }
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun StatusCard(
    status: String,
    statusDisplay: String,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = getStatusColor(status).copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Статус записи", style = MaterialTheme.typography.labelSmall, color = getStatusColor(status))
                Text(text = statusDisplay, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = getStatusColor(status))
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Изменить статус", tint = getStatusColor(status))
            }
        }
    }
}

@Composable
fun DetailItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun StatusSelectionDialog(
    currentStatus: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val statuses = mapOf(
        "SCHEDULED" to "Запланировано",
        "IN_PROGRESS" to "В работе",
        "COMPLETED" to "Завершено",
        "CANCELLED" to "Отменено"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите статус") },
        text = {
            Column {
                statuses.forEach { (key, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onConfirm(key) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = key == currentStatus, onClick = null)
                        Spacer(Modifier.width(12.dp))
                        Text(text = label)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}

@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var note by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить примечание") },
        text = {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Примечание админа") },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(note) }, enabled = note.isNotBlank()) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
