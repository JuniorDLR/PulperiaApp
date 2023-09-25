package com.example.pulperiaapp.data.database.entitie

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pulperiaapp.data.database.dao.ClienteDao
import com.example.pulperiaapp.data.database.dao.CocaDao
import com.example.pulperiaapp.data.database.dao.CreditoDao
import com.example.pulperiaapp.data.database.dao.InventarioDao
import com.example.pulperiaapp.data.database.dao.PrixDao
import com.example.pulperiaapp.data.database.entitie.cliente.ClienteEntity
import com.example.pulperiaapp.data.database.entitie.coca.PrecioCocaEntity
import com.example.pulperiaapp.data.database.entitie.coca.VentaCocaEntity
import com.example.pulperiaapp.data.database.entitie.credito.CreditoEntity
import com.example.pulperiaapp.data.database.entitie.inventario.InventarioEntity
import com.example.pulperiaapp.data.database.entitie.prix.PrecioPrixEntity
import com.example.pulperiaapp.data.database.entitie.prix.VentaPrixEntity

@Database(
    entities = [
        ClienteEntity::class,
        PrecioCocaEntity::class,
        VentaCocaEntity::class,
        CreditoEntity::class,
        InventarioEntity::class,
        PrecioPrixEntity::class,
        VentaPrixEntity::class],
    version = 1
)
abstract class Database : RoomDatabase() {

    abstract fun getClienteDao(): ClienteDao

    abstract fun getCocaDao(): CocaDao

    abstract fun creditoDao(): CreditoDao

    abstract fun invetarioDao(): InventarioDao

    abstract fun prixDao(): PrixDao
}