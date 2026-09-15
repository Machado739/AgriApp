package com.example.agrimexapp.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 1. Aumentamos la versión a 2
@Database(entities = [EquipoLocal::class], version = 2, exportSchema = false)
abstract class AgrimexDatabase : RoomDatabase() {
    abstract fun equipoDao(): EquipoDao

    companion object {
        @Volatile
        private var INSTANCE: AgrimexDatabase? = null

        fun getDatabase(context: Context): AgrimexDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgrimexDatabase::class.java,
                    "agrimex_database"
                )
                    // 2. Instrucción para recrear las tablas automáticamente al detectar cambios
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance

            }
        }
    }
}