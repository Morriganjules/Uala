package com.example.uala.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    private val Context.dataStore by preferencesDataStore(name = "favorites_prefs")

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context) = context.dataStore
}