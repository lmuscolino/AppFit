package com.appfit.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
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
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Calendar, "Calendario", Icons.Filled.CalendarMonth),
    BottomNavItem(Screen.Workouts, "Allenamenti", Icons.Filled.FitnessCenter),
    BottomNavItem(Screen.Diet, "Dieta", Icons.Filled.Restaurant),
    BottomNavItem(Screen.Shopping, "Spesa", Icons.Filled.ShoppingCart),
    BottomNavItem(Screen.Chat, "AI Chat", Icons.Filled.SmartToy)
)

data class DrawerNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val isHighlighted: Boolean = false
)

val drawerNavItems = listOf(
    DrawerNavItem(Screen.Calendar, "Calendario", Icons.Filled.CalendarMonth),
    DrawerNavItem(Screen.Workouts, "Allenamenti", Icons.Filled.FitnessCenter),
    DrawerNavItem(Screen.Diet, "Dieta", Icons.Filled.Restaurant),
    DrawerNavItem(Screen.Shopping, "Lista della spesa", Icons.Filled.ShoppingCart),
    DrawerNavItem(Screen.Chat, "AI Assistente", Icons.Filled.SmartToy),
    DrawerNavItem(Screen.Profile, "Profilo", Icons.Filled.Person, isHighlighted = true)
)
