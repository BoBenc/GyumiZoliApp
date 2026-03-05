package hu.bobenc.gyumizoli.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bobenc.gyumizoli.data.model.Product
import hu.bobenc.gyumizoli.data.repository.ProductRepository
import hu.bobenc.gyumizoli.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _fruits = MutableStateFlow<List<Product>>(emptyList())
    val fruits = _fruits.asStateFlow()

    private val _vegetables = MutableStateFlow<List<Product>>(emptyList())
    val vegetables = _vegetables.asStateFlow()

    private val _sales = MutableStateFlow<List<Product>>(emptyList())
    val sales = _sales.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = productRepository.getProducts()) {
                is Resource.Success -> {
                    val allProducts = result.data ?: emptyList()

                    _sales.value = allProducts.filter { it.promotion == 1 }
                    _fruits.value = allProducts.filter { it.category.equals("Gyümölcs", ignoreCase = true) && it.promotion != 1 }
                    _vegetables.value = allProducts.filter { it.category.equals("Zöldség", ignoreCase = true) && it.promotion != 1 }
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> { }
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}