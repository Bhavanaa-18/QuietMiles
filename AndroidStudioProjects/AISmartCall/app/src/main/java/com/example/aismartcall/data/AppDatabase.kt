package com.example.aismartcall.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aismartcall.data.dao.CallLogDao
import com.example.aismartcall.data.entity.CallLogEntry

@Database(entities = [CallLogEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun callLogDao(): CallLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ai_smart_call_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
