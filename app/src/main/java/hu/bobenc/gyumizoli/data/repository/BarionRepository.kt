package hu.bobenc.gyumizoli.data.repository

import hu.bobenc.gyumizoli.data.remote.api.BarionApiService
import hu.bobenc.gyumizoli.data.remote.dto.BarionPaymentRequest
import hu.bobenc.gyumizoli.data.remote.dto.BarionPaymentResponse
import hu.bobenc.gyumizoli.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarionRepository @Inject constructor(
    private val barionApi: BarionApiService
) {
    suspend fun startPayment(request: BarionPaymentRequest): Resource<BarionPaymentResponse> = withContext(Dispatchers.IO) {
        try {
            val response = barionApi.startPayment(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Hiba a Barion hívásakor: HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Hálózati hiba a Barion fizetés indításakor!")
        }
    }
}