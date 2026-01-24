package com.iberosolutions.shifterup.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.iberosolutions.shifterup.core.Resource

interface AuthRepository {
    // Función para Login
    suspend fun login(email: String, pass: String): Resource<FirebaseUser>

    // Función para Registro
    suspend fun register(name: String, email: String, pass: String): Resource<FirebaseUser>

    // Función para cerrar sesión (logout)
    fun logout()

    // Obtener usuario actual (si ya está logueado)
    fun getCurrentUser(): FirebaseUser?

    // Función de envio de correo electrónico de verificación
    suspend fun sendEmailVerification(): Resource<Boolean>

    // Función para forzar la actualización de datos del usuario
    suspend fun reloadUser(): Resource<Boolean>

    // FUNCIÓN para recuperar contraseña
    suspend fun sendPasswordResetEmail(email: String): Resource<Boolean>
}