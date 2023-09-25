package com.example.pulperiaapp.di

import android.content.Context
import androidx.room.Room
import com.example.pulperiaapp.data.database.entitie.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val NAME_DATABASE = "DistribuidoraAna"

    @Provides
    @Singleton
    fun providerRoom(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, Database::class.java, NAME_DATABASE).build()


    @Provides
    @Singleton
    fun providerDao(db: Database) = db.getPulperiaDao()
}