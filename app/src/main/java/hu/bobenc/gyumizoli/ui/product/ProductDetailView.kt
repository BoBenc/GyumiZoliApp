package hu.bobenc.gyumizoli.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import hu.bobenc.gyumizoli.data.model.BasketItem
import hu.bobenc.gyumizoli.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailView(
    category: String,
    productId: Int,
    onBackClick: () -> Unit,
    onItemAddedToCart: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    val product by viewModel.product.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var quantity by remember { mutableIntStateOf(1) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Termék részletei", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Vissza", tint = GreenPrimaryDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = GreenPrimaryDark
                )
            )
        },
        bottomBar = {
            product?.let { currentProduct ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .navigationBarsPadding()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(50.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { if (quantity > 1) quantity-- },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Csökkentés")
                                }

                                Text(
                                    text = "$quantity ${currentProduct.unit}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                IconButton(
                                    onClick = { if (quantity < currentProduct.stock) quantity++ },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Növelés")
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (currentProduct.stock > 0) {
                                    val finalPrice = (currentProduct.discount_price ?: currentProduct.price).toDouble()
                                    val item = BasketItem(
                                        product = currentProduct,
                                        quantity = quantity,
                                        totalPrice = finalPrice * quantity
                                    )
                                    viewModel.addToBasket(item)
                                    onItemAddedToCart()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("${currentProduct.name} kosárba került!")
                                    }
                                }
                            },
                            enabled = currentProduct.stock > 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentProduct.stock > 0) "Kosárba teszem" else "Elfogyott",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF9F9F9))
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = GreenPrimary, modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text(text = "Hiba: ${error!!}", color = ErrorRed, modifier = Modifier.align(Alignment.Center))
            } else {
                product?.let { currentProduct ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .background(Color.White)
                        ) {
                            AsyncImage(
                                model = currentProduct.image_url,
                                contentDescription = currentProduct.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentScale = ContentScale.Fit
                            )

                            if (currentProduct.promotion == 1) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(16.dp)
                                        .background(ErrorRed, RoundedCornerShape(50.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Akció!",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = category.uppercase(),
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = currentProduct.name,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GreenPrimaryDark,
                                lineHeight = 38.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val originalPrice = currentProduct.price.toInt()
                            val discountPrice = currentProduct.discount_price?.toInt()
                            val unit = currentProduct.unit

                            if (currentProduct.promotion == 1 && discountPrice != null && discountPrice < originalPrice) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$discountPrice Ft/$unit",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GreenPrimary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Eredeti ár: $originalPrice Ft/$unit",
                                        fontSize = 16.sp,
                                        color = Color.Gray,
                                        textDecoration = TextDecoration.LineThrough,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            } else {
                                Text(
                                    text = "$originalPrice Ft/$unit",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GreenPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val isAvailable = currentProduct.stock > 0
                                val stockColor = if (isAvailable) GreenPrimary else ErrorRed

                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(stockColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAvailable) "Raktáron: ${currentProduct.stock} $unit" else "Jelenleg nincs készleten",
                                    color = stockColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Termékleírás",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = GreenPrimaryDark
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = currentProduct.description ?: "A termékhez nem tartozik részletes leírás.",
                                color = Color.DarkGray,
                                fontSize = 15.sp,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}