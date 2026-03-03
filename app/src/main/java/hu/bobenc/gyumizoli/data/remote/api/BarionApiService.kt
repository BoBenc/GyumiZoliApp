package hu.bobenc.gyumizoli.data.remote.api

import hu.bobenc.gyumizoli.data.remote.dto.BarionPaymentRequest
import hu.bobenc.gyumizoli.data.remote.dto.BarionPaymentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BarionApiService {
    @POST("v2/Payment/Start")
    suspend fun startPayment(
        @Body request: BarionPaymentRequest
    ): Response<BarionPaymentResponse>
}