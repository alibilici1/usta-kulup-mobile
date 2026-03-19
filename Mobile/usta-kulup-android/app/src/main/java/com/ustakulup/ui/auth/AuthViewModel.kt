package com.ustakulup.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ustakulup.data.model.User
import com.ustakulup.data.repository.Result
import com.ustakulup.data.repository.UstaKulupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val user: User? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: UstaKulupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "E-posta ve şifre zorunludur")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = repository.login(email, password)) {
                is Result.Success -> _uiState.value = AuthUiState(success = true, user = result.data)
                is Result.Error   -> _uiState.value = AuthUiState(error = result.message)
                else -> {}
            }
        }
    }

    fun register(name: String, email: String, password: String, phone: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Tüm alanları doldurun")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = repository.register(name, email, password, phone)) {
                is Result.Success -> _uiState.value = AuthUiState(success = true, user = result.data)
                is Result.Error   -> _uiState.value = AuthUiState(error = result.message)
                else -> {}
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
