package hu.bobenc.gyumizoli.data.repository

import hu.bobenc.gyumizoli.data.model.Product
import hu.bobenc.gyumizoli.data.remote.api.ProductApiService
import hu.bobenc.gyumizoli.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productApi: ProductApiService
) {
    suspend fun getProducts(): Resource<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = productApi.getProducts()
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Termékek betöltése sikertelen!")
            } else {
                Resource.Error("HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Hálózati hiba!")
        }
    }

    suspend fun getProductShow(): Resource<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = productApi.getProductShow()
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Akciós termékek betöltése sikertelen!")
            } else {
                Resource.Error("HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Hálózati hiba!")
        }
    }
}