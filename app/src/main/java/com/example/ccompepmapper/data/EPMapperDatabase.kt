package com.example.ccompepmapper.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MapBase::class, MapLayer::class], version = 2, exportSchema = false)
abstract class EPMapperDatabase : RoomDatabase() {
    abstract fun mapBaseDao(): MapBaseDao
    abstract fun mapLayerDao(): MapLayerDao
    companion object {
        @Volatile
        private var INSTANCE: EPMapperDatabase? = null

        fun getDatabase(context: Context): EPMapperDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, EPMapperDatabase::class.java, "epmapperdatabase.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}