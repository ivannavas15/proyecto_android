package com.iberosolutions.shifterup.ui.routines

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberosolutions.shifterup.data.model.Routine
import com.iberosolutions.shifterup.data.model.RoutineDay
import com.iberosolutions.shifterup.data.model.RoutineExercise
import com.iberosolutions.shifterup.ui.theme.BlueActive
import com.iberosolutions.shifterup.viewmodel.RoutinesViewModel
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.asComposeRenderEffect

@Composable
fun RoutinesScreen(
    viewModel: RoutinesViewModel = hiltViewModel(),
    onAddRoutineClick: () -> Unit,
    onAddDayToRoutine: (String) -> Unit
) {
    val routines by viewModel.routinesListState.collectAsState()
    val searchText = viewModel.searchText

    // ESTADOS PARA LOS DIÁLOGOS
    var routineToDelete by remember { mutableStateOf<Routine?>(null) }
    var routineToEdit by remember { mutableStateOf<Routine?>(null) } // Nuevo estado para editar

    // --- CONFIGURACIÓN DE MEDIDAS ---
    val density = LocalDensity.current
    val topPaddingDp = 0.dp
    val topPaddingPx = with(density) { topPaddingDp.toPx() }

    // --- VARIABLES PARA EL SCROLL ---
    var topBarHeightPx by remember { mutableFloatStateOf(0f) }
    var topBarOffsetHeightPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = topBarOffsetHeightPx + delta
                val limit = -(topBarHeightPx + topPaddingPx)
                topBarOffsetHeightPx = newOffset.coerceIn(limit, 0f)
                return Offset.Zero
            }
        }
    }

    // --- COLORES ---
    val searchBarColor = Color(0xFF252535).copy(alpha = 0.95f)
    val contentColor = Color(0xFFEEEEEE)
    val placeholderColor = Color(0xFFAAAAAA)

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .nestedScroll(nestedScrollConnection)
        ) {
            // 1. LISTA (FONDO)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = topPaddingDp + 70.dp,
                    bottom = 30.dp,
                    start = 20.dp,
                    end = 20.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(routines) { routine ->
                    RoutineSection(
                        routine = routine,
                        onDeleteClick = { routineToDelete = routine },
                        onEditClick = { routineToEdit = routine }, // Pasamos la acción de editar
                        onAddDayClick = {
                            onAddDayToRoutine(routine.id)
                        }
                    )
                }
            }

            // 2. BARRA SUPERIOR (FLOTANTE)
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = topPaddingDp, start = 20.dp, end = 20.dp)
                    .onSizeChanged { size ->
                        topBarHeightPx = size.height.toFloat()
                    }
                    .graphicsLayer {
                        translationY = topBarOffsetHeightPx
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- BARRA DE BÚSQUEDA ---
                BasicTextField(
                    value = searchText,
                    onValueChange = { viewModel.onSearch(it) },
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp)
                        .shadow(8.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.5f))
                        .background(searchBarColor, CircleShape)
                        .clip(CircleShape),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = contentColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(BlueActive),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = placeholderColor,
                                modifier = Modifier.size(29.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchText.isEmpty()) {
                                    Text(
                                        text = "Busca tu rutina",
                                        color = placeholderColor,
                                        fontSize = 16.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.width(12.dp))

                // --- BOTÓN AÑADIR ---
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .shadow(8.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.5f))
                        .background(searchBarColor, CircleShape)
                        .clip(CircleShape)
                        .clickable { onAddRoutineClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nueva Rutina",
                        tint = contentColor,
                        modifier = Modifier.size(29.dp)
                    )
                }
            }
        }
    }

    // --- DIÁLOGOS ---

    // 1. Diálogo de Borrado
    if (routineToDelete != null) {
        AlertDialog(
            onDismissRequest = { routineToDelete = null },
            title = { Text("Eliminar Rutina") },
            text = { Text("¿Eliminar '${routineToDelete?.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        routineToDelete?.let { viewModel.deleteRoutine(it.id) }
                        routineToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { routineToDelete = null }) { Text("Cancelar") }
            },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // 2. Diálogo de Edición (NUEVO)
    if (routineToEdit != null) {
        EditRoutineDialog(
            routine = routineToEdit!!,
            onDismiss = { routineToEdit = null },
            onConfirm = { newName, newDescription ->
                // Actualiza la rutina con los nuevos datos
                val updatedRoutine = routineToEdit!!.copy(name = newName, description = newDescription)

                // IMPORTANTE: Asegúrate de tener 'updateRoutine' en tu ViewModel
                // o usa 'addRoutine' si tu lógica de backend sobreescribe por ID.
                viewModel.updateRoutine(updatedRoutine)

                routineToEdit = null
            }
        )
    }
}

// ---------------------------------------------------------
// COMPONENTE 1: SECCIÓN DE RUTINA (MODIFICADO CON MENÚ)
// ---------------------------------------------------------
@Composable
fun RoutineSection(
    routine: Routine,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onAddDayClick: () -> Unit // <--- Nuevo callback
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        // ... (La parte del título y menú de 3 puntos se queda IGUAL) ...
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = routine.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (routine.description.isNotEmpty()) {
                    Text(
                        text = routine.description,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box {
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                // ... (El DropdownMenu se queda IGUAL) ...
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(x = 0.dp, y = 10.dp),
                    containerColor = Color(0xFF2C2C2C)
                ) {
                    DropdownMenuItem(
                        text = { Text("Modificar", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = Color.White) },
                        onClick = {
                            expanded = false
                            onEditClick()
                        }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.3f))
                    DropdownMenuItem(
                        text = { Text("Eliminar", color = Color(0xFFFF5555)) },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color(0xFFFF5555)) },
                        onClick = {
                            expanded = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // LA LISTA HORIZONTAL CON LA TARJETA DE AÑADIR
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            // 1. Las tarjetas de días existentes
            items(routine.days) { day ->
                DayWorkoutCard(day = day)
            }

            // 2. La tarjeta extra para AÑADIR DÍA
            item {
                AddDayCard(onClick = onAddDayClick)
            }
        }
    }
}

// ---------------------------------------------------------
// COMPONENTE NUEVO: DIÁLOGO DE EDICIÓN
// ---------------------------------------------------------
@Composable
fun EditRoutineDialog(
    routine: Routine,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(routine.name) }
    var description by remember { mutableStateOf(routine.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modificar Rutina") },
        text = {
            Column {
                // Campo Nombre
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = BlueActive,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = BlueActive,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = BlueActive
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Campo Descripción
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = BlueActive,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = BlueActive,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = BlueActive
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, description) },
                colors = ButtonDefaults.buttonColors(containerColor = BlueActive)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

// ---------------------------------------------------------
// COMPONENTE 2: TARJETA EXTRA COMPACTA (IGUAL QUE ANTES)
// ---------------------------------------------------------
@Composable
fun DayWorkoutCard(day: RoutineDay) {
    // Configuración del estilo Glass
    val glassCornerRadius = 16.dp
    // Color base oscuro (Azul noche profundo)
    val darkGlassTintBase = Color(0xFF1E2B3A)

    // 1. Pincel de tinte: Define la oscuridad y transparencia.
    // Lo hacemos más transparente arriba para que se "vean detalles" del fondo.
    val glassTintBrush = Brush.verticalGradient(
        colors = listOf(
            darkGlassTintBase.copy(alpha = 0.55f), // Arriba: Más transparente
            darkGlassTintBase.copy(alpha = 0.80f)  // Abajo: Más opaco para solidez
        )
    )

    // 2. Borde efecto hielo: Un borde blanco fino que se desvanece.
    val glassBorder = BorderStroke(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.35f), // Borde superior brillante
                Color.Transparent // Se desvanece hacia abajo
            )
        )
    )

    Card(
        shape = RoundedCornerShape(glassCornerRadius),
        modifier = Modifier
            .width(150.dp)
            .height(165.dp),
        // El contenedor principal debe ser transparente
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = glassBorder, // Aplicamos el borde helado
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        // Usamos un Box para apilar capas y simular el cristal
        Box(modifier = Modifier.fillMaxSize()) {

            // --- CAPA 1: TEXTURA DE DIFUMINADO (Solo Android 12+) ---
            // Esto crea una capa interna borrosa que simula la textura esmerilada.
            // No difumina el fondo de la app en tiempo real, pero engaña al ojo.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // CORRECCIÓN AQUÍ:
                            // Usamos explícitamente 'android.graphics.RenderEffect'
                            // y 'android.graphics.Shader'
                            renderEffect = android.graphics.RenderEffect.createBlurEffect(
                                50f,
                                50f,
                                android.graphics.Shader.TileMode.MIRROR
                            ).asComposeRenderEffect()

                            alpha = 0.2f
                            clip = true
                            shape = RoundedCornerShape(glassCornerRadius)
                        }
                        .background(Color.White.copy(alpha = 0.1f))
                )
            }

            // --- CAPA 2: TINTE OSCURO ---
            // Esta capa aplica el color oscuro degradado sobre la textura.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(glassTintBrush)
            )

            // --- CAPA 3: CONTENIDO NÍTIDO ---
            // El texto y los ejercicios van encima de todo, perfectamente nítidos.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Título Día (Blanco translúcido)
                Text(
                    text = day.name,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Descripción (Blanco brillante)
                Text(
                    text = day.description.ifBlank { "Entreno" },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))
                // Línea divisoria sutil
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                val exerciseCount = day.exercises.size

                if (exerciseCount > 0) {
                    Text(
                        text = "Compuesto por $exerciseCount ejercicios",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Normal
                    )
                } else {
                    Text(
                        text = "Día de descanso",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------
// AUXILIAR: FILA DE EJERCICIO CLARA (Para fondo OSCURO)
// ---------------------------------------------------------
@Composable
fun ExerciseItemRowLight(exercise: RoutineExercise) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Nombre del ejercicio en BLANCO
        Text(
            text = exercise.name,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Reps en Azul
            Text(
                text = "${exercise.sets}x${exercise.reps}",
                color = BlueActive,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            // Descanso en Gris claro
            if (exercise.restTime.isNotEmpty()) {
                Text(
                    text = "(${exercise.restTime})",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            }
        }
    }
}



// ---------------------------------------------------------
// COMPONENTE 3: TARJETA "AÑADIR DÍA" (CORRECCIÓN ERROR 2)
// ---------------------------------------------------------
@Composable
fun AddDayCard(onClick: () -> Unit) {
    // Configuración general (Misma que la versión sutil anterior)
    val glassCornerRadius = 16.dp
    val darkGlassTintBase = Color(0xFF1E2B3A)

    // 1. DEGRADADO DE FONDO LEVE
    val lightGlassBrush = Brush.verticalGradient(
        colors = listOf(
            darkGlassTintBase.copy(alpha = 0.15f),
            darkGlassTintBase.copy(alpha = 0.35f)
        )
    )

    // 2. BORDE SUTIL
    val subtleBorder = BorderStroke(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.30f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    )

    Card(
        shape = RoundedCornerShape(glassCornerRadius),
        modifier = Modifier
            .width(100.dp)
            .height(155.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = subtleBorder,
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // --- CAPA 1: BLUR (Opcional) ---
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            renderEffect = android.graphics.RenderEffect.createBlurEffect(
                                30f, 30f,
                                android.graphics.Shader.TileMode.MIRROR
                            ).asComposeRenderEffect()
                            alpha = 0.2f
                            clip = true
                            shape = RoundedCornerShape(glassCornerRadius)
                        }
                        .background(Color.White.copy(alpha = 0.1f))
                )
            }

            // --- CAPA 2: TINTE SUAVE ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(lightGlassBrush)
            )

            // --- CAPA 3: CONTENIDO (Círculo + Icono) ---
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // A. El círculo de fondo sutil
                Box(
                    modifier = Modifier
                        .size(50.dp) // Tamaño del círculo
                        .background(
                            // Un blanco muy transparente para que parezca "grabado" en el cristal
                            color = Color.White.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                )

                // B. El icono "+" blanco encima
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir Día",
                    tint = Color.White, // Blanco puro
                    modifier = Modifier.size(26.dp) // Un poco más pequeño para que respire dentro del círculo
                )
            }
        }
    }
}