package hu.bobenc.gyumizoli.ui.productlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import hu.bobenc.gyumizoli.ui.home.HomeViewModel
import hu.bobenc.gyumizoli.ui.home.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListView(
    categorySlug: String,
    onBackClick: () -> Unit,
    onProductClick: (String, Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val fruits by viewModel.fruits.collectAsState()
    val vegetables by viewModel.vegetables.collectAsState()
    val sales by viewModel.sales.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val currentProducts = when (categorySlug) {
        "gyumolcs" -> fruits
        "zoldseg" -> vegetables
        "akcios" -> sales
        else -> fruits + vegetables + sales
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = when(categorySlug) {
                        "gyumolcs" -> "Gyümölcsök"
                        "zoldseg" -> "Zöldségek"
                        "akcios" -> "Akciós termékek"
                        else -> "Összes termék"
                    })
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Vissza")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text(
                    text = error!!,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 100.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(currentProducts) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.category, product.id) }
                        )
                    }
                }
            }
        }
    }
}