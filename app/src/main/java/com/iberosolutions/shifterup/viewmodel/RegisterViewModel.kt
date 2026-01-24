package com.iberosolutions.shifterup.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.iberosolutions.shifterup.core.Resource
import com.iberosolutions.shifterup.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    // --- DATOS DEL REGISTRO ---
    var name by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var repeatPassword by mutableStateOf("")
        private set

    // --- VISIBILIDAD DE CONTRASEÑAS ---
    var isPasswordVisible by mutableStateOf(false)
        private set
    var isRepeatPasswordVisible by mutableStateOf(false)
        private set

    // Variable de estado del registro
    var signupFlow by mutableStateOf<Resource<FirebaseUser>?>(null)
        private set

    // --- VALIDACIONES ---
    val isNameValid: Boolean get() = name.isNotEmpty()
    val isEmailValid: Boolean get() = email.isNotEmpty() && isEmailFormatValid // Puedes añadir regex de email aquí
    val isPasswordValid: Boolean get() = password.isNotEmpty() && isPasswordLengthValid

    // El botón final solo se activa si las contraseñas coinciden y no están vacías
    val isRegisterEnabled: Boolean
        get() = repeatPassword.isNotEmpty() && password == repeatPassword

    // Lógica de Validación de Email
    private var validationJob: Job? = null

    // Validación de Contraseña
    private var passwordValidationJob: Job? = null

    // Error de contraseña debil
    var showPasswordError by mutableStateOf(false)
        private set

    var showEmailError by mutableStateOf(false)
        private set


    // Firebase exige mínimo 6 caracteres
    private val isPasswordLengthValid: Boolean
        get() = password.length >= 6

    // LÓGICA DE VALIDACIÓN (Regex)
    // Comprueba si el texto cumple el formato a@b.c
    private val isEmailFormatValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // Validación de Repetir Contraseña
    private var repeatPasswordValidationJob: Job? = null

    // Variable que controla cuándo mostrar el borde rojo y el texto
    var showRepeatPasswordError by mutableStateOf(false)
        private set

    // --- ACCIONES (Setters) ---
    fun onNameChanged(newValue: String) { name = newValue }
    fun onEmailChanged(newValue: String) {
        email = newValue

        // 1. Cancelamos el temporizador anterior
        validationJob?.cancel()

        // 2. Iniciamos uno nuevo
        validationJob = viewModelScope.launch {
            delay(1000) // Esperamos 1 segundo

            // 3. Si paró de escribir, validamos
            showEmailError = email.isNotEmpty() && !isEmailFormatValid
        }
    }
    fun onPasswordChanged(newValue: String) {
        password = newValue

        // Cancelamos espera anterior
        passwordValidationJob?.cancel()

        // Iniciamos nueva espera de 1 segundo
        passwordValidationJob = viewModelScope.launch {
            delay(1000)

            // Si escribió algo pero es muy corto (< 6) -> Error
            showPasswordError = password.isNotEmpty() && !isPasswordLengthValid
        }

    }
    fun onRepeatPasswordChanged(newValue: String) {
        repeatPassword = newValue
        // Cancelamos temporizador anterior
        repeatPasswordValidationJob?.cancel()

        // Iniciamos espera de 1 segundo
        repeatPasswordValidationJob = viewModelScope.launch {
            delay(1000)

            // Validamos: Si escribió algo Y no es igual a la original -> Error
            showRepeatPasswordError = repeatPassword.isNotEmpty() && password != repeatPassword
        }
    }

    fun togglePasswordVisibility() { isPasswordVisible = !isPasswordVisible }
    fun toggleRepeatPasswordVisibility() { isRepeatPasswordVisible = !isRepeatPasswordVisible }

    fun register() = viewModelScope.launch {
        signupFlow = Resource.Loading
        val result = repository.register(name, email, password)
        signupFlow = result

    }

}