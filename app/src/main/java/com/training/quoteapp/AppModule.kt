package com.training.quoteapp

import android.content.Context
import androidx.room.Room
import com.training.quoteapp.data.QuoteDao
import com.training.quoteapp.data.QuoteDatabase
import com.training.quoteapp.repository.QuoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideMyDatabase(
        @ApplicationContext app: Context,
    ) = Room.databaseBuilder(
        app,
        QuoteDatabase::class.java,
        "quote_database"
    ).build()

    @Provides
    @Singleton
    fun provideMyDao(database: QuoteDatabase): QuoteDao {
        return database.quoteDao()
    }

    @Provides
    @Singleton
    fun provideMyApi():QuoteApi{
        return Retrofit.Builder()
            .baseUrl("https://api.api-ninjas.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(QuoteApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMyRepository(quoteDao:QuoteDao,quoteApi: QuoteApi):QuoteRepository{
        return QuoteRepository(quoteDao,quoteApi)
    }



}