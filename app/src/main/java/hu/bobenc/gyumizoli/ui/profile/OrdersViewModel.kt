package hu.bobenc.gyumizoli.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bobenc.gyumizoli.data.model.Order
import hu.bobenc.gyumizoli.data.model.Product
import hu.bobenc.gyumizoli.data.repository.AuthRepository
import hu.bobenc.gyumizoli.data.repository.OrderRepository
import hu.bobenc.gyumizoli.data.repository.ProductRepository
import hu.bobenc.gyumizoli.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val prodResult = productRepository.getProducts()
            if (prodResult is Resource.Success) {
                _products.value = prodResult.data ?: emptyList()
            }

            when (val userResult = authRepository.getCurrentUser()) {
                is Resource.Success -> {
                    val userId = userResult.data?.id
                    if (userId != null) {
                        when (val orderResult = orderRepository.getOrdersByUser(userId)) {
                            is Resource.Success -> {
                                _orders.value = orderResult.data ?: emptyList()
                            }
                            is Resource.Error -> _error.value = orderResult.message
                            else -> {}
                        }
                    }
                }
                is Resource.Error -> _error.value = "Bejelentkezés szükséges."
                else -> {}
            }
            _isLoading.value = false
        }
    }
}