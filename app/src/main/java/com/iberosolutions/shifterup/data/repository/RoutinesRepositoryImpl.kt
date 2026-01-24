package com.iberosolutions.shifterup.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.iberosolutions.shifterup.core.Resource
import com.iberosolutions.shifterup.data.model.Routine
import com.iberosolutions.shifterup.domain.repository.RoutinesRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RoutinesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RoutinesRepository {

    override fun getUserRoutines(userId: String): Flow<Resource<List<Routine>>> = callbackFlow {
        trySend(Resource.Loading)
        val listener = firestore.collection("routines")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val routines = snapshot.toObjects(Routine::class.java)
                    trySend(Resource.Success(routines))
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateRoutine(routine: Routine) {
        try {
            firestore.collection("routines")
                .document(routine.id)
                .set(routine)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override suspend fun addRoutine(routine: Routine): Resource<Boolean> {
        return try {
            val ref = firestore.collection("routines").document()
            val newRoutine = routine.copy(id = ref.id)
            ref.set(newRoutine).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteRoutine(routineId: String): Resource<Boolean> {
        return try {
            firestore.collection("routines").document(routineId).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}