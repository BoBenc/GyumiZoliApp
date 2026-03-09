package hu.bobenc.gyumizoli.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.bobenc.gyumizoli.data.model.Order
import hu.bobenc.gyumizoli.data.model.OrderItem
import hu.bobenc.gyumizoli.util.Constants.BASE_URL
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersView(
    onBackClick: () -> Unit,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val allProducts by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedOrderItems by remember { mutableStateOf<List<OrderItem>?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedOrderItems == null) "Rendeléseim" else "Megrendelt termékek", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { if (selectedOrderItems != null) selectedOrderItems = null else onBackClick() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Vissza")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5))) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF59b84c))
            } else if (error != null) {
                Text(text = error!!, color = Color.Red, modifier = Modifier.align(Alignment.Center))
            } else if (selectedOrderItems != null) {
                OrderItemsList(items = selectedOrderItems!!)
            } else if (orders.isEmpty()) {
                EmptyOrdersState()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp)) {
                    items(orders) { order ->
                        OrderCard(order = order, onShowItems = { jsonString ->
                            try {
                                val type = object : TypeToken<List<OrderItem>>() {}.type
                                val basicItems: List<OrderItem> = Gson().fromJson(jsonString, type)
                                val enriched = basicItems.map { item ->
                                    val match = allProducts.find { it.id == item.product.id }
                                    item.copy(
                                        product = item.product.copy(
                                            name = match?.name ?: "Ismeretlen termék",
                                            image_url = match?.image_url,
                                            price = match?.price ?: item.product.price,
                                            discount_price = match?.discount_price,
                                            unit = match?.unit ?: item.product.unit
                                        )
                                    )
                                }
                                selectedOrderItems = enriched
                            } catch (e: Exception) { e.printStackTrace() }
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order, onShowItems: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Rendelés #${order.id}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = translateStatus(order.status), color = getStatusColor(order.status), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Dátum: ${formatOrderDate(order.created_at)}", color = Color.Gray, fontSize = 14.sp)
            Text(text = "Fizetés: ${if (order.payment_method == "card") "Bankkártya" else "Készpénz"}", color = Color.Gray, fontSize = 14.sp)
            Text(text = "Cím: ${order.delivery_address}", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${order.totalPrice} Ft", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF59b84c))
                Button(onClick = { onShowItems(order.items) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF59b84c))) {
                    Text("Termékek")
                }
            }
        }
    }
}

@Composable
private fun OrderItemsList(items: List<OrderItem>) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp)) {
        items(items) { item ->
            val rawImg = item.product.image_url.orEmpty()
            val fullImgUrl = if (rawImg.startsWith("http")) {
                rawImg
            } else {
                BASE_URL.replace("/api/", "/") + rawImg.removePrefix("/")
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)) {
                        AsyncImage(model = fullImgUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.product.name, fontWeight = FontWeight.Bold)
                        Text(text = "Mennyiség: ${item.quantity} ${item.product.unit}", color = Color.Gray, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(4.dp))

                        val originalPrice = item.product.price.toDouble()
                        val discountPrice = item.product.discount_price?.toDouble()

                        if (discountPrice != null && discountPrice < originalPrice) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${(discountPrice * item.quantity).toInt()} Ft",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF59b84c),
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${(originalPrice * item.quantity).toInt()} Ft",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                            Text(
                                text = "Akciós termék",
                                color = Color(0xFFE53935),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        } else {
                            Text(
                                text = "${(originalPrice * item.quantity).toInt()} Ft",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF59b84c),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyOrdersState() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Text("Nincsenek rendelések.", color = Color.Gray)
    }
}
private fun translateStatus(s: String) = when(s) { "pending" -> "Függőben"; "delivered" -> "Kiszállítva"; "cancelled" -> "Törölve"; else -> s }
private fun getStatusColor(s: String) = when(s) { "delivered" -> Color(0xFF5bc24f); "cancelled" -> Color.Red; else -> Color.Gray }

private fun formatOrderDate(rawDate: String): String {
    return try {
        val cleanDate = rawDate.substringBefore(".")
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(cleanDate)
        val formatter = SimpleDateFormat("yyyy. MM. dd. HH:mm", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()

        if (date != null) formatter.format(date) else rawDate
    } catch (e: Exception) {
        rawDate.replace("T", " ").substringBefore(".")
    }
}