package hu.bobenc.gyumizoli.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bobenc.gyumizoli.data.repository.AuthRepository
import hu.bobenc.gyumizoli.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess = _registerSuccess.asStateFlow()

    fun register(name: String, email: String, pass: String, phone: String, address: String, birthDate: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank() || phone.isBlank() || address.isBlank() || birthDate.isBlank()) {
            _error.value = "Minden kötelező mezőt ki kell tölteni!"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = authRepository.register(name, email, pass, phone, address, birthDate)) {
                is Resource.Success -> {
                    loginAfterRegister(email, pass)
                }
                is Resource.Error -> {
                    _error.value = result.message
                    _isLoading.value = false
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun loginAfterRegister(email: String, pass: String) {
        viewModelScope.launch {
            when (val result = authRepository.login(email, pass)) {
                is Resource.Success -> {
                    _registerSuccess.value = true
                }
                is Resource.Error -> {
                    _error.value = "Sikeres regisztráció, de a bejelentkezés sikertelen: ${result.message}"
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}