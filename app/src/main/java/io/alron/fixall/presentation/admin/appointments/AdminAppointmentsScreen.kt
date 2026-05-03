package io.alron.fixall.presentation.admin.appointments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.alron.fixall.domain.model.AdminAppointmentListItem
import io.alron.fixall.presentation.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAppointmentsScreen(
    onBack: () -> Unit,
    onAppointmentClick: (String) -> Unit,
    viewModel: AdminAppointmentsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showFilters by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshSilently()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Все записи") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = if (showFilters) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Фильтры"
                        )
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refreshAppointments() }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if (showFilters) {
                    FiltersBlock(
                        state = state,
                        onSearchChange = viewModel::onSearchChange,
                        onStatusChange = viewModel::onStatusChange,
                        onCenterChange = viewModel::onCenterChange,
                        onDateFromChange = viewModel::onDateFromChange,
                        onDateToChange = viewModel::onDateToChange,
                        onClearFilters = viewModel::clearFilters,
                        onApplySearch = viewModel::loadAppointments
                    )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.isLoading && state.appointments.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (state.error != null && state.appointments.isEmpty()) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else if (state.appointments.isEmpty() && !state.isLoading) {
                        Text(
                            text = "Записей не найдено",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "Найдено записей: ${state.appointments.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.appointments) { appointment ->
                                    AdminAppointmentCard(
                                        appointment = appointment,
                                        onClick = { onAppointmentClick(appointment.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBlock(
    state: AdminAppointmentsState,
    onSearchChange: (String) -> Unit,
    onStatusChange: (String?) -> Unit,
    onCenterChange: (String?) -> Unit,
    onDateFromChange: (String?) -> Unit,
    onDateToChange: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onApplySearch: () -> Unit
) {
    var showDatePickerFrom by remember { mutableStateOf(false) }
    var showDatePickerTo by remember { mutableStateOf(false) }

    val datePickerStateFrom = rememberDatePickerState()
    val datePickerStateTo = rememberDatePickerState()

    if (showDatePickerFrom) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerFrom = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerStateFrom.selectedDateMillis?.let {
                        onDateFromChange(DateTimeUtils.millisToApiDate(it))
                    }
                    showDatePickerFrom = false
                }) { Text("ОК") }
            }
        ) { DatePicker(state = datePickerStateFrom) }
    }

    if (showDatePickerTo) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerTo = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerStateTo.selectedDateMillis?.let {
                        onDateToChange(DateTimeUtils.millisToApiDate(it))
                    }
                    showDatePickerTo = false
                }) { Text("ОК") }
            }
        ) { DatePicker(state = datePickerStateTo) }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = state.search,
                onValueChange = onSearchChange,
                label = { Text("Поиск (клиент, авто)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            var centerExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = centerExpanded,
                onExpandedChange = { centerExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.branches.find { it.id == state.centerId }?.address ?: "Все филиалы",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Филиал") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = centerExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = centerExpanded,
                    onDismissRequest = { centerExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Все филиалы") },
                        onClick = { onCenterChange(null); centerExpanded = false }
                    )
                    state.branches.forEach { branch ->
                        DropdownMenuItem(
                            text = { Text(branch.address) },
                            onClick = { onCenterChange(branch.id); centerExpanded = false }
                        )
                    }
                }
            }

            val statuses = mapOf(
                "SCHEDULED" to "Запланировано",
                "IN_PROGRESS" to "В работе",
                "COMPLETED" to "Завершено",
                "CANCELLED" to "Отменено"
            )
            var statusExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = it }
            ) {
                OutlinedTextField(
                    value = statuses[state.status] ?: "Все статусы",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Статус") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Все статусы") },
                        onClick = { onStatusChange(null); statusExpanded = false }
                    )
                    statuses.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = { onStatusChange(key); statusExpanded = false }
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = DateTimeUtils.formatDate(state.dateFrom),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("С") },
                    modifier = Modifier.weight(1f).clickable { showDatePickerFrom = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                OutlinedTextField(
                    value = DateTimeUtils.formatDate(state.dateTo),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("По") },
                    modifier = Modifier.weight(1f).clickable { showDatePickerTo = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApplySearch,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Найти")
                }
                Button(
                    onClick = onClearFilters,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Сбросить")
                }
            }
        }
    }
}

@Composable
fun AdminAppointmentCard(
    appointment: AdminAppointmentListItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${appointment.scheduledDate} в ${appointment.scheduledTime}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Surface(
                        color = getStatusColor(appointment.status).copy(alpha = 0.1f),
                        contentColor = getStatusColor(appointment.status),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = appointment.statusDisplay,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(12.dp))

            AdminAppointmentDetailItem(
                icon = Icons.Default.Person, 
                label = "Клиент", 
                value = "${appointment.clientName ?: "Не указано"}\n${appointment.clientEmail ?: "Почта не указана"}"
            )
            AdminAppointmentDetailItem(icon = Icons.Default.ShoppingCart, label = "Автомобиль", value = appointment.carInfo)
            AdminAppointmentDetailItem(icon = Icons.Default.Build, label = "Услуга", value = appointment.serviceName)
            AdminAppointmentDetailItem(icon = Icons.Default.Place, label = "Филиал", value = appointment.centerAddress)
        }
    }
}

@Composable
fun AdminAppointmentDetailItem(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

fun getStatusColor(status: String): androidx.compose.ui.graphics.Color {
    return when (status) {
        "SCHEDULED" -> androidx.compose.ui.graphics.Color(0xFF2196F3)
        "IN_PROGRESS" -> androidx.compose.ui.graphics.Color(0xFFFF9800)
        "COMPLETED" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        "CANCELLED" -> androidx.compose.ui.graphics.Color(0xFFF44336)
        else -> androidx.compose.ui.graphics.Color.Gray
    }
}
