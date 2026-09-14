package com.example.agrimexapp.datos

import androidx.room.Entity
import androidx.room.PrimaryKey

// La etiqueta @Entity le dice a la app que esto será una tabla local en el teléfono
@Entity(tableName = "equipos_computo")
data class Equipo(
    // @PrimaryKey indica que este es el identificador principal
    @PrimaryKey val id_equipo: Int,
    val marca: String,
    val modelo: String,
    val numero_serie: String,
    val estado: String
)