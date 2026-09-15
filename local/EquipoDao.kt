package com.example.agrimexapp.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlin.jvm.JvmSuppressWildcards // Nueva importación

@Dao
interface EquipoDao {
    @Query("SELECT * FROM equipos_locales")
    suspend fun obtenerTodos(): @JvmSuppressWildcards List<EquipoLocal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarEquipos(equipos: List<EquipoLocal>): @JvmSuppressWildcards List<Long>

    @Query("SELECT * FROM equipos_locales WHERE id_departamento = :idDepto")
    suspend fun obtenerPorDepartamento(idDepto: Int): @JvmSuppressWildcards List<EquipoLocal>
}
