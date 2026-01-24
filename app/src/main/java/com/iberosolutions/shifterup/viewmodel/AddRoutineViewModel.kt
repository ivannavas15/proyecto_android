package com.iberosolutions.shifterup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberosolutions.shifterup.core.Resource
import com.iberosolutions.shifterup.data.model.*
import com.iberosolutions.shifterup.domain.repository.AuthRepository
import com.iberosolutions.shifterup.domain.repository.ExercisesRepository
import com.iberosolutions.shifterup.domain.repository.RoutinesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AddRoutinePhase {
    ROUTINE_INFO,
    DAY_INFO,
    EXERCISE_INFO
}

@HiltViewModel
class AddRoutineViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val routinesRepository: RoutinesRepository,
    private val exercisesRepository: ExercisesRepository
) : ViewModel() {

    private val _currentPhase = MutableStateFlow(AddRoutinePhase.ROUTINE_INFO)
    val currentPhase = _currentPhase.asStateFlow()

    private val _userExercises = MutableStateFlow<List<ExerciseSelection>>(emptyList())
    val userExercises = _userExercises.asStateFlow()

    // --- VARIABLES DE DATOS ---
    var routineName = MutableStateFlow("")
    var routineDescription = MutableStateFlow("") // <--- NUEVO
    var totalDaysRoutine = MutableStateFlow("")

    var dayDescription = MutableStateFlow("") // <--- NUEVO
    var exercisesCountForCurrentDay = MutableStateFlow("")

    var selectedExerciseName = MutableStateFlow("")
    var selectedExerciseId = MutableStateFlow("")
    var setsInput = MutableStateFlow("")
    var repsInput = MutableStateFlow("")
    var minInput = MutableStateFlow("")
    var secInput = MutableStateFlow("")

    private var currentDayIndex = 1
    private var targetDays = 0
    private var targetExercisesForDay = 0
    private val _completedDays = mutableListOf<RoutineDay>()
    private val _tempExercisesForDay = mutableListOf<RoutineExercise>()

    val headerTitle = MutableStateFlow("Nueva Rutina")

    init {
        loadUserExercises()
    }

    // --- VALIDACIÓN DEL BOTÓN (DIVIDIDA PARA QUE NO FALLE) ---

    // Paso 1: Nombre, Descripción y Días > 0
    private val isStep1Valid = combine(routineName, routineDescription, totalDaysRoutine) { name, desc, days ->
        name.isNotBlank() && desc.isNotBlank() && (days.toIntOrNull() ?: 0) > 0
    }

    // Paso 2: Descripción y Ejercicios > 0
    private val isStep2Valid = combine(dayDescription, exercisesCountForCurrentDay) { desc, count ->
        desc.isNotBlank() && (count.toIntOrNull() ?: 0) > 0
    }

    // Paso 3: ID Ejercicio, Series, Reps y Tiempo
    private val isStep3Valid = combine(selectedExerciseId, setsInput, repsInput, minInput, secInput) { id, sets, reps, min, sec ->
        id.isNotBlank() &&
                (sets.toIntOrNull() ?: 0) > 0 &&
                (reps.toIntOrNull() ?: 0) > 0 &&
                ((min.toIntOrNull() ?: 0) > 0 || (sec.toIntOrNull() ?: 0) > 0)
    }

    // Combinación final: Decide qué validador usar según la fase
    val isNextButtonEnabled = combine(_currentPhase, isStep1Valid, isStep2Valid, isStep3Valid) { phase, s1, s2, s3 ->
        when (phase) {
            AddRoutinePhase.ROUTINE_INFO -> s1
            AddRoutinePhase.DAY_INFO -> s2
            AddRoutinePhase.EXERCISE_INFO -> s3
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false) // Eagerly = Escuchar siempre

    // --------------------------------------------------------

    private fun loadUserExercises() {
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            exercisesRepository.getUserExercises(user.uid).collect { result ->
                if (result is Resource.Success) {
                    _userExercises.value = result.data?.map {
                        ExerciseSelection(it.id, it.name)
                    } ?: emptyList()
                }
            }
        }
    }

    fun onBackStep(onCloseScreen: () -> Unit) {
        when (_currentPhase.value) {
            AddRoutinePhase.ROUTINE_INFO -> onCloseScreen()
            AddRoutinePhase.DAY_INFO -> {
                if (currentDayIndex == 1) {
                    headerTitle.value = "Nueva Rutina"
                    _currentPhase.value = AddRoutinePhase.ROUTINE_INFO
                } else {
                    onCloseScreen()
                }
            }
            AddRoutinePhase.EXERCISE_INFO -> {
                _tempExercisesForDay.clear()
                headerTitle.value = "Configuración Día $currentDayIndex"
                _currentPhase.value = AddRoutinePhase.DAY_INFO
            }
        }
    }

    fun onNextStep(onFinished: () -> Unit) {
        when (_currentPhase.value) {
            AddRoutinePhase.ROUTINE_INFO -> {
                targetDays = totalDaysRoutine.value.toInt()
                currentDayIndex = 1
                prepareForNextDay()
            }
            AddRoutinePhase.DAY_INFO -> {
                targetExercisesForDay = exercisesCountForCurrentDay.value.toInt()
                prepareForNextExercise()
            }
            AddRoutinePhase.EXERCISE_INFO -> {
                addExerciseAndContinue(onFinished)
            }
        }
    }

    private fun prepareForNextDay() {
        if (currentDayIndex > targetDays) return

        exercisesCountForCurrentDay.value = ""
        dayDescription.value = ""
        _tempExercisesForDay.clear()

        headerTitle.value = "Día $currentDayIndex de $targetDays"
        _currentPhase.value = AddRoutinePhase.DAY_INFO
    }

    private fun prepareForNextExercise() {
        selectedExerciseName.value = ""
        selectedExerciseId.value = ""
        setsInput.value = ""
        repsInput.value = ""
        minInput.value = ""
        secInput.value = ""

        val currentExIndex = _tempExercisesForDay.size + 1
        headerTitle.value = "Día $currentDayIndex: Ejercicio $currentExIndex de $targetExercisesForDay"
        _currentPhase.value = AddRoutinePhase.EXERCISE_INFO
    }

    private fun addExerciseAndContinue(onFinished: () -> Unit) {
        val mins = minInput.value.ifBlank { "0" }.padStart(2, '0')
        val secs = secInput.value.ifBlank { "0" }.padStart(2, '0')

        val exercise = RoutineExercise(
            originalExerciseId = selectedExerciseId.value,
            name = selectedExerciseName.value,
            sets = setsInput.value,
            reps = repsInput.value,
            restTime = "${mins}'${secs}\""
        )
        _tempExercisesForDay.add(exercise)

        // LÓGICA DE DECISIÓN CRÍTICA
        if (_tempExercisesForDay.size < targetExercisesForDay) {
            // Aún quedan ejercicios en el día actual
            prepareForNextExercise()
        } else {
            // Se acabaron los ejercicios de este día -> Guardamos el Día
            val day = RoutineDay(
                dayIndex = currentDayIndex,
                name = "Día $currentDayIndex",
                description = dayDescription.value,
                exercises = _tempExercisesForDay.toList()
            )
            _completedDays.add(day)

            // Comprobamos si quedan más DÍAS
            if (currentDayIndex < targetDays) {
                // Quedan días -> Pasamos al siguiente
                currentDayIndex++
                prepareForNextDay()
            } else {
                // NO quedan días -> GUARDAR Y SALIR
                saveRoutineToFirebase(onSuccess = onFinished)
            }
        }
    }

    private fun saveRoutineToFirebase(onSuccess: () -> Unit) {
        val user = authRepository.getCurrentUser()

        if (user == null) {
            android.util.Log.e("ADD_ROUTINE", "ERROR: Usuario no logueado")
            return
        }

        // LOG DE DEBUG PARA VER SI LLEGA AQUI
        android.util.Log.d("ADD_ROUTINE", "Intentando guardar rutina: ${routineName.value} con ${targetDays} días")

        viewModelScope.launch {
            try {
                val newRoutine = Routine(
                    userId = user.uid,
                    name = routineName.value,
                    description = routineDescription.value,
                    totalDays = targetDays,
                    days = _completedDays.toList()
                )

                // ESTA ES LA LÍNEA QUE FALLA AHORA MISMO POR LA API
                routinesRepository.addRoutine(newRoutine)

                android.util.Log.d("ADD_ROUTINE", "¡ÉXITO! Rutina guardada.")
                onSuccess() // Cierra la pantalla

            } catch (e: Exception) {
                // AQUÍ VERÁS EL ERROR EN EL LOGCAT
                android.util.Log.e("ADD_ROUTINE", "ERROR CRÍTICO AL GUARDAR: ${e.message}")
                e.printStackTrace()

                // IMPORTANTE: Si quieres cerrar la pantalla AUNQUE falle (para probar navegación),
                // descomenta la siguiente línea:
                // onSuccess()
            }
        }
    }
}