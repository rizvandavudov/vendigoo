package com.example.vendigoo.data.entities.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vendigoo.data.entities.District
import com.example.vendigoo.data.entities.InitialBalance
import com.example.vendigoo.data.entities.Supplier
import com.example.vendigoo.data.entities.Transaction
import com.example.vendigoo.data.entities.dao.WholesaleDao


// data/database/WholesaleDatabase.kt
@Database(
    entities = [District::class, Supplier::class, InitialBalance::class, Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class WholesaleDatabase : RoomDatabase() {
    abstract fun wholesaleDao(): WholesaleDao

    companion object {
        @Volatile
        private var INSTANCE: WholesaleDatabase? = null

        fun getDatabase(context: Context): WholesaleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WholesaleDatabase::class.java,
                    "wholesale_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


}
