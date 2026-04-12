package com.appfit.ui.debug

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfit.data.model.Activity
import com.appfit.data.model.ChatMessage
import com.appfit.data.model.ChatRole
import com.appfit.data.model.Meal
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DbViewerScreen(
    onBack: () -> Unit,
    viewModel: DbViewerViewModel = hiltViewModel()
) {
    val activities by viewModel.activities.collectAsStateWithLifecycle()
    val meals by viewModel.meals.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val debugLog by viewModel.debugLog.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Attività (${activities.size})", "Pasti (${meals.size})", "Chat (${messages.size})", "Log AI (${debugLog.size})")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DB Viewer") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 12.sp) }
                    )
                }
            }

            when (selectedTab) {
                0 -> ActivitiesTab(activities)
                1 -> MealsTab(meals)
                2 -> ChatTab(messages)
                3 -> LogTab(debugLog)
            }
        }
    }
}

@Composable
private fun ActivitiesTab(activities: List<Activity>) {
    if (activities.isEmpty()) {
        EmptyState("Nessuna attività nel database")
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            Text(
                "Totale: ${activities.size} attività",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        items(activities.sortedByDescending { it.scheduledDate }) { activity ->
            DbCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ID: ${activity.id}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (activity.aiGenerated) Badge { Text("AI") }
                        if (activity.isCompleted) Badge(containerColor = MaterialTheme.colorScheme.tertiary) { Text("✓") }
                    }
                }
                Text(activity.title, fontWeight = FontWeight.SemiBold)
                Text(
                    "${activity.type.displayName()} · ${activity.durationMinutes}min · ${activity.scheduledDate}${activity.scheduledTime?.let { " alle $it" } ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (activity.caloriesBurned > 0) {
                    Text("${activity.caloriesBurned} kcal bruciate", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun MealsTab(meals: List<Meal>) {
    if (meals.isEmpty()) {
        EmptyState("Nessun pasto nel database")
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            Text(
                "Totale: ${meals.size} pasti",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        items(meals.sortedByDescending { it.scheduledDate }) { meal ->
            DbCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ID: ${meal.id}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (meal.aiGenerated) Badge { Text("AI") }
                        if (meal.isConsumed) Badge(containerColor = MaterialTheme.colorScheme.tertiary) { Text("✓") }
                    }
                }
                Text(meal.name, fontWeight = FontWeight.SemiBold)
                Text(
                    "${meal.type.displayName()} · ${meal.scheduledDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (meal.caloriesKcal > 0) {
                    Text(
                        "${meal.caloriesKcal} kcal · P:${meal.proteinG}g C:${meal.carbsG}g G:${meal.fatG}g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (meal.ingredients.isNotEmpty()) {
                    Text(
                        meal.ingredients.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatTab(messages: List<ChatMessage>) {
    if (messages.isEmpty()) {
        EmptyState("Nessun messaggio nel database")
        return
    }
    val timeFormatter = DateTimeFormatter.ofPattern("dd/MM HH:mm")
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            Text(
                "Totale: ${messages.size} messaggi",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        items(messages) { msg ->
            val isUser = msg.role == ChatRole.USER
            DbCard(
                containerColor = if (isUser)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (isUser) "👤 Utente" else "🤖 AI",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (msg.planModified) Badge { Text("piano") }
                        Text(
                            msg.timestamp.atZone(ZoneId.systemDefault()).format(timeFormatter),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Text(
                    msg.content.take(200) + if (msg.content.length > 200) "…" else "",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun LogTab(entries: List<String>) {
    if (entries.isEmpty()) {
        EmptyState("Nessun log disponibile.\nInvia un messaggio alla chat AI per vedere il log del loop agentivo.")
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item {
            Text(
                "Log ultima sessione AI (${entries.size} voci)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        items(entries) { entry ->
            SelectionContainer {
                Text(
                    text = entry,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = when {
                        entry.startsWith("❌") -> MaterialTheme.colorScheme.error
                        entry.startsWith("🔧") || entry.startsWith("   Input") || entry.startsWith("   Risultato") ->
                            MaterialTheme.colorScheme.tertiary
                        entry.startsWith("✅") -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun DbCard(
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            content = content
        )
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }
}
