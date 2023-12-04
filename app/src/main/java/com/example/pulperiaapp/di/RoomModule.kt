package com.example.pulperiaapp.di

import android.content.Context
import androidx.room.Room
import com.example.pulperiaapp.data.Repository.VentaRepositorio
import com.example.pulperiaapp.data.database.dao.VentaCocaPrix
import com.example.pulperiaapp.data.database.entitie.Database
import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca
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
    fun providerCliente(db: Database) = db.getClienteDao()


    @Provides
    @Singleton
    fun providerCoca(db: Database) = db.getCocaDao()


    @Provides
    @Singleton
    fun providerCreditoDao(db: Database) = db.creditoDao()

    @Provides
    @Singleton
    fun providerInventario(db: Database) = db.invetarioDao()


    @Provides
    @Singleton
    fun providerPrix(db: Database) = db.prixDao()

    @Provides
    @Singleton
    fun providerVenta(db: Database) = db.venta()

    @Provides
    @Singleton
    fun providerBigCola(db: Database) = db.getBigColca()

}