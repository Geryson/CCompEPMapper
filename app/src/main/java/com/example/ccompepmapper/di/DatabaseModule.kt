package com.example.ccompepmapper.di

import android.content.Context
import com.example.ccompepmapper.data.EPMapperDatabase
import com.example.ccompepmapper.data.MapBaseDao
import com.example.ccompepmapper.data.MapLayerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideMapBaseDao(database: EPMapperDatabase): MapBaseDao {
        return database.mapBaseDao()
    }

    @Provides
    fun provideMapLayerDao(database: EPMapperDatabase): MapLayerDao {
        return database.mapLayerDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): EPMapperDatabase {
        return EPMapperDatabase.getDatabase(appContext)
    }

}