package hu.bobenc.gyumizoli.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import hu.bobenc.gyumizoli.data.model.Product
import hu.bobenc.gyumizoli.ui.common.ErrorDialog
import hu.bobenc.gyumizoli.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    viewModel: HomeViewModel = hiltViewModel(),
    onProductClick: (category: String, id: Int) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val fruits by viewModel.fruits.collectAsState()
    val vegetables by viewModel.vegetables.collectAsState()
    val sales by viewModel.sales.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.fetchProducts() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF9F9F9)),
                contentPadding = PaddingValues(bottom = 140.dp)
            ) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .statusBarsPadding(),
                        shape = RoundedCornerShape(50.dp),
                        color = Color.White,
                        shadowElevation = 4.dp,
                        onClick = onNavigateToSearch
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Keresés", tint = GreenPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Keresés termékek között...", color = Color.Gray, fontSize = 16.sp)
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 16.dp)
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Üdvözöljük a Frissesség Otthonában!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GreenAccentDark,
                            textAlign = TextAlign.Center,
                            lineHeight = 34.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Kezdőlapunkon mindent megtalál, amire egy igazi gyümölcs- és zöldségkedvelőnek szüksége lehet a legkiválóbb terményeket, praktikus vásárlási élményt és olyan szolgáltatásokat, melyekkel egyszerűbbé tesszük az egészséges életmódot.",
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (fruits.isNotEmpty()) {
                    item { ProductSection(title = "Gyümölcsök", products = fruits, onProductClick) }
                }
                if (vegetables.isNotEmpty()) {
                    item { ProductSection(title = "Zöldségek", products = vegetables, onProductClick) }
                }
                if (sales.isNotEmpty()) {
                    item { ProductSection(title = "Kiemelt Akciók", products = sales, onProductClick) }
                }
            }
        }

        if (error != null) {
            ErrorDialog(
                errorMessage = error!!,
                onDismiss = { viewModel.clearError() }
            )
        }
    }
}

@Composable
fun ProductSection(title: String, products: List<Product>, onProductClick: (String, Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = GreenAccentDark,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products) { product ->
                ProductCard(product = product, onClick = { onProductClick(product.category, product.id) })
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(200.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color.White)
            ) {
                AsyncImage(
                    model = product.image_url,
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                )

                if (product.promotion == 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(ErrorRed, RoundedCornerShape(50.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Akció!",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimaryDark,
                    fontSize = 18.sp,
                    maxLines = 1
                )

                Text(
                    text = product.description ?: "",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    maxLines = 2,
                    lineHeight = 18.sp,
                    modifier = Modifier.height(36.dp).padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                val originalPrice = product.price.toInt()
                val discountPrice = product.discount_price?.toInt()
                val unit = product.unit

                if (product.promotion == 1 && discountPrice != null && discountPrice < originalPrice) {
                    Column {
                        Text(
                            text = "Eredeti ár: $originalPrice Ft/$unit",
                            textDecoration = TextDecoration.LineThrough,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Akciós ár: $discountPrice Ft/$unit",
                            fontWeight = FontWeight.ExtraBold,
                            color = GreenPrimary,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    Column {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Ár: $originalPrice Ft/$unit",
                            fontWeight = FontWeight.ExtraBold,
                            color = GreenPrimary,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (product.stock == 0) {
                    Text(
                        text = "❌ Jelenleg elfogyott",
                        color = ErrorRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Button(
                    onClick = onClick,
                    enabled = product.stock > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Megnézem", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}