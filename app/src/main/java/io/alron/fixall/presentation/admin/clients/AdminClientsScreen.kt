package io.alron.fixall.presentation.admin.clients

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.alron.fixall.data.remote.dto.AdminClientListItemDto
import io.alron.fixall.presentation.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminClientsScreen(
    onBack: () -> Unit,
    onAddClient: () -> Unit,
    onClientClick: (Int) -> Unit,
    viewModel: AdminClientsViewModel = hiltViewModel()
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
                title = { Text("Пользователи") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(if (showFilters) Icons.Default.Close else Icons.Default.Search, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClient) {
                Icon(Icons.Default.Add, contentDescription = "Добавить пользователя")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(padding),
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refreshClients() }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (showFilters) {
                    ClientFiltersBlock(
                        state = state,
                        onSearchChange = viewModel::onSearchChange,
                        onEmailChange = viewModel::onEmailChange,
                        onPhoneChange = viewModel::onPhoneChange,
                        onHasCarsToggle = viewModel::onHasCarsToggle,
                        onHasActiveToggle = viewModel::onHasActiveToggle,
                        onDateFromChange = viewModel::onDateFromChange,
                        onDateToChange = viewModel::onDateToChange,
                        onClear = viewModel::clearFilters,
                        onApply = viewModel::loadClients
                    )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.isLoading && state.clients.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (state.error != null && state.clients.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                            Button(onClick = { viewModel.loadClients() }) {
                                Text("Повторить")
                            }
                        }
                    } else if (state.clients.isEmpty() && !state.isLoading) {
                        Text("Пользователи не найдены", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Всего пользователей: ${state.totalCount}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            items(state.clients) { client ->
                                ClientListItem(
                                    client = client,
                                    onClick = { onClientClick(client.id) }
                                )
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
fun ClientFiltersBlock(
    state: AdminClientsState,
    onSearchChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onHasCarsToggle: (Boolean) -> Unit,
    onHasActiveToggle: (Boolean) -> Unit,
    onDateFromChange: (String?) -> Unit,
    onDateToChange: (String?) -> Unit,
    onClear: () -> Unit,
    onApply: () -> Unit
) {
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    val dateStateFrom = rememberDatePickerState()
    val dateStateTo = rememberDatePickerState()

    if (showFromPicker) {
        DatePickerDialog(
            onDismissRequest = { showFromPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateStateFrom.selectedDateMillis?.let {
                        onDateFromChange(DateTimeUtils.millisToApiDate(it))
                    }
                    showFromPicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = dateStateFrom) }
    }

    if (showToPicker) {
        DatePickerDialog(
            onDismissRequest = { showToPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateStateTo.selectedDateMillis?.let {
                        onDateToChange(DateTimeUtils.millisToApiDate(it))
                    }
                    showToPicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = dateStateTo) }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = state.search,
                onValueChange = onSearchChange,
                label = { Text("Поиск (логин, имя)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = state.phone,
                onValueChange = onPhoneChange,
                label = { Text("Телефон") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f).clickable { showFromPicker = true }) {
                    OutlinedTextField(
                        value = DateTimeUtils.formatDate(state.dateFrom),
                        onValueChange = {},
                        label = { Text("С даты") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                Box(modifier = Modifier.weight(1f).clickable { showToPicker = true }) {
                    OutlinedTextField(
                        value = DateTimeUtils.formatDate(state.dateTo),
                        onValueChange = {},
                        label = { Text("По дату") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = state.hasCars, onCheckedChange = onHasCarsToggle)
                Text("Только с авто", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(16.dp))
                Checkbox(checked = state.hasActive, onCheckedChange = onHasActiveToggle)
                Text("Только активные", style = MaterialTheme.typography.bodyMedium)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onApply, modifier = Modifier.weight(1f)) { Text("Найти") }
                OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) { Text("Сбросить") }
            }
        }
    }
}

@Composable
fun ClientListItem(
    client: AdminClientListItemDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = client.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "@${client.username}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                if (client.isStaff) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(4.dp)) {
                        Text("ADMIN", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

            ClientInfoRow(Icons.Default.Email, client.email ?: "—")
            ClientInfoRow(Icons.Default.Phone, client.phone ?: "—")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatChip(label = "Авто", count = client.carsCount)
                StatChip(label = "Записи", count = client.appointmentsCount)
                StatChip(label = "Активные", count = client.activeAppointmentsCount, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ClientInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun StatChip(label: String, count: Int, color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = count.toString(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
    }
}
