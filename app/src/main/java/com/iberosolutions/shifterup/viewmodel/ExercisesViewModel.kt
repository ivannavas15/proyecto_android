package com.iberosolutions.shifterup.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberosolutions.shifterup.core.Resource
import com.iberosolutions.shifterup.data.model.Exercise
import com.iberosolutions.shifterup.domain.repository.AuthRepository
import com.iberosolutions.shifterup.domain.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val exercisesRepository: ExercisesRepository
) : ViewModel() {

    // Texto del buscador
    var searchText by mutableStateOf("")
        private set

    // Lista completa de Firebase
    private val _allExercises = MutableStateFlow<List<Exercise>>(emptyList())

    // Estado de carga/error
    var isLoading by mutableStateOf(false)
        private set

    // LISTA FILTRADA (Esta es la que observa la UI)
    // Combina la lista completa con el texto de búsqueda automáticamente
    val visibleExercises = combine(_allExercises, MutableStateFlow(searchText)) { list, query ->
        if (query.isBlank()) list
        else list.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadMyExercises()
    }

    private fun loadMyExercises() {
        val user = authRepository.getCurrentUser()
        if (user != null) {
            viewModelScope.launch {
                exercisesRepository.getUserExercises(user.uid).collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            _allExercises.value = result.data ?: emptyList()
                            isLoading = false
                        }
                        is Resource.Loading -> isLoading = true
                        is Resource.Error -> isLoading = false
                    }
                }
            }
        }
    }

    fun onSearchTextChange(text: String) {
        searchText = text
        // Forzamos actualización del flujo combinando (truco sencillo)
        // Nota: En el combine de arriba usé un Flow, aquí actualizo el estado
    }

    // Método auxiliar para que el combine reaccione al cambio de texto
    // (En una implementación más estricta usaríamos snapshotFlow, pero esto simplifica)
    // Para simplificar al máximo, actualizaremos _allExercises filtrando en UI o usando un flow de texto:
    // CORRECCIÓN: Vamos a usar un MutableStateFlow para el query para que sea reactivo.
    private val _searchQuery = MutableStateFlow("")

    fun onSearch(text: String) {
        searchText = text
        _searchQuery.value = text
    }

    // Re-definición correcta del combine:
    val exercisesListState = combine(_allExercises, _searchQuery) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            // CAMBIO AQUÍ: Filtramos por nombre O por grupo muscular
            list.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.muscleGroup.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun createExercise(name: String, muscleGroup: String) {
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            val newExercise = Exercise(
                userId = user.uid,
                name = name,
                muscleGroup = muscleGroup // Guardamos el grupo
            )
            exercisesRepository.addExercise(newExercise)
        }
    }

    // NUEVO: Función para borrar
    fun deleteExercise(exerciseId: String) {
        viewModelScope.launch {
            exercisesRepository.deleteExercise(exerciseId)
        }
    }
}