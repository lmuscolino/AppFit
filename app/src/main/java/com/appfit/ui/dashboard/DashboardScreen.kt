package com.appfit.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfit.data.model.Activity
import com.appfit.data.model.ActivityType
import com.appfit.data.model.DailyPlan
import com.appfit.data.model.Meal
import com.appfit.ui.theme.GradientTopAppBar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onOpenDrawer: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val todayPlan by viewModel.todayPlan.collectAsStateWithLifecycle()
    val weeklyDaysWithContent by viewModel.weeklyDaysWithContent.collectAsStateWithLifecycle()

    val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ITALIAN)

    Scaffold(
        topBar = {
            GradientTopAppBar(
                title = { Text("AppFit", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    text = LocalDate.now().format(dateFormatter)
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Card 1 — Calorie
            item { CaloriesCard(todayPlan) }

            // Card 2 — Attività del giorno
            item { ActivitiesCard(todayPlan.activities) }

            // Card 3 — Prossimo pasto
            item { NextMealCard(todayPlan.meals) }

            // Card 4 — Progresso settimana
            item { WeeklyProgressCard(weeklyDaysWithContent) }

            // CTA calendario
            item {
                Spacer(Modifier.height(4.dp))
                OutlinedButton(
                    onClick = onNavigateToCalendar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Vai al Calendario")
                }
            }
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun CaloriesCard(plan: DailyPlan) {
    val consumed = plan.totalCaloriesConsumed
    val goal = plan.activeDietPlan?.dailyCalorieGoal ?: plan.totalCaloriesPlanned
    val progress = if (goal > 0) (consumed.toFloat() / goal).coerceIn(0f, 1f) else 0f

    DashboardCard(title = "Calorie oggi", icon = Icons.Filled.LocalFireDepartment) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "$consumed kcal",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (goal > 0) {
                    Text(
                        text = "su $goal kcal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (plan.totalCaloriesBurned > 0) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "-${plan.totalCaloriesBurned} kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "bruciate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        if (goal > 0) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Composable
private fun ActivitiesCard(activities: List<Activity>) {
    DashboardCard(title = "Attività del giorno", icon = Icons.Filled.FitnessCenter) {
        if (activities.isEmpty()) {
            Text(
                "Nessuna attività pianificata",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            activities.forEach { activity ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (activity.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (activity.isCompleted) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            activity.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "${activity.durationMinutes} min · ${activity.type.displayName()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(activityTypeColor(activity.type))
                    )
                }
            }
        }
    }
}

@Composable
private fun NextMealCard(meals: List<Meal>) {
    val nextMeal = meals.firstOrNull { !it.isConsumed }
    DashboardCard(title = "Prossimo pasto", icon = Icons.Filled.Restaurant) {
        if (nextMeal == null) {
            Text(
                if (meals.isEmpty()) "Nessun pasto pianificato"
                else "Tutti i pasti consumati",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        nextMeal.type.displayName(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        nextMeal.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (nextMeal.scheduledTime != null) {
                        Text(
                            nextMeal.scheduledTime.toString().substring(0, 5),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (nextMeal.caloriesKcal > 0) {
                    Text(
                        "${nextMeal.caloriesKcal} kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyProgressCard(daysWithContent: Set<LocalDate>) {
    val dayLabels = listOf("L", "M", "M", "G", "V", "S", "D")
    val today = LocalDate.now()
    val monday = today.with(DayOfWeek.MONDAY)

    DashboardCard(title = "Progresso settimana", icon = Icons.Filled.CalendarMonth) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dayLabels.forEachIndexed { index, label ->
                val dayDate = monday.plusDays(index.toLong())
                val hasContent = dayDate in daysWithContent
                val isToday = dayDate == today
                val isFuture = dayDate.isAfter(today)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isToday -> MaterialTheme.colorScheme.primary
                                    hasContent -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasContent && !isToday) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                        } else if (isToday) {
                            Text(
                                dayDate.dayOfMonth.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                dayDate.dayOfMonth.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isFuture) MaterialTheme.colorScheme.onSurfaceVariant
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun activityTypeColor(type: ActivityType): Color = when (type) {
    ActivityType.CARDIO -> Color(0xFFFF7043)
    ActivityType.STRENGTH -> Color(0xFF5C6BC0)
    ActivityType.FLEXIBILITY -> Color(0xFF26A69A)
    ActivityType.YOGA -> Color(0xFFAB47BC)
    ActivityType.REST -> Color(0xFF78909C)
    ActivityType.CUSTOM -> Color(0xFFFFCA28)
}
