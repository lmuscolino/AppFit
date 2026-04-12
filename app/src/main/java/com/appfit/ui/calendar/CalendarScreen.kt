package com.appfit.ui.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfit.data.model.Activity
import com.appfit.data.model.ActivityType
import com.appfit.data.model.Meal
import com.appfit.data.model.MealType
import com.appfit.ui.common.UiState
import com.appfit.ui.theme.*
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(
    onActivityClick: (Long) -> Unit,
    onMealClick: (Long) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val currentMonth by viewModel.currentMonth.collectAsStateWithLifecycle()
    val dailyPlanState by viewModel.dailyPlanState.collectAsStateWithLifecycle()
    val datesWithContent by viewModel.datesWithContent.collectAsStateWithLifecycle()

    val startMonth = remember { YearMonth.now().minusMonths(6) }
    val endMonth = remember { YearMonth.now().plusMonths(12) }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    LaunchedEffect(calendarState.firstVisibleMonth) {
        viewModel.onMonthChanged(calendarState.firstVisibleMonth.yearMonth)
    }

    // Scroll list to top whenever selected date changes
    LaunchedEffect(selectedDate) {
        listState.animateScrollToItem(0)
    }

    var swipeOffset by remember { mutableStateOf(0f) }
    val swipeThresholdPx = with(LocalDensity.current) { 60.dp.toPx() }

    // Dialog states
    var showAddChoice by remember { mutableStateOf(false) }
    var showAddActivity by remember { mutableStateOf(false) }
    var showAddMeal by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddChoice = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Aggiungi")
            }
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            // Top bar — hamburger + month navigation inline in title
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            scope.launch {
                                calendarState.animateScrollToMonth(
                                    calendarState.firstVisibleMonth.yearMonth.minusMonths(1)
                                )
                            }
                        }) {
                            Icon(
                                Icons.Filled.ChevronLeft,
                                contentDescription = "Mese precedente",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ITALIAN)),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        IconButton(onClick = {
                            scope.launch {
                                calendarState.animateScrollToMonth(
                                    calendarState.firstVisibleMonth.yearMonth.plusMonths(1)
                                )
                            }
                        }) {
                            Icon(
                                Icons.Filled.ChevronRight,
                                contentDescription = "Mese successivo",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Profilo",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Day-of-week header
            Row(modifier = Modifier.fillMaxWidth()) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, Locale.ITALIAN).uppercase(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Calendar — swipe rilevato manualmente per animazione controllata
            HorizontalCalendar(
                state = calendarState,
                userScrollEnabled = false,
                dayContent = { day ->
                    CalendarDayCell(
                        day = day,
                        isSelected = day.date == selectedDate,
                        hasContent = day.date in datesWithContent,
                        onClick = {
                            if (day.position == DayPosition.MonthDate) {
                                viewModel.onDateSelected(day.date)
                            }
                        }
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .pointerInput(calendarState) {
                        detectHorizontalDragGestures(
                            onDragStart = { swipeOffset = 0f },
                            onDragCancel = { swipeOffset = 0f },
                            onDragEnd = {
                                if (swipeOffset < -swipeThresholdPx) {
                                    scope.launch {
                                        calendarState.animateScrollToMonth(
                                            calendarState.firstVisibleMonth.yearMonth.plusMonths(1)
                                        )
                                    }
                                } else if (swipeOffset > swipeThresholdPx) {
                                    scope.launch {
                                        calendarState.animateScrollToMonth(
                                            calendarState.firstVisibleMonth.yearMonth.minusMonths(1)
                                        )
                                    }
                                }
                                swipeOffset = 0f
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                swipeOffset += dragAmount
                            }
                        )
                    }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Selected day header
            val todayFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ITALIAN)
            Text(
                text = selectedDate.format(todayFormatter).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Daily content — Box.weight(1f) garantisce altezza bounded per LazyColumn
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val state = dailyPlanState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is UiState.Success -> {
                    val plan = state.data
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            SectionHeader(title = "Attività", icon = Icons.Filled.FitnessCenter)
                        }
                        if (plan.activities.isEmpty()) {
                            item { EmptyStateCard(text = "Nessuna attività pianificata") }
                        } else {
                            items(plan.activities, key = { it.id }) { activity ->
                                ActivityCard(
                                    activity = activity,
                                    onClick = { onActivityClick(activity.id) },
                                    onToggleComplete = {
                                        viewModel.toggleActivityCompleted(activity.id, activity.isCompleted)
                                    }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            SectionHeader(title = "Pasti", icon = Icons.Filled.Restaurant)
                        }
                        if (plan.meals.isEmpty()) {
                            item { EmptyStateCard(text = "Nessun pasto pianificato") }
                        } else {
                            items(plan.meals, key = { it.id }) { meal ->
                                MealCard(
                                    meal = meal,
                                    onClick = { onMealClick(meal.id) },
                                    onToggleConsumed = {
                                        viewModel.toggleMealConsumed(meal.id, meal.isConsumed)
                                    }
                                )
                            }
                        }

                        if (plan.meals.isNotEmpty() || plan.activities.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                CalorieSummaryCard(plan = plan)
                            }
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
            } // chiude Box weight(1f)
        }
    }

    // Add choice dialog
    if (showAddChoice) {
        AlertDialog(
            onDismissRequest = { showAddChoice = false },
            title = { Text("Cosa vuoi aggiungere?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showAddChoice = false; showAddActivity = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.FitnessCenter, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Attività fisica")
                    }
                    OutlinedButton(
                        onClick = { showAddChoice = false; showAddMeal = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Restaurant, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Pasto")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddChoice = false }) { Text("Annulla") }
            }
        )
    }

    // Add activity dialog
    if (showAddActivity) {
        AddActivityDialog(
            selectedDate = selectedDate,
            onDismiss = { showAddActivity = false },
            onSave = { title, type, duration, time, calories, description ->
                viewModel.addActivityManually(title, type, duration, selectedDate, time, calories, description)
                showAddActivity = false
            }
        )
    }

    // Add meal dialog
    if (showAddMeal) {
        AddMealDialog(
            selectedDate = selectedDate,
            onDismiss = { showAddMeal = false },
            onSave = { name, type, calories, protein, carbs, fat, ingredients ->
                viewModel.addMealManually(name, type, selectedDate, calories, protein, carbs, fat, ingredients)
                showAddMeal = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddActivityDialog(
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onSave: (String, ActivityType, Int, LocalTime?, Int, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ActivityType.CARDIO) }
    var duration by remember { mutableStateOf("30") }
    var timeStr by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    val dateLabel = selectedDate.format(DateTimeFormatter.ofPattern("d MMMM", Locale.ITALIAN))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuova attività — $dateLabel") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    label = { Text("Titolo *") },
                    isError = titleError,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedType.displayName(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        ActivityType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName()) },
                                onClick = { selectedType = type; typeExpanded = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Durata (minuti)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = timeStr,
                    onValueChange = { timeStr = it },
                    label = { Text("Ora (HH:mm, opzionale)") },
                    placeholder = { Text("es. 07:30") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calorie bruciate (opzionale)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrizione (opzionale)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isBlank()) { titleError = true; return@Button }
                val parsedTime = timeStr.takeIf { it.isNotBlank() }?.let {
                    try { LocalTime.parse(it) } catch (e: Exception) { null }
                }
                onSave(
                    title.trim(),
                    selectedType,
                    duration.toIntOrNull() ?: 30,
                    parsedTime,
                    calories.toIntOrNull() ?: 0,
                    description.trim()
                )
            }) { Text("Salva") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMealDialog(
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onSave: (String, MealType, Int, Int, Int, Int, List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MealType.LUNCH) }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var ingredientsStr by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    val dateLabel = selectedDate.format(DateTimeFormatter.ofPattern("d MMMM", Locale.ITALIAN))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuovo pasto — $dateLabel") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text("Nome *") },
                    isError = nameError,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedType.displayName(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo pasto") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        MealType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName()) },
                                onClick = { selectedType = type; typeExpanded = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calorie (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it },
                        label = { Text("Prot. (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it },
                        label = { Text("Carb. (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { fat = it },
                        label = { Text("Grassi (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = ingredientsStr,
                    onValueChange = { ingredientsStr = it },
                    label = { Text("Ingredienti (separati da virgola, opzionale)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isBlank()) { nameError = true; return@Button }
                val ingredients = ingredientsStr
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                onSave(
                    name.trim(),
                    selectedType,
                    calories.toIntOrNull() ?: 0,
                    protein.toIntOrNull() ?: 0,
                    carbs.toIntOrNull() ?: 0,
                    fat.toIntOrNull() ?: 0,
                    ingredients
                )
            }) { Text("Salva") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}

@Composable
private fun CalendarDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    hasContent: Boolean,
    onClick: () -> Unit
) {
    val isToday = day.date == LocalDate.now()
    val isCurrentMonth = day.position == DayPosition.MonthDate

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }
            )
            .clickable(enabled = isCurrentMonth, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                    isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                },
                fontSize = 14.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
            if (hasContent && isCurrentMonth) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.primary
                        )
                )
            }
        }
    }
}

@Composable
fun ActivityCard(
    activity: Activity,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit
) {
    val activityColor = when (activity.type) {
        ActivityType.CARDIO -> CardioColor
        ActivityType.STRENGTH -> StrengthColor
        ActivityType.FLEXIBILITY -> FlexibilityColor
        ActivityType.YOGA -> YogaColor
        ActivityType.REST -> RestColor
        ActivityType.CUSTOM -> CustomColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(activityColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (activity.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (activity.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(activity.type.displayName(), fontSize = 11.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = activityColor.copy(alpha = 0.15f)
                        )
                    )
                    Text(
                        text = "${activity.durationMinutes} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (activity.scheduledTime != null) {
                        Text(
                            text = activity.scheduledTime.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Checkbox(
                checked = activity.isCompleted,
                onCheckedChange = { onToggleComplete() }
            )
        }
    }
}

@Composable
fun MealCard(
    meal: Meal,
    onClick: () -> Unit = {},
    onToggleConsumed: () -> Unit
) {
    val mealIcon = when (meal.type) {
        MealType.BREAKFAST -> Icons.Filled.Coffee
        MealType.LUNCH -> Icons.Filled.LunchDining
        MealType.DINNER -> Icons.Filled.DinnerDining
        MealType.SNACK -> Icons.Filled.EmojiFoodBeverage
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = mealIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.type.displayName(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (meal.isConsumed) TextDecoration.LineThrough else TextDecoration.None
                )
                if (meal.caloriesKcal > 0) {
                    Text(
                        text = "${meal.caloriesKcal} kcal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Checkbox(
                checked = meal.isConsumed,
                onCheckedChange = { onToggleConsumed() }
            )
        }
    }
}

@Composable
private fun CalorieSummaryCard(plan: com.appfit.data.model.DailyPlan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Riepilogo calorie",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CalorieItem(label = "Assunte", value = "${plan.totalCaloriesConsumed}", unit = "kcal")
                CalorieItem(label = "Bruciate", value = "${plan.totalCaloriesBurned}", unit = "kcal")
                val net = plan.totalCaloriesConsumed - plan.totalCaloriesBurned
                CalorieItem(
                    label = "Nette",
                    value = if (net >= 0) "+$net" else "$net",
                    unit = "kcal"
                )
            }
        }
    }
}

@Composable
private fun CalorieItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold
        )
        Text(text = unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun EmptyStateCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}
