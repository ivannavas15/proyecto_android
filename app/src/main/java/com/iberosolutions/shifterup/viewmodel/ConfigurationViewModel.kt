package com.iberosolutions.shifterup.ui.configuration

import androidx.lifecycle.ViewModel
import com.iberosolutions.shifterup.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    fun logout() {
        repository.logout()
    }
}