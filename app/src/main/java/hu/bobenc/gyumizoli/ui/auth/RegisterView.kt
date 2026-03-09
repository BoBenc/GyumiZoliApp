package hu.bobenc.gyumizoli.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import hu.bobenc.gyumizoli.ui.common.ErrorDialog
import hu.bobenc.gyumizoli.ui.common.LoadingDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(
    onRegisterSuccess: () -> Unit,
    onSwitchToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val registerSuccess by viewModel.registerSuccess.collectAsState()

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            onRegisterSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color(0xFF2E7D32).copy(alpha = 0.85f), Color(0xFF2E7D32)), startY = 200f)))

        Card(
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth(0.9f).heightIn(max = 700.dp).align(Alignment.Center).padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Regisztráció", style = TextStyle(brush = Brush.linearGradient(listOf(Color(0xFF2E7D32), Color(0xFF4CAF50))), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold), modifier = Modifier.padding(bottom = 24.dp))

                var name by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var phone by remember { mutableStateOf("") }
                var address by remember { mutableStateOf("") }
                var birthDate by remember { mutableStateOf("") }
                var showDatePicker by remember { mutableStateOf(false) }
                var password by remember { mutableStateOf("") }
                var passwordConfirm by remember { mutableStateOf("") }
                var isPassVisible by remember { mutableStateOf(false) }

                val shape = RoundedCornerShape(12.dp)
                val baseColors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4CAF50), focusedLabelColor = Color(0xFF4CAF50), cursorColor = Color(0xFF4CAF50))

                val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}\$".toRegex()
                val isPassValid = password.matches(passwordPattern)
                val passwordsMatch = password == passwordConfirm && password.isNotEmpty()
                val showPasswordError = passwordConfirm.isNotEmpty() && !passwordsMatch

                val confirmPassColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (showPasswordError) Color.Red else (if (passwordsMatch) Color(0xFF4CAF50) else Color.Gray),
                    unfocusedBorderColor = if (showPasswordError) Color.Red else (if (passwordsMatch) Color(0xFF4CAF50) else Color.Gray),
                    focusedLabelColor = if (showPasswordError) Color.Red else Color(0xFF4CAF50), cursorColor = Color(0xFF4CAF50)
                )

                OutlinedTextField(value = name, onValueChange = { if (it.length <= 10) { name = it; viewModel.clearError() } }, label = { Text("Teljes név (max 10 kar.)") }, leadingIcon = { Icon(Icons.Default.Person, null) }, modifier = Modifier.fillMaxWidth(), shape = shape, colors = baseColors)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = email, onValueChange = { email = it; viewModel.clearError() }, label = { Text("E-mail cím") }, leadingIcon = { Icon(Icons.Default.Email, null) }, modifier = Modifier.fillMaxWidth(), shape = shape, colors = baseColors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it; viewModel.clearError() }, label = { Text("Telefonszám") }, leadingIcon = { Icon(Icons.Default.Phone, null) }, modifier = Modifier.fillMaxWidth(), shape = shape, colors = baseColors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = address, onValueChange = { address = it; viewModel.clearError() }, label = { Text("Cím") }, leadingIcon = { Icon(Icons.Default.LocationOn, null) }, modifier = Modifier.fillMaxWidth(), shape = shape, colors = baseColors)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = if (birthDate.isNotEmpty()) "$birthDate ${calculateAge(birthDate)}" else "",
                    onValueChange = { }, label = { Text("Születési dátum") }, readOnly = true,
                    trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange, null) } },
                    modifier = Modifier.fillMaxWidth(), shape = shape, colors = baseColors
                )

                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    formatter.timeZone = TimeZone.getTimeZone("UTC")
                                    birthDate = formatter.format(Date(millis))
                                    viewModel.clearError()
                                }
                                showDatePicker = false
                            }) { Text("OK", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold) }
                        },
                        dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Mégse", color = Color.Gray) } }
                    ) { DatePicker(state = datePickerState) }
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password, onValueChange = { password = it; viewModel.clearError() }, label = { Text("Jelszó") }, leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = { TextButton(onClick = { isPassVisible = !isPassVisible }) { Text(if (isPassVisible) "Elrejt" else "Mutat", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold) } },
                    visualTransformation = if (isPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(), shape = shape, colors = baseColors,
                    supportingText = { if (password.isNotEmpty() && !isPassValid) Text("Min. 6 karakter, kisbetű, nagybetű, szám!", color = Color.Red, fontSize = 11.sp) }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = passwordConfirm, onValueChange = { passwordConfirm = it; viewModel.clearError() }, label = { Text("Jelszó újra") }, leadingIcon = { Icon(Icons.Default.Lock, null) },
                    visualTransformation = if (isPassVisible) VisualTransformation.None else PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = shape, colors = confirmPassColors,
                    supportingText = {
                        if (showPasswordError) { Text("A jelszavak nem egyeznek!", color = Color.Red, fontSize = 11.sp) }
                        else if (passwordsMatch) { Text("A jelszavak megegyeznek", color = Color(0xFF4CAF50), fontSize = 11.sp) }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                val isFormValid = email.isNotBlank() && name.isNotBlank() && phone.isNotBlank() && address.isNotBlank() && birthDate.isNotBlank() && isPassValid && passwordsMatch

                Button(
                    onClick = { viewModel.register(name, email, password, phone, address, birthDate) },
                    enabled = isFormValid, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), disabledContainerColor = Color.LightGray)
                ) { Text("REGISZTRÁCIÓ", color = Color.White, fontWeight = FontWeight.Bold) }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(" Már van fiókod? ", color = Color.Gray)
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = onSwitchToLogin, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4CAF50))) { Text("Bejelentkezés") }
                Spacer(modifier = Modifier.height(140.dp))
            }
        }

        if (isLoading) LoadingDialog()
        if (error != null) ErrorDialog(errorMessage = error!!, onDismiss = { viewModel.clearError() })
    }
}

fun calculateAge(birthDate: String): String {
    if (birthDate.isBlank()) return ""
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatter.parse(birthDate) ?: return ""
        val dob = Calendar.getInstance().apply { time = date }
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) { age-- }
        "($age éves)"
    } catch (e: Exception) { "" }
}