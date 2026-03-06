package hu.bobenc.gyumizoli.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bobenc.gyumizoli.data.model.BasketItem
import hu.bobenc.gyumizoli.data.model.Product
import hu.bobenc.gyumizoli.data.repository.BasketRepository
import hu.bobenc.gyumizoli.data.repository.ProductRepository
import hu.bobenc.gyumizoli.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val basketRepository: BasketRepository
) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product = _product.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun addToBasket(item: BasketItem) {
        viewModelScope.launch {
            basketRepository.addToBasket(item)
        }
    }

    fun loadProduct(productId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = productRepository.getProducts()) {
                is Resource.Success -> {
                    val foundProduct = result.data?.find { it.id == productId }
                    if (foundProduct != null) {
                        _product.value = foundProduct
                    } else {
                        _error.value = "A termék nem található."
                    }
                }
                is Resource.Error -> {
                    _error.value = result.message ?: "Hiba történt a betöltés során."
                }
                is Resource.Loading -> { }
            }
            _isLoading.value = false
        }
    }
}