package com.iberosolutions.shifterup.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberosolutions.shifterup.core.Resource
import com.iberosolutions.shifterup.data.model.Routine
import com.iberosolutions.shifterup.domain.repository.AuthRepository
import com.iberosolutions.shifterup.domain.repository.RoutinesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutinesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val routinesRepository: RoutinesRepository
) : ViewModel() {

    var searchText by mutableStateOf("")
        private set

    private val _searchQuery = MutableStateFlow("")
    private val _allRoutines = MutableStateFlow<List<Routine>>(emptyList())

    // Lógica de filtrado en tiempo real
    val routinesListState = combine(_allRoutines, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadRoutines()
    }

    private fun loadRoutines() {
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            routinesRepository.getUserRoutines(user.uid).collect { result ->
                if (result is Resource.Success) {
                    _allRoutines.value = result.data ?: emptyList()
                }
            }
        }
    }

    fun onSearch(query: String) {
        searchText = query
        _searchQuery.value = query
    }

    fun createRoutine(name: String) { // <--- Quita 'description: String' de aquí
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            val newRoutine = Routine(
                userId = user.uid,
                name = name,
                totalDays = 1 // Valor por defecto temporal para que no falle
                // description = description <--- BORRA ESTA LÍNEA (Causa del error rojo)
            )
            routinesRepository.addRoutine(newRoutine)
        }
    }

    fun deleteRoutine(id: String) {
        viewModelScope.launch {
            routinesRepository.deleteRoutine(id)
        }
    }

    fun updateRoutine(routine: Routine) {
        viewModelScope.launch {
            routinesRepository.updateRoutine(routine)
        }
    }
}