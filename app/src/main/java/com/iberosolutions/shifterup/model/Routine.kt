package com.iberosolutions.shifterup.data.model

data class Routine(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val totalDays: Int = 0,
    val days: List<RoutineDay> = emptyList(), // Lista de Días
    val createdAt: Long = System.currentTimeMillis()
)

data class RoutineDay(
    val dayIndex: Int = 0, // 1 para Lunes/Día 1, etc.
    val name: String = "", // "Día 1" o "Pecho/Bíceps"
    val description: String = "",
    val exercises: List<RoutineExercise> = emptyList()
)

data class RoutineExercise(
    val originalExerciseId: String = "",
    val name: String = "",
    val sets: String = "",
    val reps: String = "",
    val restTime: String = "" // formato min'seg"
)

// Modelo auxiliar para el Dropdown
data class ExerciseSelection(val id: String, val name: String)