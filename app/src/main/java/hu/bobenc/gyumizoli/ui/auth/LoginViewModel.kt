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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _error.value = "Kérjük, töltse ki az összes mezőt!"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = authRepository.login(email, pass)) {
                is Resource.Success -> {
                    _loginSuccess.value = true
                }
                is Resource.Error -> {
                    _error.value = result.message
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