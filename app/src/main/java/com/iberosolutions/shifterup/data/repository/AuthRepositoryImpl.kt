package com.iberosolutions.shifterup.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.iberosolutions.shifterup.core.Resource
import com.iberosolutions.shifterup.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, pass: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user

            if (user != null) {
                // VALIDACIÓN DE CORREO
                if (user.isEmailVerified) {
                    Resource.Success(user)
                } else {
                    // Si no está verificado, cerramos sesión y devolvemos error
                    firebaseAuth.signOut()
                    Resource.Error(Exception("Por favor, verifica tu correo electrónico antes de entrar."))
                }
            } else {
                Resource.Error(Exception("Error desconocido al iniciar sesión."))
            }
        } catch (e: Exception) {
            // --- CORRECCIÓN: UN SOLO CATCH CON LAS TRADUCCIONES ---
            val mensajeError = when (e) {
                is FirebaseAuthInvalidUserException -> "Tu cuenta ha sido inhabilitada o no existe."
                // Firebase agrupa "usuario no encontrado" y "contraseña mal" aquí por seguridad
                is FirebaseAuthInvalidCredentialsException -> "El correo electrónico o la contraseña son incorrectos."
                else -> "Error de conexión o desconocido. Inténtalo de nuevo."
            }
            Resource.Error(Exception(mensajeError))
        }
    }

    override suspend fun sendEmailVerification(): Resource<Boolean> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.sendEmailVerification().await()
                Resource.Success(true)
            } else {
                Resource.Error(Exception("No se ha encontrado el usuario para enviar el correo."))
            }
        } catch (e: Exception) {
            Resource.Error(Exception("Error al enviar correo de verificación."))
        }
    }

    override suspend fun register(name: String, email: String, pass: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user

            if (user != null) {
                // Nada más crear el usuario, enviamos el correo.
                user.sendEmailVerification().await()

                // AQUÍ PODRÍAS GUARDAR EL "NAME" EN FIRESTORE SI QUISIERAS
                Resource.Success(user)
            } else {
                Resource.Error(Exception("No se pudo crear el usuario."))
            }
        } catch (e: Exception) {
            // --- TRADUCCIONES DE REGISTRO ---
            val mensajeError = when (e) {
                is FirebaseAuthUserCollisionException -> "Este correo electrónico ya está registrado."
                is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil (mínimo 6 caracteres)."
                is FirebaseAuthInvalidCredentialsException -> "El formato del correo electrónico no es válido."
                else -> "Error al registrarse. Inténtalo de nuevo."
            }
            Resource.Error(Exception(mensajeError))
        }
    }

    override suspend fun reloadUser(): Resource<Boolean> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.reload().await() // Recargamos los datos desde el servidor
                Resource.Success(true)
            } else {
                Resource.Error(Exception("No hay usuario activo."))
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    // Función recuperar contraseña
    override suspend fun sendPasswordResetEmail(email: String): Resource<Boolean> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Resource.Success(true)
        } catch (e: Exception) {
            // --- TRADUCCIONES DE RECUPERACIÓN ---
            val mensaje = when(e) {
                is FirebaseAuthInvalidUserException -> "No existe ninguna cuenta con este correo."
                is FirebaseAuthInvalidCredentialsException -> "El formato del correo no es válido."
                else -> "Error al enviar el correo. Verifica tu conexión."
            }
            Resource.Error(Exception(mensaje))
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}