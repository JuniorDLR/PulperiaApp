package com.example.pulperiaapp.data.database.entitie.credito

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pulperiaapp.data.database.dao.CreditoDao
import com.example.pulperiaapp.domain.amoroso.VentaAmorosoDetalle
import java.util.Date

@Entity("tbl_credito")
data class CreditoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,
    @ColumnInfo("cliente") val cliente: String,
    @ColumnInfo("producto") val producto: String,
    @ColumnInfo("cantidad") val cantidad: String,
    @ColumnInfo("precio_total") val precioTotal: Double,
    @ColumnInfo("fecha") val fecha: Long,
    @ColumnInfo("estado_pago") var estado_pago: Boolean
)


