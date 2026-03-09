package hu.bobenc.gyumizoli.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bobenc.gyumizoli.data.model.User
import hu.bobenc.gyumizoli.data.repository.AuthRepository
import hu.bobenc.gyumizoli.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        checkLoginStatus()
    }

    fun reloadUser() {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = authRepository.getCurrentUser()) {
                is Resource.Success -> {
                    _user.value = result.data
                    _isLoggedIn.value = true
                }
                is Resource.Error -> {
                    _isLoggedIn.value = false
                    _user.value = null
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.logout()
            _isLoggedIn.value = false
            _user.value = null
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}