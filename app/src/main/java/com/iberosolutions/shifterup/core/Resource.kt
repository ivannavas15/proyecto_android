package com.iberosolutions.shifterup.core

// Clase para manejar Resultados
//Para saber si el login fue bien o mal (sealed class).
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val exception: Exception) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}