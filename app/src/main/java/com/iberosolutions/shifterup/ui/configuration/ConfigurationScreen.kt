package com.iberosolutions.shifterup.ui.configuration

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun ConfigurationScreen(
    // Recibimos el controlador PRINCIPAL para poder salir al Login
    rootNavController: NavHostController,
    viewModel: ConfigurationViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {

        Button(
            onClick = {
                // 1. Cerramos sesión en Firebase
                viewModel.logout()

                // 2. Navegamos al Login borrando TODO el historial
                rootNavController.navigate("email_login") {
                    // Esto borra toda la pila de pantallas para que no puedan volver atrás
                    popUpTo(rootNavController.graph.id) { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red.copy(alpha = 0.1f),
                contentColor = Color.Red
            ),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión")
        }

        Spacer(modifier = Modifier.height(100.dp)) // Espacio para el navbar
    }
}