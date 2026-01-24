package com.iberosolutions.shifterup.ui.add

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberosolutions.shifterup.ui.theme.BlueActive
import com.iberosolutions.shifterup.viewmodel.AddRoutinePhase
import com.iberosolutions.shifterup.viewmodel.AddRoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoutineScreen(
    onClose: () -> Unit,
    viewModel: AddRoutineViewModel = hiltViewModel()
) {
    val phase by viewModel.currentPhase.collectAsState()
    val headerTitle by viewModel.headerTitle.collectAsState()
    val isButtonEnabled by viewModel.isNextButtonEnabled.collectAsState()

    Scaffold(
        containerColor = Color(0xFF0B0B15),
        topBar = {
            TopAppBar(
                title = {
                    Text(headerTitle, color = Color.White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    // Ocultamos la flecha solo en la primera pantalla
                    if (phase != AddRoutinePhase.ROUTINE_INFO) {
                        IconButton(onClick = { viewModel.onBackStep(onCloseScreen = onClose) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.White
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {
            when (phase) {
                AddRoutinePhase.ROUTINE_INFO -> RoutineInfoView(viewModel)
                AddRoutinePhase.DAY_INFO -> DayInfoView(viewModel)
                AddRoutinePhase.EXERCISE_INFO -> ExerciseInfoView(viewModel)
            }

            Spacer(modifier = Modifier.weight(1f))

            // ANIMACIÓN Y ESTADO DEL BOTÓN
            val buttonColor by animateColorAsState(
                targetValue = if (isButtonEnabled) BlueActive else Color.DarkGray,
                label = "color"
            )

            Button(
                onClick = { viewModel.onNextStep(onFinished = onClose) },
                enabled = isButtonEnabled, // <--- AQUÍ SE APLICA EL ESTADO
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    disabledContainerColor = Color.DarkGray,
                    contentColor = Color.White,
                    disabledContentColor = Color.Gray
                )
            ) {
                Text("Siguiente", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// VISTA 1: Nombre, Descripción y Días
@Composable
fun RoutineInfoView(viewModel: AddRoutineViewModel) {
    val name by viewModel.routineName.collectAsState()
    val description by viewModel.routineDescription.collectAsState()
    val days by viewModel.totalDaysRoutine.collectAsState()

    Text("Datos de la Rutina", color = BlueActive, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))

    CustomTextField(
        value = name,
        onValueChange = { viewModel.routineName.value = it },
        label = "Nombre (ej. Hipertrofia)"
    )
    Spacer(modifier = Modifier.height(16.dp))

    CustomTextField(
        value = description,
        onValueChange = { viewModel.routineDescription.value = it },
        label = "Descripción (ej. Foco en fuerza)"
    )
    Spacer(modifier = Modifier.height(16.dp))

    CustomTextField(
        value = days,
        onValueChange = { if(it.all{c->c.isDigit()}) viewModel.totalDaysRoutine.value = it },
        label = "¿Cuántos días?",
        isNumber = true
    )
}

// VISTA 2: Descripción del día y Cantidad de ejercicios
@Composable
fun DayInfoView(viewModel: AddRoutineViewModel) {
    val description by viewModel.dayDescription.collectAsState()
    val count by viewModel.exercisesCountForCurrentDay.collectAsState()

    Text("Configuración del Día", color = BlueActive, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))

    CustomTextField(
        value = description,
        onValueChange = { viewModel.dayDescription.value = it },
        label = "Descripción del día (ej. Pecho/Bíceps)"
    )
    Spacer(modifier = Modifier.height(16.dp))

    CustomTextField(
        value = count,
        onValueChange = { if(it.all{c->c.isDigit()}) viewModel.exercisesCountForCurrentDay.value = it },
        label = "Número de ejercicios hoy",
        isNumber = true
    )
}

// VISTA 3: Ejercicios
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseInfoView(viewModel: AddRoutineViewModel) {
    val userExercises by viewModel.userExercises.collectAsState()
    val selectedName by viewModel.selectedExerciseName.collectAsState()

    val sets by viewModel.setsInput.collectAsState()
    val reps by viewModel.repsInput.collectAsState()
    val min by viewModel.minInput.collectAsState()
    val sec by viewModel.secInput.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        CustomTextField(
            value = selectedName, onValueChange = {}, label = "Seleccionar Ejercicio", readOnly = true,
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (userExercises.isEmpty()) {
                DropdownMenuItem(text = { Text("No hay ejercicios creados") }, onClick = {})
            }
            userExercises.forEach { ex ->
                DropdownMenuItem(text = { Text(ex.name) }, onClick = {
                    viewModel.selectedExerciseName.value = ex.name
                    viewModel.selectedExerciseId.value = ex.id
                    expanded = false
                })
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.weight(1f)) {
            CustomTextField(value = sets, onValueChange = { if(it.isDigitsOnly()) viewModel.setsInput.value = it }, label = "Series", isNumber = true)
        }
        Box(Modifier.weight(1f)) {
            CustomTextField(value = reps, onValueChange = { if(it.isDigitsOnly()) viewModel.repsInput.value = it }, label = "Reps", isNumber = true)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
    Text("Descanso", color = Color.Gray, fontSize = 14.sp)

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.width(80.dp)) {
            CustomTextField(value = min, onValueChange = { if(it.isDigitsOnly()) viewModel.minInput.value = it }, label = "Min", isNumber = true)
        }
        Text(" : ", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
        Box(Modifier.width(80.dp)) {
            CustomTextField(value = sec, onValueChange = { if(it.isDigitsOnly()) viewModel.secInput.value = it }, label = "Seg", isNumber = true)
        }
    }
}

// Función auxiliar
fun String.isDigitsOnly(): Boolean = this.all { it.isDigit() }

@Composable
fun CustomTextField(
    value: String, onValueChange: (String) -> Unit, label: String,
    isNumber: Boolean = false, readOnly: Boolean = false,
    modifier: Modifier = Modifier, trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label) },
        readOnly = readOnly,
        modifier = modifier.fillMaxWidth(),
        trailingIcon = trailingIcon,
        keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlueActive, unfocusedBorderColor = Color.Gray,
            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
            focusedLabelColor = BlueActive, unfocusedLabelColor = Color.Gray
        ),
        singleLine = true
    )
}