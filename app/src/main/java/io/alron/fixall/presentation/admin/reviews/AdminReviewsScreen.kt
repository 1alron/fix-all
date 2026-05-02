package io.alron.fixall.presentation.admin.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.alron.fixall.domain.model.AdminReviewListItem
import io.alron.fixall.presentation.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReviewsScreen(
    onBack: () -> Unit,
    viewModel: AdminReviewsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showFilters by remember { mutableStateOf(false) }
    
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var showReplyDialog by remember { mutableStateOf<AdminReviewListItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отзывы") },
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
            modifier = Modifier.fillMaxSize().padding(padding),
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refreshReviews() }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (showFilters) {
                    ReviewsFiltersBlock(
                        state = state,
                        onSearchChange = viewModel::onSearchChange,
                        onRatingChange = viewModel::onRatingChange,
                        onCenterChange = viewModel::onCenterChange,
                        onUnansweredChange = viewModel::onUnansweredOnlyChange,
                        onClearFilters = viewModel::clearFilters,
                        onApply = viewModel::loadReviews
                    )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.isLoading && state.reviews.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (state.reviews.isEmpty()) {
                        Text(text = "Отзывов не найдено", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Найдено отзывов: ${state.reviews.size}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            items(state.reviews) { review ->
                                AdminReviewCard(
                                    review = review,
                                    onReplyClick = { showReplyDialog = review },
                                    onDeleteClick = { showDeleteDialog = review.id }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    showDeleteDialog?.let { id ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Удалить отзыв?") },
            text = { Text("Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.deleteReview(id)
                    showDeleteDialog = null
                }) { Text("Удалить", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Отмена") }
            }
        )
    }

    showReplyDialog?.let { review ->
        var replyText by remember { mutableStateOf(review.adminReply ?: "") }
        AlertDialog(
            onDismissRequest = { showReplyDialog = null },
            title = { Text(if (review.adminReply == null) "Ответить на отзыв" else "Редактировать ответ") },
            text = {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    placeholder = { Text("Введите ваш ответ...") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.replyToReview(review.id, replyText)
                    showReplyDialog = null
                }) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { showReplyDialog = null }) { Text("Отмена") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsFiltersBlock(
    state: AdminReviewsState,
    onSearchChange: (String) -> Unit,
    onRatingChange: (Int?) -> Unit,
    onCenterChange: (String?) -> Unit,
    onUnansweredChange: (Boolean) -> Unit,
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
                label = { Text("Поиск (имя, текст)") },
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                var ratingExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = ratingExpanded, onExpandedChange = { ratingExpanded = it }, modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = state.rating?.toString() ?: "Любая",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Оценка") },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = ratingExpanded, onDismissRequest = { ratingExpanded = false }) {
                        DropdownMenuItem(text = { Text("Любая") }, onClick = { onRatingChange(null); ratingExpanded = false })
                        (1..5).forEach { r ->
                            DropdownMenuItem(text = { Text(r.toString()) }, onClick = { onRatingChange(r); ratingExpanded = false })
                        }
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1.2f)) {
                    Checkbox(checked = state.unansweredOnly, onCheckedChange = onUnansweredChange)
                    Text("Без ответа", style = MaterialTheme.typography.bodySmall)
                }
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
fun AdminReviewCard(
    review: AdminReviewListItem,
    onReplyClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = DateTimeUtils.formatFullDateTime(review.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(text = review.userName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Text(text = review.centerAddress, style = MaterialTheme.typography.bodySmall)
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                repeat(5) { index ->
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (index < review.rating) Color(0xFFFFB400) else MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(text = review.comment ?: "Без комментария", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(16.dp))

            if (review.adminReply != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Ваш ответ:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            IconButton(onClick = onReplyClick, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Редактировать", modifier = Modifier.size(16.dp))
                            }
                        }
                        Text(text = review.adminReply, style = MaterialTheme.typography.bodySmall)
                        review.adminReplyAt?.let {
                            Text(
                                text = DateTimeUtils.formatFullDateTime(it),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onReplyClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ответить")
                }
            }
        }
    }
}
