package com.example.agrimexapp.datos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// @Dao le indica a Room que este es nuestro "control remoto"
@Dao
interface EquipoDao {

    // @Query nos permite hacer una consulta para leer los datos guardados en el teléfono
    @Query("SELECT * FROM equipos_computo")
    fun obtenerEquiposLocal(): List<Equipo>

    // @Insert guarda una lista de equipos en la base de datos local
    // REPLACE significa que si un equipo ya existe, lo actualizará con la nueva información
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardarEquipos(equipos: List<Equipo>)
}