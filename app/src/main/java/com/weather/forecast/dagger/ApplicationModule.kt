package com.weather.forecast.dagger

import android.content.Context
import androidx.room.Room
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.hokart.android.database.WeatherDatabase
import com.weather.forecast.BuildConfig
import com.weather.forecast.PreferenceManager
import com.weather.forecast.R
import com.weather.forecast.data.location.repository.LocationRepo
import com.weather.forecast.data.weather.remote.WeatherRemoteDataSource
import com.weather.forecast.data.weather.remote.WeatherRemoteDataSourceImpl
import com.weather.forecast.data.weather.remote.WeatherService
import com.weather.forecast.data.weather.repository.WeatherRepository
import com.weather.forecast.data.weather.repository.WeatherRepositoryImpl
import com.weather.forecast.helper.LocationListenerLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {


    @Provides
    @Singleton
    fun provideDB(@ApplicationContext appContext: Context): WeatherDatabase {
        return Room.databaseBuilder(
            appContext,
            WeatherDatabase::class.java, BuildConfig.DataBaseName
        ).build()
    }

    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext appContext: Context): PreferenceManager {
        return PreferenceManager(appContext)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

        val jsonBuilder = com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder()
        jsonBuilder.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        jsonBuilder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        jsonBuilder.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);


        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(
                JacksonConverterFactory.create(jsonBuilder.build())
            )
            .build()
    }

    @Provides
    @Singleton
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        if (!Places.isInitialized()) {
            Places.initialize(context, context.getString(R.string.google_map_key), Locale.US);
        }
        return Places.createClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationListenerLiveData(@ApplicationContext appContext: Context): LocationListenerLiveData =
        LocationListenerLiveData(appContext)

    @Provides
    @Singleton
    fun provideLocationRepo(
        @ApplicationContext appContext: Context,
        client: PlacesClient
    ): LocationRepo =
        LocationRepo(appContext, client)


    @Provides
    @Singleton
    fun provideWeatherService(retrofit: Retrofit): WeatherService =
        retrofit.create(WeatherService::class.java)


    @Provides
    @Singleton
    fun provideWeatherRemoteDataSource(weatherService: WeatherService): WeatherRemoteDataSource {
        return WeatherRemoteDataSourceImpl(weatherService)
    }

    @Provides
    @Singleton
    fun provideWeatherRepo(
        db: WeatherDatabase,
        weatherRemoteDataSource: WeatherRemoteDataSource
    ): WeatherRepository {
        return WeatherRepositoryImpl(db, weatherRemoteDataSource)
    }
}