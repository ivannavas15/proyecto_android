package com.iberosolutions.shifterup.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.iberosolutions.shifterup.core.Resource
import com.iberosolutions.shifterup.data.model.Exercise
import com.iberosolutions.shifterup.domain.repository.ExercisesRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExercisesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ExercisesRepository {

    override fun getUserExercises(userId: String): Flow<Resource<List<Exercise>>> = callbackFlow {
        trySend(Resource.Loading)

        // FILTRO DE PRIVACIDAD: whereEqualTo("userId", userId)
        val listener = firestore.collection("exercises")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val exercises = snapshot.toObjects(Exercise::class.java)
                    trySend(Resource.Success(exercises))
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addExercise(exercise: Exercise): Resource<Boolean> {
        return try {
            val docRef = firestore.collection("exercises").document()
            val newExercise = exercise.copy(id = docRef.id)
            docRef.set(newExercise).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteExercise(exerciseId: String): Resource<Boolean> {
        return try {
            firestore.collection("exercises").document(exerciseId).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}