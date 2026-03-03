package hu.bobenc.gyumizoli.data.remote.api

import hu.bobenc.gyumizoli.data.model.Order
import hu.bobenc.gyumizoli.data.remote.dto.OrderRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderApiService {
    @GET("getcustomersorders")
    suspend fun getOrdersByUser(
        @Query("user_id") userId: Int
    ): Response<List<Order>>

    @POST("addorder")
    suspend fun submitOrder(
        @Body request: OrderRequest
    ): Response<Any>
}