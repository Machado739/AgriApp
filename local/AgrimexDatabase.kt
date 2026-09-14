package com.example.agrimexapp.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EquipoLocal::class], version = 1, exportSchema = false)
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
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}