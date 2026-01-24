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
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository // Aquí se llama "repository"
) : ViewModel(){

    // --- ESTADO (Datos) ---
    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isPasswordVisible by mutableStateOf(false)
        private set

    var loginFlow by mutableStateOf<Resource<FirebaseUser>?>(null)
        private set

    val isLoginEnabled: Boolean
        get() = password.isNotEmpty()

    // LÓGICA DE VALIDACIÓN (Regex)
    private val isEmailFormatValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private var validationJob: Job? = null

    // --- LÓGICA (Validaciones) ---
    val isEmailNextEnabled: Boolean
        get() = email.isNotEmpty() && isEmailFormatValid

    var showEmailError by mutableStateOf(false)
        private set

    // --- FUNCIONES (Acciones) ---

    // *** AQUÍ ESTABA EL ERROR CORREGIDO ***
    fun isUserLoggedIn(): Boolean {
        val user = repository.getCurrentUser() // Usamos 'repository'
        return user != null && user.isEmailVerified
    }

    fun onPasswordChanged(newPassword: String) {
        password = newPassword
        if (loginFlow is Resource.Error) {
            loginFlow = null
        }
    }

    fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
    }

    fun login() = viewModelScope.launch {
        loginFlow = Resource.Loading
        val result = repository.login(email, password)
        loginFlow = result
    }

    fun onEmailChanged(newEmail: String) {
        email = newEmail
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(1000)
            showEmailError = email.isNotEmpty() && !isEmailFormatValid
        }
    }
}