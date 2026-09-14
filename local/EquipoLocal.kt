package com.example.agrimexapp.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos_locales")
data class EquipoLocal(
    @PrimaryKey val id_equipo: Int,
    val marca: String,
    val modelo: String,
    val numero_serie: String,
    val estado: String
)