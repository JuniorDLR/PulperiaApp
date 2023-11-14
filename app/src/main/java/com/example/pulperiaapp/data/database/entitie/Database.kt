package com.example.pulperiaapp.data.database.entitie
//_+uqd<nK-@NK$R8GT2i&

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pulperiaapp.data.database.dao.BigDao
import com.example.pulperiaapp.data.database.dao.ClienteDao
import com.example.pulperiaapp.data.database.dao.CocaDao
import com.example.pulperiaapp.data.database.dao.CreditoDao
import com.example.pulperiaapp.data.database.dao.InventarioDao
import com.example.pulperiaapp.data.database.dao.PrixDao
import com.example.pulperiaapp.data.database.dao.VentaCocaPrix


@Database(
    entities = [ClienteEntity::class, PrecioCocaEntity::class, CreditoEntity::class, InventarioEntity::class, PrecioPrixEntity::class, VentaPrixCoca::class, PrecioBigCola::class],
    version = 1, exportSchema = false
)
abstract class Database : RoomDatabase() {

    abstract fun getClienteDao(): ClienteDao

    abstract fun getCocaDao(): CocaDao

    abstract fun creditoDao(): CreditoDao

    abstract fun invetarioDao(): InventarioDao

    abstract fun prixDao(): PrixDao

    abstract fun venta(): VentaCocaPrix

    abstract fun getBigColca(): BigDao
}