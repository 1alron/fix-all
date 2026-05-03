package io.alron.fixall.presentation.admin.clients.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.alron.fixall.domain.model.AdminClientAppointment
import io.alron.fixall.domain.model.AdminClientStatItem
import io.alron.fixall.presentation.admin.appointments.getStatusColor
import io.alron.fixall.presentation.admin.clients.ClientInfoRow
import io.alron.fixall.presentation.util.DateTimeUtils
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminClientDetailScreen(
    onBack: () -> Unit,
    onAppointmentClick: (String) -> Unit,
    viewModel: AdminClientDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пользователь @${state.client?.username ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (!state.isEditMode) {
                        IconButton(onClick = viewModel::toggleEditMode) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(padding),
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refreshAppointment() }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading && state.client == null) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (state.client != null) {
                    if (state.isEditMode) {
                        EditClientForm(
                            state = state,
                            viewModel = viewModel,
                            onCancel = viewModel::toggleEditMode
                        )
                    } else {
                        ClientDetailContent(
                            state = state,
                            onAppointmentClick = onAppointmentClick
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить пользователя?") },
            text = { Text("Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteClient(onBack); showDeleteDialog = false }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
fun ClientDetailContent(
    state: AdminClientDetailState,
    onAppointmentClick: (String) -> Unit
) {
    val client = state.client!!
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(text = client.fullName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(text = "@${client.username}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                ClientDetailItem(Icons.Default.Email, "Email", client.email)
                ClientDetailItem(Icons.Default.Phone, "Телефон", client.phone ?: "—")
                ClientDetailItem(Icons.Default.Place, "Адрес", client.address ?: "—")
                ClientDetailItem(Icons.Default.DateRange, "Зарегистрирован", client.dateJoined)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(modifier = Modifier.weight(1f), label = "Всего записей", value = client.appointmentsCount.toString())
            StatCard(modifier = Modifier.weight(1f), label = "Автомобилей", value = client.carsCount.toString())
            StatCard(modifier = Modifier.weight(1.2f), label = "Оплачено", value = String.format(Locale.getDefault(), "%.2f ₽", client.totalPaid))
        }

        Text("Недавние записи", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (client.recentAppointments.isEmpty()) {
            Text("Нет записей", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            client.recentAppointments.forEach { appt ->
                ClientAppointmentItem(appt, onClick = { onAppointmentClick(appt.id) })
            }
        }

        PopularItemsBlock(title = "Популярные услуги", items = client.topServices)
        PopularItemsBlock(title = "Популярные филиалы", items = client.topCenters)

        WeekdayStatsBlock(counts = client.weekdayCounts)
    }
}

@Composable
fun EditClientForm(
    state: AdminClientDetailState,
    viewModel: AdminClientDetailViewModel,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Редактирование профиля", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        
        OutlinedTextField(
            value = state.editUsername,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.formErrors.containsKey("username"),
            supportingText = state.formErrors["username"]?.let { { Text(it) } }
        )
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.editFirstName,
                onValueChange = viewModel::onFirstNameChange,
                label = { Text("Имя") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.editLastName,
                onValueChange = viewModel::onLastNameChange,
                label = { Text("Фамилия") },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = state.editEmail,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.formErrors.containsKey("email"),
            supportingText = state.formErrors["email"]?.let { { Text(it) } }
        )

        OutlinedTextField(
            value = state.editPhone,
            onValueChange = viewModel::onPhoneChange,
            label = { Text("Телефон") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.editAddress,
            onValueChange = viewModel::onAddressChange,
            label = { Text("Адрес") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = state.editIsStaff, onCheckedChange = viewModel::onIsStaffChange)
            Text("Права администратора", style = MaterialTheme.typography.bodyMedium)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = viewModel::updateClient, modifier = Modifier.weight(1f)) { Text("Сохранить") }
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Отмена") }
        }
    }
}

@Composable
fun ClientDetailItem(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, label: String, value: String) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        }
    }
}

@Composable
fun ClientAppointmentItem(appt: AdminClientAppointment, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                val formattedDate = DateTimeUtils.formatDate(appt.scheduledDate)
                val formattedTime = DateTimeUtils.formatTime(appt.scheduledTime)
                Text(text = "$formattedDate $formattedTime", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                Text(text = appt.serviceName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(text = appt.centerAddress, style = MaterialTheme.typography.bodySmall)
            }
            Surface(
                color = getStatusColor(appt.status).copy(alpha = 0.1f),
                contentColor = getStatusColor(appt.status),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(text = appt.statusDisplay, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun PopularItemsBlock(title: String, items: List<AdminClientStatItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (items.isEmpty()) {
            Text("Нет данных", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            val max = items.maxOf { it.count }.coerceAtLeast(1)
            items.forEach { item ->
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.name, style = MaterialTheme.typography.bodySmall)
                        Text(item.count.toString(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { item.count.toFloat() / max },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun WeekdayStatsBlock(counts: List<Int>) {
    val days = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Активность по дням недели", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (counts.isEmpty() || counts.all { it == 0 }) {
            Text("Нет данных", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            val max = (counts.maxOfOrNull { it } ?: 1).coerceAtLeast(1)
            counts.forEachIndexed { index, count ->
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(days.getOrNull(index) ?: "", style = MaterialTheme.typography.bodySmall)
                        Text(count.toString(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { count.toFloat() / max },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}
