package com.example.pulperiaapp.data.database.entitie


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("tbl_precio_prix")
data class PrecioPrixEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,
    @ColumnInfo("producto") val producto: String,
    @ColumnInfo("precio") val precio: Double
)