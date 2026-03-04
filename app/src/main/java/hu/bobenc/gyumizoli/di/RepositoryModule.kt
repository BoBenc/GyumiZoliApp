package hu.bobenc.gyumizoli.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.bobenc.gyumizoli.data.remote.api.OrderApiService
import hu.bobenc.gyumizoli.data.remote.api.ProductApiService
import hu.bobenc.gyumizoli.data.repository.OrderRepository
import hu.bobenc.gyumizoli.data.repository.ProductRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideProductRepository(
        apiService: ProductApiService
    ): ProductRepository = ProductRepository(apiService)

    @Provides
    @Singleton
    fun provideOrderRepository(
        apiService: OrderApiService
    ): OrderRepository = OrderRepository(apiService)
}