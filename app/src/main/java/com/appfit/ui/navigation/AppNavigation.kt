package com.appfit.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Calendar : Screen("calendar")
    object Diet : Screen("diet")
    object Shopping : Screen("shopping")
    object Chat : Screen("chat")
    object Onboarding : Screen("onboarding")
    object ActivityDetail : Screen("activity/{activityId}") {
        fun createRoute(id: Long) = "activity/$id"
    }
    object MealDetail : Screen("meal/{mealId}") {
        fun createRoute(id: Long) = "meal/$id"
    }
    object DbViewer : Screen("dbviewer")
    object Profile : Screen("profile")
    object Workouts : Screen("workouts")
    object Favorites : Screen("favorites")
    object Reminders : Screen("reminders")
    object Family : Screen("family")
    object PendingInbox : Screen("pending_inbox")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Home", Icons.Filled.Home),
    BottomNavItem(Screen.Calendar, "Calendario", Icons.Filled.CalendarMonth),
    BottomNavItem(Screen.Workouts, "Workout", Icons.Filled.FitnessCenter),
    BottomNavItem(Screen.Diet, "Dieta", Icons.Filled.Restaurant),
    BottomNavItem(Screen.Shopping, "Spesa", Icons.Filled.ShoppingCart)
)

data class DrawerNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val isHighlighted: Boolean = false
)

val drawerNavItems = listOf(
    DrawerNavItem(Screen.Dashboard, "Home", Icons.Filled.Home),
    DrawerNavItem(Screen.Calendar, "Calendario", Icons.Filled.CalendarMonth),
    DrawerNavItem(Screen.Workouts, "Allenamenti", Icons.Filled.FitnessCenter),
    DrawerNavItem(Screen.Diet, "Dieta", Icons.Filled.Restaurant),
    DrawerNavItem(Screen.Favorites, "Ricette preferite", Icons.Filled.Favorite),
    DrawerNavItem(Screen.Shopping, "Lista della spesa", Icons.Filled.ShoppingCart),
    DrawerNavItem(Screen.Reminders, "Promemoria", Icons.Filled.NotificationsActive),
    DrawerNavItem(Screen.PendingInbox, "Posta AI", Icons.Filled.MarkEmailRead),
    DrawerNavItem(Screen.Family, "Famiglia", Icons.Filled.Groups),
    DrawerNavItem(Screen.Chat, "AI Assistente", Icons.Filled.SmartToy),
    DrawerNavItem(Screen.Profile, "Profilo", Icons.Filled.Person, isHighlighted = true)
)
