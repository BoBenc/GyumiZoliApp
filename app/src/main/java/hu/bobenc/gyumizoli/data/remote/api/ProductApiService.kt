package hu.bobenc.gyumizoli.data.remote.api

import hu.bobenc.gyumizoli.data.model.Product
import retrofit2.Response
import retrofit2.http.GET

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("productshow")
    suspend fun getProductShow(): Response<List<Product>>
}