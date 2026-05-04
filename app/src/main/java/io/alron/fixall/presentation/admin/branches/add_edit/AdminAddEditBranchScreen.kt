package io.alron.fixall.presentation.admin.branches.add_edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import io.alron.fixall.BuildConfig
import io.alron.fixall.domain.model.AdminWorkingHour
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddEditBranchScreen(
    onBack: () -> Unit,
    viewModel: AdminAddEditBranchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = File(context.cacheDir, "temp_branch_photo.jpg")
            context.contentResolver.openInputStream(it)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            viewModel.onPhotoSelected(file)
        }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.id == null) "Новый филиал" else "Редактирование") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = viewModel::save) {
                            Icon(Icons.Default.Check, contentDescription = "Сохранить")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (state.error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp).clickable { photoLauncher.launch("image/*") },
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val photoModel = state.selectedPhotoFile ?: state.photoUrl?.let { url ->
                        if (url.startsWith("http")) url else "${BuildConfig.BASE_URL}${url.removePrefix("/")}"
                    }

                    if (photoModel != null) {
                        AsyncImage(
                            model = photoModel,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(48.dp))
                            Text("Выбрать фото")
                        }
                    }
                }
            }

            OutlinedTextField(
                value = state.address,
                onValueChange = viewModel::onAddressChange,
                label = { Text("Адрес") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Телефон") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            OutlinedTextField(
                value = state.openingHours,
                onValueChange = viewModel::onOpeningHoursChange,
                label = { Text("Краткое время работы") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Пн-Пт: 9:00-20:00") }
            )

            Text("График работы", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            WorkingHoursTable(
                workingHours = state.workingHours,
                onHourChange = viewModel::onWorkingHourChange
            )

            if (state.id == null) {
                Text("Услуги филиала", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                
                SelectableServicesTable(
                    services = state.selectableServices,
                    onToggle = viewModel::onServiceToggle,
                    onPriceChange = viewModel::onServicePriceChange,
                    onDurationChange = viewModel::onServiceDurationChange
                )
            }
            
            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Сохранить филиал")
                }
            }
        }
    }
}

@Composable
fun SelectableServicesTable(
    services: List<SelectableService>,
    onToggle: (Int, Boolean) -> Unit,
    onPriceChange: (Int, String) -> Unit,
    onDurationChange: (Int, Int) -> Unit
) {
    if (services.isEmpty()) {
        Text("Список услуг пуст", style = MaterialTheme.typography.bodyMedium)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            services.forEachIndexed { index, service ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (service.isSelected) 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else 
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.SpaceBetween, 
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = service.name, 
                                style = MaterialTheme.typography.titleMedium, 
                                fontWeight = FontWeight.Bold, 
                                modifier = Modifier.weight(1f)
                            )
                            Checkbox(
                                checked = service.isSelected,
                                onCheckedChange = { onToggle(index, it) }
                            )
                        }
                        
                        if (service.isSelected) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = service.price,
                                    onValueChange = { onPriceChange(index, it) },
                                    label = { Text("Цена (₽)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = service.duration.toString(),
                                    onValueChange = { val d = it.toIntOrNull() ?: 0; onDurationChange(index, d) },
                                    label = { Text("Длит. (мин)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkingHoursTable(
    workingHours: List<AdminWorkingHour>,
    onHourChange: (Int, AdminWorkingHour) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        workingHours.forEachIndexed { index, wh ->
            WorkingHourRow(wh) { updated -> onHourChange(index, updated) }
            if (index < workingHours.size - 1) HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkingHourRow(
    wh: AdminWorkingHour,
    onUpdate: (AdminWorkingHour) -> Unit
) {
    var showTimePicker by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = wh.dayDisplay ?: "День ${wh.dayOfWeek}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Рабочий", style = MaterialTheme.typography.bodySmall)
                Checkbox(checked = wh.isWorking, onCheckedChange = { onUpdate(wh.copy(isWorking = it)) })
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TimeField(label = "Откр.", time = wh.startTime, onClick = { showTimePicker = "start" }, modifier = Modifier.weight(1f), enabled = wh.isWorking)
            TimeField(label = "Закр.", time = wh.endTime, onClick = { showTimePicker = "end" }, modifier = Modifier.weight(1f), enabled = wh.isWorking)
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TimeField(label = "Обед с", time = wh.lunchStart ?: "—", onClick = { showTimePicker = "lunch_start" }, modifier = Modifier.weight(1f), enabled = wh.isWorking)
            TimeField(label = "Обед до", time = wh.lunchEnd ?: "—", onClick = { showTimePicker = "lunch_end" }, modifier = Modifier.weight(1f), enabled = wh.isWorking)
        }
    }

    if (showTimePicker != null) {
        val initialTime = when(showTimePicker) {
            "start" -> wh.startTime
            "end" -> wh.endTime
            "lunch_start" -> wh.lunchStart ?: "13:00"
            "lunch_end" -> wh.lunchEnd ?: "14:00"
            else -> "00:00"
        }
        val timeParts = initialTime.split(":")
        val state = rememberTimePickerState(
            initialHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0,
            initialMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0,
            is24Hour = true
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = null },
            confirmButton = {
                TextButton(onClick = {
                    val formatted = String.format(Locale.US, "%02d:%02d", state.hour, state.minute)
                    val updated = when(showTimePicker) {
                        "start" -> wh.copy(startTime = formatted)
                        "end" -> wh.copy(endTime = formatted)
                        "lunch_start" -> wh.copy(lunchStart = formatted)
                        "lunch_end" -> wh.copy(lunchEnd = formatted)
                        else -> wh
                    }
                    onUpdate(updated)
                    showTimePicker = null
                }) { Text("ОК") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = null }) { Text("Отмена") } },
            text = { TimePicker(state = state) }
        )
    }
}

@Composable
fun TimeField(label: String, time: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    OutlinedCard(
        onClick = if (enabled) onClick else ({}),
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        enabled = enabled
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f))
            Text(text = time.take(5), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f))
        }
    }
}
