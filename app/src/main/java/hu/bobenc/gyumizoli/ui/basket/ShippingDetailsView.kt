package hu.bobenc.gyumizoli.ui.basket

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bobenc.gyumizoli.ui.common.ErrorDialog
import hu.bobenc.gyumizoli.ui.common.LoadingDialog
import hu.bobenc.gyumizoli.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingDetailsView(
    viewModel: BasketViewModel,
    onBackClick: () -> Unit,
    onNavigateToPayment: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val orderStatus by viewModel.orderStatus.collectAsState()

    val gyumiGreen = Color(0xFF4CAF50)
    val bgColor = Color(0xFFF4F6F4)
    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = gyumiGreen,
        focusedLabelColor = gyumiGreen,
        cursorColor = gyumiGreen
    )

    LaunchedEffect(orderStatus) {
        if (orderStatus is Resource.Success<*>) {
            if (viewModel.paymentMethod == "card") {
                viewModel.resetOrderStatus()
                onNavigateToPayment()
            } else {
                viewModel.resetOrderStatus()
                onOrderSuccess()
            }
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = { Text("Pénztár", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Vissza") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor
                )
            )
        }
    ) { padding ->
        if (orderStatus is Resource.Loading<*>) {
            LoadingDialog(message = "Rendelés feldolgozása...")
        }
        if (orderStatus is Resource.Error<*>) {
            ErrorDialog(
                errorMessage = (orderStatus as Resource.Error<*>).message ?: "Ismeretlen hiba történt!",
                onDismiss = { viewModel.resetOrderStatus() }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "1. Szállítási adatok",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = { viewModel.loadUserData() },
                            label = { Text("Adataim betöltése", fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.PersonSearch, null, modifier = Modifier.size(18.dp), tint = gyumiGreen) },
                            colors = AssistChipDefaults.assistChipColors(leadingIconContentColor = gyumiGreen)
                        )
                        AssistChip(
                            onClick = { viewModel.clearShippingFields() },
                            label = { Text("Űrlap törlése", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.ClearAll, null, modifier = Modifier.size(18.dp), tint = Color.Gray) }
                        )
                    }
                    OutlinedTextField(
                        value = viewModel.name, onValueChange = { viewModel.name = it },
                        label = { Text("Teljes név") }, leadingIcon = { Icon(Icons.Default.PersonOutline, null) },
                        modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors, shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.phone, onValueChange = { viewModel.phone = it },
                        label = { Text("Telefonszám") }, leadingIcon = { Icon(Icons.Default.Phone, null) },
                        modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors, shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.email, onValueChange = { viewModel.email = it },
                        label = { Text("E-mail cím") }, leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors, shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.address, onValueChange = { viewModel.address = it },
                        label = { Text("Szállítási cím") }, leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                        modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors, shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "2. Fizetési mód",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    PaymentOptionRow(
                        title = "Bankkártya (Barion)",
                        subtitle = "Biztonságos, gyors online fizetés",
                        icon = Icons.Outlined.CreditCard,
                        isSelected = viewModel.paymentMethod == "card",
                        onClick = { viewModel.paymentMethod = "card" },
                        selectedColor = gyumiGreen
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = bgColor)

                    PaymentOptionRow(
                        title = "Készpénz (Utánvét)",
                        subtitle = "Fizetés a futárnak átvételkor",
                        icon = Icons.Outlined.LocalShipping,
                        isSelected = viewModel.paymentMethod == "cash",
                        onClick = { viewModel.paymentMethod = "cash" },
                        selectedColor = gyumiGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.submitOrder() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = gyumiGreen)
            ) {
                Text("MEGRENDELÉS ELKÜLDÉSE", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun PaymentOptionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(if (isSelected) selectedColor.copy(alpha = 0.05f) else Color.Transparent)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(if (isSelected) selectedColor else Color(0xFFEEEEEE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isSelected) Color.White else Color.Gray)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
        }

        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = selectedColor)
        )
    }
}