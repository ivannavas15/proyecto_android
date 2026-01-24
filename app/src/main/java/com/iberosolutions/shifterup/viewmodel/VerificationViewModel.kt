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
class VerificationViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel(){

    var isVerified by mutableStateOf(false)
        private set

    init {
        // Al iniciarse la pantalla, empezamos a comprobar automáticamente
        startChecking()
    }

    private fun startChecking() {
        viewModelScope.launch {
            while (!isVerified) {
                // 1. Recargamos los datos del usuario
                repository.reloadUser()

                // 2. Comprobamos si ya está verificado
                val user = repository.getCurrentUser()
                if (user?.isEmailVerified == true) {
                    isVerified = true // ¡Esto disparará la navegación!
                }

                // 3. Esperamos 3 segundos antes de volver a comprobar
                delay(3000)
            }
        }
    }

}