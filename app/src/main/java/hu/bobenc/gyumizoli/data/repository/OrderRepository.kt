package hu.bobenc.gyumizoli.data.repository

import hu.bobenc.gyumizoli.data.model.Order
import hu.bobenc.gyumizoli.data.remote.api.OrderApiService
import hu.bobenc.gyumizoli.data.remote.dto.OrderRequest
import hu.bobenc.gyumizoli.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderApi: OrderApiService
) {
    suspend fun getOrdersByUser(userId: Int): Resource<List<Order>> = withContext(Dispatchers.IO) {
        try {
            val response = orderApi.getOrdersByUser(userId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Üres válasz érkezett a szervertől!")
            } else {
                Resource.Error("HTTP Hiba: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Hálózati hiba történt a rendelések betöltésekor!")
        }
    }

    suspend fun submitOrder(request: OrderRequest): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val response = orderApi.submitOrder(request)
            if (response.isSuccessful) {
                val responseMap = response.body() as? Map<*, *>
                var rawId: String? = null
                if (responseMap != null) {
                    rawId = responseMap["id"]?.toString() ?: responseMap["order_id"]?.toString()
                    if (rawId == null) {
                        val orderBlock = responseMap["order"]
                        if (orderBlock is Map<*, *>) {
                            rawId = orderBlock["id"]?.toString() ?: orderBlock["order_id"]?.toString()
                        }
                    }
                    if (rawId == null) {
                        val dataBlock = responseMap["data"]
                        if (dataBlock is Map<*, *>) {
                            rawId = dataBlock["id"]?.toString() ?: dataBlock["order_id"]?.toString()
                        } else if (dataBlock is Number || dataBlock is String) {
                            rawId = dataBlock.toString()
                        }
                    }
                }
                val cleanId = rawId?.substringBefore(".")
                val finalId = cleanId ?: "REND-${System.currentTimeMillis()}"
                Resource.Success(finalId)
            } else {
                Resource.Error("Hiba a rendelés leadásakor (HTTP ${response.code()})")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Hálózati hiba történt a rendelés során!")
        }
    }
}