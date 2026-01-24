package com.iberosolutions.shifterup.data.model

data class Exercise(
    val id: String = "",
    val userId: String = "", // <--- ESTO ASEGURA LA PRIVACIDAD
    val name: String = "",
    val muscleGroup: String = ""
)