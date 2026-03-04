package hu.bobenc.gyumizoli.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.bobenc.gyumizoli.data.remote.api.AuthApiService
import hu.bobenc.gyumizoli.data.remote.api.BarionApiService
import hu.bobenc.gyumizoli.data.remote.api.OrderApiService
import hu.bobenc.gyumizoli.data.remote.api.ProductApiService
import hu.bobenc.gyumizoli.data.remote.interceptor.AuthInterceptor
import hu.bobenc.gyumizoli.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder().addInterceptor(authInterceptor).addInterceptor(logging).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApiService = retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApiService = retrofit.create(ProductApiService::class.java)

    @Provides
    @Singleton
    fun provideOrderApi(retrofit: Retrofit): OrderApiService = retrofit.create(OrderApiService::class.java)

    @Provides
    @Singleton
    fun provideBarionApiService(okHttpClient: OkHttpClient): BarionApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BARION_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BarionApiService::class.java)
    }
}