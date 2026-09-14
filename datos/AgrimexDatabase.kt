package com.example.agrimexapp.datos

import androidx.room.Database
import androidx.room.RoomDatabase

// @Database indica que este archivo es la base de datos principal.
// entities: Aquí le pasamos una lista con las tablas que creamos (por ahora solo Equipo).
// version = 1: Como es la primera vez que creamos la base de datos, es la versión 1.
@Database(entities = [Equipo::class], version = 1)
abstract class AgrimexDatabase : RoomDatabase() {

    // Aquí conectamos nuestro "control remoto" (el DAO) para poder usarlo más adelante
    abstract fun equipoDao(): EquipoDao

}