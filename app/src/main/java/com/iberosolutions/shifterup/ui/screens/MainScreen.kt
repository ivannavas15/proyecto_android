package com.iberosolutions.shifterup.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape // Necesario para la forma rectangular
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iberosolutions.shifterup.R
import com.iberosolutions.shifterup.ui.configuration.ConfigurationScreen
import com.iberosolutions.shifterup.ui.exercises.ExercisesScreen
import com.iberosolutions.shifterup.ui.kpi.KpiScreen
import com.iberosolutions.shifterup.ui.routines.RoutinesScreen
import com.iberosolutions.shifterup.ui.theme.BlueActive
import com.iberosolutions.shifterup.ui.theme.DefaultBackground

// Colores
val NavBarBackground = Color(0xFF13132B)
val NavBarContentColor = Color.White
val SelectedPillColor = BlueActive

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val mainNavController = rememberNavController()
    var selectedRoute by remember { mutableStateOf("Rutinas") }

    Scaffold(
        containerColor = DefaultBackground,
        bottomBar = {
            FloatingCapsuleBar(
                selectedRoute = selectedRoute,
                onItemSelected = { newRoute ->
                    selectedRoute = newRoute
                    mainNavController.navigate(newRoute) {
                        popUpTo(mainNavController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 30.dp)
        ) {
            NavHost(
                navController = mainNavController,
                startDestination = "Rutinas",
                modifier = Modifier.fillMaxSize()
            ) {
                // AQUÍ ESTÁ LA CLAVE:
                composable("Rutinas") {
                    RoutinesScreen(
                        onAddRoutineClick = {
                            // Esto navega al formulario COMPLETO (Paso 1: Nombre rutina -> Paso 2: Días)
                            rootNavController.navigate("create_routine_screen")
                        },
                        onAddDayToRoutine = { routineId ->
                            // Esto navega DIRECTAMENTE al Paso 2 (Formulario de día), pasándole el ID
                            rootNavController.navigate("add_day_screen/$routineId")
                        }
                    )
                }
                composable("Ejercicios") { ExercisesScreen() }
                composable("Kpi") { KpiScreen() }
                composable("Configuración") {
                    ConfigurationScreen(rootNavController = rootNavController)
                }

            }
        }
    }
}

// --- BARRA CÁPSULA FLOTANTE ---
@Composable
fun FloatingCapsuleBar(
    selectedRoute: String,
    onItemSelected: (String) -> Unit
) {
    Surface(
        color = NavBarBackground,
        contentColor = NavBarContentColor,
        shape = RectangleShape,
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
            .padding(top = 8.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // ITEM 1: RUTINAS (Ejemplo: Tamaño normal 24dp)
                CapsuleNavItem(
                    icon = painterResource(R.drawable.ic_list),
                    label = "Rutinas",
                    iconSize = 24.dp, // <--- TAMAÑO PERSONALIZADO
                    isSelected = selectedRoute == "Rutinas",
                    onClick = { onItemSelected("Rutinas") }
                )

                // ITEM 2: EJERCICIOS (Ejemplo: Más grande 32dp)
                CapsuleNavItem(
                    icon = painterResource(R.drawable.ic_dumbbell),
                    label = "Ejercicios",
                    iconSize = 30.dp,
                    horizontalPadding = 12.dp,
                    verticalPadding = 5.dp,
                    isSelected = selectedRoute == "Ejercicios",
                    onClick = { onItemSelected("Ejercicios") }
                )

                // ITEM 3: KPI (Ejemplo: Muy grande 40dp)
                CapsuleNavItem(
                    icon = painterResource(R.drawable.ic_kpi),
                    label = "Progreso",
                    iconSize = 34.dp,
                    horizontalPadding = 12.dp,
                    verticalPadding = 3.dp,
                    isSelected = selectedRoute == "Kpi",
                    onClick = { onItemSelected("Kpi") }
                )

                // ITEM 4: CONFIGURACIÓN (Ejemplo: Pequeño 20dp)
                CapsuleNavItem(
                    icon = painterResource(R.drawable.ic_profile),
                    label = "Perfil",
                    iconSize = 22.dp,
                    isSelected = selectedRoute == "Configuración",
                    onClick = { onItemSelected("Configuración") }
                )
            }
        }
    }
}

// --- ÍTEM INDIVIDUAL (EFECTO PASTILLA/PILL) ---
@Composable
fun CapsuleNavItem(
    icon: Painter,
    label: String,
    isSelected: Boolean,
    iconSize: Dp = 28.dp,
    horizontalPadding: Dp = 14.dp,
    verticalPadding: Dp = 7.dp,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) SelectedPillColor else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "bgColorAnim"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Gray,
        label = "contentColorAnim"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(iconSize) // <--- APLICAMOS EL TAMAÑO AQUÍ
            )

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        color = contentColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}