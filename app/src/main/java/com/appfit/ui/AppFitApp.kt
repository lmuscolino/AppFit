package com.appfit.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.appfit.ui.activity.ActivityDetailScreen
import com.appfit.ui.calendar.CalendarScreen
import com.appfit.ui.dashboard.DashboardScreen
import com.appfit.ui.diet.MealDetailScreen
import com.appfit.ui.chat.ChatScreen
import com.appfit.ui.debug.DbViewerScreen
import com.appfit.ui.diet.DietScreen
import com.appfit.ui.favorites.FavoritesScreen
import com.appfit.ui.family.FamilyScreen
import com.appfit.ui.reminders.ReminderScreen
import com.appfit.ui.pendinginbox.PendingInboxScreen
import com.appfit.ui.navigation.Screen
import com.appfit.ui.navigation.bottomNavItems
import com.appfit.ui.navigation.drawerNavItems
import com.appfit.ui.onboarding.OnboardingScreen
import com.appfit.ui.onboarding.OnboardingViewModel
import com.appfit.ui.profile.ProfileScreen
import com.appfit.ui.profile.ProfileViewModel
import com.appfit.ui.shopping.ShoppingListScreen
import com.appfit.ui.workouts.WorkoutsScreen
import kotlinx.coroutines.launch

@Composable
fun AppFitApp(openPendingInbox: Boolean = false) {
    val navController = rememberNavController()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val isApiKeySet by onboardingViewModel.isApiKeySet.collectAsState()

    LaunchedEffect(openPendingInbox, isApiKeySet) {
        if (openPendingInbox && isApiKeySet) {
            navController.navigate(Screen.PendingInbox.route) { launchSingleTop = true }
        }
    }

    val profileViewModel: ProfileViewModel = hiltViewModel()
    val currentProvider by profileViewModel.currentProvider.collectAsStateWithLifecycle()
    val currentModel by profileViewModel.currentModel.collectAsStateWithLifecycle()

    val pendingConsentIntent by profileViewModel.pendingConsentIntent.collectAsStateWithLifecycle()
    val googleConsentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { profileViewModel.clearPendingConsentIntent() }
    LaunchedEffect(pendingConsentIntent) {
        pendingConsentIntent?.let { googleConsentLauncher.launch(it) }
    }

    val startDestination = if (isApiKeySet) Screen.Dashboard.route else Screen.Onboarding.route

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isMainScreen = bottomNavItems.any { it.screen.route == currentDestination?.route } ||
            currentDestination?.route == Screen.Chat.route ||
            currentDestination?.route == Screen.Reminders.route ||
            currentDestination?.route == Screen.Family.route ||
            currentDestination?.route == Screen.PendingInbox.route
    val showBottomBar = currentDestination?.route != Screen.Onboarding.route &&
            currentDestination?.route != Screen.Profile.route &&
            currentDestination?.route != Screen.Favorites.route &&
            isMainScreen
    val showChatFab = showBottomBar && currentDestination?.route != Screen.Chat.route

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBottomBar,
        drawerContent = {
            AppFitDrawerContent(
                currentRoute = currentDestination?.route,
                aiModelLabel = currentModel,
                onNavigate = { screen ->
                    scope.launch { drawerState.close() }
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        bottomNavItems.forEach { item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label, maxLines = 1, softWrap = false) },
                                selected = currentDestination?.hierarchy?.any {
                                    it.route == item.screen.route
                                } == true,
                                onClick = {
                                    navController.navigate(item.screen.route) {
                                        // Usa Dashboard come radice esplicita invece di findStartDestination()
                                        // che potrebbe restituire Onboarding se il grafo è stato inizializzato con esso
                                        popUpTo(Screen.Dashboard.route) {
                                            saveState = true
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                if (showChatFab) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(Screen.Chat.route) {
                                popUpTo(Screen.Dashboard.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            Icons.Filled.SmartToy,
                            contentDescription = "AI Chat",
                            tint = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onComplete = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        onOpenDrawer = openDrawer,
                        onNavigateToCalendar = {
                            navController.navigate(Screen.Calendar.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Screen.Calendar.route) {
                    CalendarScreen(
                        onActivityClick = { id ->
                            navController.navigate(Screen.ActivityDetail.createRoute(id))
                        },
                        onMealClick = { id ->
                            navController.navigate(Screen.MealDetail.createRoute(id))
                        },
                        onNavigateToProfile = {
                            navController.navigate(Screen.Profile.route)
                        },
                        onOpenDrawer = openDrawer
                    )
                }
                composable(Screen.Workouts.route) {
                    WorkoutsScreen(
                        onOpenDrawer = openDrawer,
                        onActivityClick = { id ->
                            navController.navigate(Screen.ActivityDetail.createRoute(id))
                        }
                    )
                }
                composable(Screen.Diet.route) {
                    DietScreen(
                        onOpenDrawer = openDrawer,
                        onMealClick = { id -> navController.navigate(Screen.MealDetail.createRoute(id)) }
                    )
                }
                composable(Screen.Shopping.route) {
                    ShoppingListScreen(onOpenDrawer = openDrawer)
                }
                composable(Screen.Chat.route) {
                    ChatScreen(
                        onPlanUpdated = {
                            navController.navigate(Screen.Calendar.route) {
                                launchSingleTop = true
                            }
                        },
                        onNavigateToDebug = {
                            navController.navigate(Screen.DbViewer.route)
                        },
                        onOpenDrawer = openDrawer
                    )
                }
                composable(
                    route = Screen.MealDetail.route,
                    arguments = listOf(
                        androidx.navigation.navArgument("mealId") {
                            type = androidx.navigation.NavType.LongType
                        }
                    )
                ) {
                    MealDetailScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.DbViewer.route) {
                    DbViewerScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.Favorites.route) {
                    FavoritesScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.Reminders.route) {
                    ReminderScreen(onOpenDrawer = openDrawer)
                }
                composable(Screen.Family.route) {
                    FamilyScreen(onOpenDrawer = openDrawer)
                }
                composable(Screen.PendingInbox.route) {
                    PendingInboxScreen(onOpenDrawer = openDrawer)
                }
                composable(
                    route = Screen.ActivityDetail.route,
                    arguments = listOf(
                        androidx.navigation.navArgument("activityId") {
                            type = androidx.navigation.NavType.LongType
                        }
                    )
                ) {
                    ActivityDetailScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppFitDrawerContent(
    currentRoute: String?,
    aiModelLabel: String = "",
    onNavigate: (Screen) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
        // Header con gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(primaryColor, secondaryColor),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Icon(
                    Icons.Filled.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "AppFit",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    "Il tuo assistente fitness",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
                if (aiModelLabel.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.SmartToy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            aiModelLabel.toShortModelName(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        drawerNavItems.forEachIndexed { index, item ->
            if (item.isHighlighted && index > 0) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
            }
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) },
                selected = currentRoute == item.screen.route,
                onClick = { onNavigate(item.screen) },
                modifier = Modifier.padding(horizontal = 12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
        } // chiude Column scrollabile
    }
}

private fun String.toShortModelName(): String = when {
    contains("opus") -> "Claude Opus"
    contains("sonnet") -> "Claude Sonnet"
    contains("haiku") -> "Claude Haiku"
    contains("2.5-pro") -> "Gemini 2.5 Pro"
    contains("2.5-flash-lite") -> "Gemini 2.5 Flash Lite"
    contains("2.5-flash") -> "Gemini 2.5 Flash"
    contains("3.1-flash-lite") -> "Gemini 3.1 Flash Lite"
    contains("3.1-flash") -> "Gemini 3.1 Flash"
    else -> this
}
