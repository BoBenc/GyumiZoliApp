package hu.bobenc.gyumizoli.ui.basket

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaymentView(
    viewModel: BasketViewModel,
    onStartBarionPayment: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val clientSecret by viewModel.barionClientSecret.collectAsState()
    val paymentError by viewModel.paymentError.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startBarionPayment()
    }

    LaunchedEffect(clientSecret) {
        if (clientSecret != null) {
            onStartBarionPayment(clientSecret!!)
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (paymentError != null) {
                Icon(Icons.Default.ErrorOutline, null, tint = Color.Red, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Hiba történt!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(paymentError!!, textAlign = TextAlign.Center, color = Color.Gray)
                Button(onClick = onBackClick, modifier = Modifier.padding(top = 20.dp)) {
                    Text("Vissza a kosárhoz")
                }
            } else {
                CircularProgressIndicator(color = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = if (clientSecret == null) "Biztonságos kapcsolat létrehozása..."
                    else "Átirányítás a banki felületre...",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }
        }
    }
}