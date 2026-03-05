package hu.bobenc.gyumizoli.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bobenc.gyumizoli.data.model.Product
import hu.bobenc.gyumizoli.data.repository.ProductRepository
import hu.bobenc.gyumizoli.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())

    val searchResults = combine(_searchQuery, _allProducts) { query, products ->
        if (query.isBlank()) {
            emptyList()
        } else {
            products.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        (product.description?.contains(query, ignoreCase = true) == true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadAllProducts()
    }

    private fun loadAllProducts() {
        viewModelScope.launch {
            val result = repository.getProducts()

            if (result is Resource.Success && result.data != null) {
                _allProducts.value = result.data
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }
}