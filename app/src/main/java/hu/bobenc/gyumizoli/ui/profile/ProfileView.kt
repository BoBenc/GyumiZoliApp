package hu.bobenc.gyumizoli.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import hu.bobenc.gyumizoli.data.model.User
import hu.bobenc.gyumizoli.ui.common.ErrorDialog
import hu.bobenc.gyumizoli.ui.common.LoadingDialog
import hu.bobenc.gyumizoli.ui.auth.LoginView
import hu.bobenc.gyumizoli.ui.auth.RegisterView

@Composable
fun ProfileView(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToOrders: () -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var isLoginMode by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isLoggedIn && user != null) {
            LoggedInScreen(
                user = user!!,
                onLogout = { viewModel.logout() },
                onNavigateToOrders = onNavigateToOrders
            )
        }
        else {
            if (isLoginMode) {
                LoginView(
                    onLoginSuccess = { viewModel.reloadUser() },
                    onSwitchToRegister = { isLoginMode = false }
                )
            } else {
                RegisterView(
                    onRegisterSuccess = { viewModel.reloadUser() },
                    onSwitchToLogin = { isLoginMode = true }
                )
            }
        }
        if (isLoading) LoadingDialog()
        if (error != null) ErrorDialog(errorMessage = error!!, onDismiss = { viewModel.clearError() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoggedInScreen(
    user: User,
    onLogout: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    var showAddressDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9))
                .border(2.dp, Color(0xFF59b84c), CircleShape)
                .clickable {},
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(70.dp), tint = Color(0xFF59b84c))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Üdv, ${user.name}!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileInfoRow(label = "Teljes név:", value = user.name)
                ProfileInfoRow(label = "Email:", value = user.email)
                ProfileInfoRow(label = "Telefonszám:", value = user.phone ?: "Nincs megadva")
                ProfileInfoRow(label = "Cím:", value = user.address ?: "Nincs megadva")
                ProfileInfoRow(label = "Születési dátum:", value = user.birth_date ?: "Nincs megadva")
                ProfileInfoRow(label = "Jogosultság:", value = if (user.admin == 0) "Felhasználó" else "Admin")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Műveletek", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        ActionCardButton(icon = Icons.Default.ShoppingCart, title = "Rendelések", onClick = onNavigateToOrders)
        ActionCardButton(icon = Icons.Default.Lock, title = "Jelszó módosítása", onClick = { showPasswordDialog = true })
        ActionCardButton(icon = Icons.Default.LocationOn, title = "Cím módosítása", onClick = { showAddressDialog = true })

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
        ) {
            Text("Kijelentkezés", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(140.dp))
    }

    if (showAddressDialog) {
        var newAddress by remember { mutableStateOf(user.address ?: "") }
        var confirmNewAddress by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("Cím módosítása") },
            text = {
                Column {
                    OutlinedTextField(value = newAddress, onValueChange = { newAddress = it }, label = { Text("Új cím") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = confirmNewAddress, onValueChange = { confirmNewAddress = it }, label = { Text("Új cím megerősítése") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                val isEnabled = newAddress.isNotBlank() && newAddress == confirmNewAddress
                TextButton(onClick = { showAddressDialog = false }, enabled = isEnabled) { Text("Módosítás", color = if (isEnabled) Color(0xFF59b84c) else Color.Gray) }
            },
            dismissButton = { TextButton(onClick = { showAddressDialog = false }) { Text("Mégse", color = Color.Gray) } }
        )
    }

    if (showPasswordDialog) {
        var oldPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Jelszó módosítása") },
            text = {
                Column {
                    OutlinedTextField(value = oldPassword, onValueChange = { oldPassword = it }, label = { Text("Jelenlegi jelszó") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text("Új jelszó") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Új jelszó megerősítése") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                val isEnabled = newPassword.isNotBlank() && newPassword == confirmPassword
                TextButton(onClick = { showPasswordDialog = false }, enabled = isEnabled) { Text("Módosítás", color = if (isEnabled) Color(0xFF59b84c) else Color.Gray) }
            },
            dismissButton = { TextButton(onClick = { showPasswordDialog = false }) { Text("Mégse", color = Color.Gray) } }
        )
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, modifier = Modifier.width(130.dp))
        Text(text = value, color = Color.DarkGray, modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionCardButton(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, tint = Color(0xFF59b84c), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        }
    }
}