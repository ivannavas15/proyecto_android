package com.iberosolutions.shifterup.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberosolutions.shifterup.core.Resource
import com.iberosolutions.shifterup.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var email by mutableStateOf("")
        private set

    var resetFlow by mutableStateOf<Resource<Boolean>?>(null)
        private set

    // --- Validación de Email (Igual que en Login) ---
    private var validationJob: Job? = null
    var showEmailError by mutableStateOf(false)
        private set

    private val isEmailFormatValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val isButtonEnabled: Boolean
        get() = email.isNotEmpty() && isEmailFormatValid

    fun onEmailChanged(newValue: String) {
        email = newValue
        // Debounce para el error visual
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(1000)
            showEmailError = email.isNotEmpty() && !isEmailFormatValid
        }
    }

    // --- ACCIÓN PRINCIPAL ---
    fun sendRecoveryEmail() = viewModelScope.launch {
        resetFlow = Resource.Loading
        val result = repository.sendPasswordResetEmail(email)
        resetFlow = result
    }

    // Función para resetear el estado si vuelven a entrar
    fun clearState() {
        resetFlow = null
        email = ""
    }
}