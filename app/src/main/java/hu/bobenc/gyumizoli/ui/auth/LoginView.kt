package hu.bobenc.gyumizoli.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import hu.bobenc.gyumizoli.ui.common.ErrorDialog
import hu.bobenc.gyumizoli.ui.common.LoadingDialog

@Composable
fun LoginView(
    onLoginSuccess: () -> Unit,
    onSwitchToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color(0xFF2E7D32).copy(alpha = 0.85f), Color(0xFF2E7D32)), startY = 200f)))

        Card(
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth(0.9f).align(Alignment.Center),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Bejelentkezés", style = TextStyle(brush = Brush.linearGradient(listOf(Color(0xFF2E7D32), Color(0xFF4CAF50))), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold), modifier = Modifier.padding(bottom = 24.dp))

                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var isPasswordVisible by remember { mutableStateOf(false) }

                val shape = RoundedCornerShape(12.dp)
                val customColors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4CAF50), focusedLabelColor = Color(0xFF4CAF50), cursorColor = Color(0xFF4CAF50))

                OutlinedTextField(value = email, onValueChange = { email = it; viewModel.clearError() }, label = { Text("E-mail") }, leadingIcon = { Icon(Icons.Default.Email, null) }, modifier = Modifier.fillMaxWidth(), shape = shape, colors = customColors)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it; viewModel.clearError() }, label = { Text("Jelszó") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = { TextButton(onClick = { isPasswordVisible = !isPasswordVisible }) { Text(if (isPasswordVisible) "Elrejt" else "Mutat", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold) } },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(), shape = shape, colors = customColors
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) { Text("BEJELENTKEZÉS", color = Color.White, fontWeight = FontWeight.Bold) }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Még nem regisztráltál?", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onSwitchToRegister, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4CAF50))) { Text("Regisztráció") }
                Spacer(modifier = Modifier.height(140.dp))
            }
        }

        if (isLoading) LoadingDialog()
        if (error != null) ErrorDialog(errorMessage = error!!, onDismiss = { viewModel.clearError() })
    }
}