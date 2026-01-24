package com.iberosolutions.shifterup.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// Define cómo es un botón del menú
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // Aquí defines tus pestañas reales
    object Routines : BottomNavItem("home", "Inicio", Icons.Default.Home)
    object Add : BottomNavItem("profile", "Perfil", Icons.Default.Person)
    object Kpi : BottomNavItem("settings", "Ajustes", Icons.Default.Settings)
}