package com.iberosolutions.shifterup.domain.repository

import com.iberosolutions.shifterup.core.Resource
import com.iberosolutions.shifterup.data.model.Routine
import kotlinx.coroutines.flow.Flow

interface RoutinesRepository {
    fun getUserRoutines(userId: String): Flow<Resource<List<Routine>>>
    suspend fun addRoutine(routine: Routine): Resource<Boolean>
    suspend fun deleteRoutine(routineId: String): Resource<Boolean>
    suspend fun updateRoutine(routine: Routine)
}