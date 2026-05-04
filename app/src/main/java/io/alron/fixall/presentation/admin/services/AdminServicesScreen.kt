package io.alron.fixall.presentation.admin.services

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.alron.fixall.domain.model.AdminService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServicesScreen(
    onBack: () -> Unit,
    viewModel: AdminServicesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showFilters by remember { mutableStateOf(false) }
    var showAddEditDialog by remember { mutableStateOf<AdminService?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Услуги") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(if (showFilters) Icons.Default.Close else Icons.Default.Search, contentDescription = "Фильтры")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить услугу")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(padding),
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refreshServices() }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (showFilters) {
                    ServicesFiltersBlock(
                        state = state,
                        onSearchChange = viewModel::onSearchChange,
                        onActiveOnlyChange = viewModel::onActiveOnlyChange,
                        onCenterChange = viewModel::onCenterChange,
                        onClearFilters = viewModel::clearFilters,
                        onApply = viewModel::loadServices
                    )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.isLoading && state.services.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (state.services.isEmpty()) {
                        Text(text = "Услуг не найдено", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Найдено услуг: ${state.services.size}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            items(state.services) { service ->
                                AdminServiceCard(
                                    service = service,
                                    onEditClick = { showAddEditDialog = service },
                                    onDeleteClick = { showDeleteDialog = service.id },
                                    onToggleActive = { viewModel.toggleActive(service.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        ServiceAddEditDialog(
            branches = state.branches,
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, desc, dur, price, centerId, active ->
                viewModel.createService(name, desc, dur, price, centerId, active)
                showCreateDialog = false
            }
        )
    }

    showAddEditDialog?.let { service ->
        ServiceAddEditDialog(
            service = service,
            branches = state.branches,
            onDismiss = { showAddEditDialog = null },
            onConfirm = { name, desc, dur, price, centerId, active ->
                viewModel.updateService(service.id, name, desc, dur, price, centerId, active)
                showAddEditDialog = null
            }
        )
    }

    showDeleteDialog?.let { id ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Удалить услугу?") },
            text = { Text("Это действие нельзя будет отменить.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteService(id); showDeleteDialog = null }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Отмена") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesFiltersBlock(
    state: AdminServicesState,
    onSearchChange: (String) -> Unit,
    onActiveOnlyChange: (Boolean) -> Unit,
    onCenterChange: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onApply: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = state.search,
                onValueChange = onSearchChange,
                label = { Text("Название услуги") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            var centerExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = centerExpanded, onExpandedChange = { centerExpanded = it }) {
                OutlinedTextField(
                    value = state.branches.find { it.id == state.centerId }?.address ?: "Все филиалы",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Филиал") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = centerExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = centerExpanded, onDismissRequest = { centerExpanded = false }) {
                    DropdownMenuItem(text = { Text("Все филиалы") }, onClick = { onCenterChange(null); centerExpanded = false })
                    state.branches.forEach { branch ->
                        DropdownMenuItem(text = { Text(branch.address) }, onClick = { onCenterChange(branch.id); centerExpanded = false })
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = state.isActiveOnly, onCheckedChange = onActiveOnlyChange)
                Text("Только активные", style = MaterialTheme.typography.bodyMedium)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onApply, modifier = Modifier.weight(1f)) { Text("Найти") }
                Button(
                    onClick = onClearFilters,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Сбросить") }
            }
        }
    }
}

@Composable
fun AdminServiceCard(
    service: AdminService,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleActive: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = service.centerAddress ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                Switch(checked = service.isActive, onCheckedChange = { onToggleActive() })
            }
            
            Spacer(Modifier.height(8.dp))
            Text(text = service.description ?: "Нет описания", style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row {
                    DetailChip("${service.duration} мин")
                    Spacer(Modifier.width(8.dp))
                    DetailChip("${service.price} ₽")
                }
                Row {
                    IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, contentDescription = "Редактировать", tint = MaterialTheme.colorScheme.primary) }
                    IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@Composable
fun DetailChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceAddEditDialog(
    service: AdminService? = null,
    branches: List<io.alron.fixall.domain.model.Branch>,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, Int, String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(service?.name ?: "") }
    var desc by remember { mutableStateOf(service?.description ?: "") }
    var duration by remember { mutableStateOf(service?.duration?.toString() ?: "60") }
    var price by remember { mutableStateOf(service?.price ?: "") }
    var selectedCenterId by remember { mutableStateOf(service?.serviceCenterId ?: branches.firstOrNull()?.id ?: "") }
    var isActive by remember { mutableStateOf(service?.isActive ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (service == null) "Создать услугу" else "Редактировать услугу") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Название") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Описание") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Мин.") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Цена") }, modifier = Modifier.weight(1f))
                }
                
                var branchExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = branchExpanded, onExpandedChange = { branchExpanded = it }) {
                    OutlinedTextField(
                        value = branches.find { it.id == selectedCenterId }?.address ?: "Выберите филиал",
                        onValueChange = {}, readOnly = true, label = { Text("Филиал") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = branchExpanded, onDismissRequest = { branchExpanded = false }) {
                        branches.forEach { branch ->
                            DropdownMenuItem(text = { Text(branch.address) }, onClick = { selectedCenterId = branch.id; branchExpanded = false })
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isActive, onCheckedChange = { isActive = it })
                    Text("Активна")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, desc, duration.toIntOrNull() ?: 0, price, selectedCenterId, isActive) },
                enabled = name.isNotBlank() && price.isNotBlank() && selectedCenterId.isNotBlank()
            ) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
