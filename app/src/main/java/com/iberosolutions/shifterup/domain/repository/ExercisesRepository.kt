package com.iberosolutions.shifterup.domain.repository

import com.iberosolutions.shifterup.core.Resource
import com.iberosolutions.shifterup.data.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExercisesRepository {
    fun getUserExercises(userId: String): Flow<Resource<List<Exercise>>>
    suspend fun addExercise(exercise: Exercise): Resource<Boolean>
    suspend fun deleteExercise(exerciseId: String): Resource<Boolean>
}