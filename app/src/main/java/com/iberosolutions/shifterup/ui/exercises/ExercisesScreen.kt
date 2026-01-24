package com.iberosolutions.shifterup.ui.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberosolutions.shifterup.data.model.Exercise
import com.iberosolutions.shifterup.ui.theme.BlueActive
import com.iberosolutions.shifterup.viewmodel.ExercisesViewModel

@Composable
fun ExercisesScreen(
    viewModel: ExercisesViewModel = hiltViewModel()
) {
    val exercises by viewModel.exercisesListState.collectAsState()
    val searchText = viewModel.searchText
    var showDialog by remember { mutableStateOf(false) }
    // Estado para el diálogo de BORRAR (Guarda el ejercicio a eliminar)
    var exerciseToDelete by remember { mutableStateOf<Exercise?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        // Eliminamos el FloatingActionButton de aquí para ponerlo arriba
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .padding(horizontal = 20.dp)
        ) {
            Text(
                "Tu biblioteca de ejercicios",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- NUEVO DISEÑO: BUSCADOR + BOTÓN ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. BARRA DE BÚSQUEDA
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { viewModel.onSearch(it) },
                    modifier = Modifier
                        .weight(1f) // Ocupa todo el espacio disponible
                        .height(56.dp), // Altura estándar
                    placeholder = {
                        Text("Buscar ejercicios...", color = Color.Gray, fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueActive,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = BlueActive,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(10.dp))

                // 2. BOTÓN DE AÑADIR (Diseño cuadrado)
                Button(
                    onClick = { showDialog = true },
                    shape = RoundedCornerShape(12.dp), // Mismo borde que el buscador
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueActive,
                        contentColor = Color.White
                    ),
                    // Tamaño cuadrado fijo (56dp para igualar altura del TextField)
                    modifier = Modifier.size(56.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            // ---------------------------------------------

            Spacer(modifier = Modifier.height(20.dp))

            // LISTA DE RESULTADOS
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                if (exercises.isEmpty()) {
                    item {
                        Text(
                            text = if (searchText.isEmpty()) "No tienes ejercicios registrados." else "No se encontraron resultados.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                } else {
                    items(exercises) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onDeleteClick = { exerciseToDelete = exercise }
                        )
                    }
                }
            }
        }
    }

    // EL DIÁLOGO
    if (showDialog) {
        AddExerciseDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, muscle ->
                viewModel.createExercise(name, muscle)
                showDialog = false
            }
        )
    }

    // DIÁLOGO DE CONFIRMACIÓN DE BORRADO
    if (exerciseToDelete != null) {
        AlertDialog(
            onDismissRequest = { exerciseToDelete = null }, // Si pincha fuera, se cancela
            title = { Text("Eliminar Ejercicio") },
            text = {
                Text("¿Estás seguro de que quieres eliminar '${exerciseToDelete?.name}'? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // AQUÍ ES DONDE BORRAMOS DE VERDAD
                        exerciseToDelete?.let { viewModel.deleteExercise(it.id) }
                        exerciseToDelete = null // Cerramos el diálogo
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { exerciseToDelete = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}

// --- FUNCIONES AUXILIARES (Estas son las que te faltaban) ---

@Composable
fun ExerciseCard(exercise: Exercise, onDeleteClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // INFO IZQUIERDA
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(BlueActive.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = exercise.muscleGroup.take(1).uppercase(),
                        color = BlueActive,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = exercise.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = exercise.muscleGroup,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }

            // BOTÓN BORRAR
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Borrar",
                    tint = Color.Red.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun AddExerciseDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var muscle by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Ejercicio") },
        text = {
            Column {

                OutlinedTextField(
                    value = muscle,
                    onValueChange = { muscle = it },
                    label = { Text("Grupo Muscular") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueActive,
                        focusedLabelColor = BlueActive,
                        cursorColor = BlueActive
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueActive,
                        focusedLabelColor = BlueActive,
                        cursorColor = BlueActive
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && muscle.isNotBlank()) {
                        onConfirm(name, muscle)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlueActive)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}