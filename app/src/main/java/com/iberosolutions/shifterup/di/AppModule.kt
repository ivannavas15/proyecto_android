package com.iberosolutions.shifterup.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.iberosolutions.shifterup.data.repository.AuthRepositoryImpl
import com.iberosolutions.shifterup.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.iberosolutions.shifterup.domain.repository.ExercisesRepository
import com.iberosolutions.shifterup.data.repository.ExercisesRepositoryImpl
import com.iberosolutions.shifterup.data.repository.RoutinesRepositoryImpl
import com.iberosolutions.shifterup.domain.repository.RoutinesRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Proveemos la instancia de FirebaseAuth para que Hilt la pueda inyectar
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    // Proveer Base de Datos (Firestore)
    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideExercisesRepository(firestore: FirebaseFirestore): ExercisesRepository {
        return ExercisesRepositoryImpl(firestore)
    }


    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideRoutinesRepository(firestore: FirebaseFirestore): RoutinesRepository {
        return RoutinesRepositoryImpl(firestore)
    }
}