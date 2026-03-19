package com.ustakulup.ui.professional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ustakulup.data.model.*
import com.ustakulup.data.repository.Result
import com.ustakulup.data.repository.UstaKulupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfessionalViewModel @Inject constructor(
    private val repository: UstaKulupRepository
) : ViewModel() {

    private val _dashboard = MutableStateFlow<ProfessionalDashboard?>(null)
    val dashboard = _dashboard.asStateFlow()

    private val _selectedRequest = MutableStateFlow<ServiceRequest?>(null)
    val selectedRequest = _selectedRequest.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage = _successMessage.asStateFlow()

    // Apply form state
    private val _applySuccess = MutableStateFlow(false)
    val applySuccess = _applySuccess.asStateFlow()

    init { loadDashboard() }

    fun loadDashboard() = viewModelScope.launch {
        _isLoading.value = true
        when (val r = repository.getProfessionalDashboard()) {
            is Result.Success -> _dashboard.value = r.data
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
        _isLoading.value = false
    }

    fun loadRequest(id: String) = viewModelScope.launch {
        _isLoading.value = true
        when (val r = repository.getRequest(id)) {
            is Result.Success -> _selectedRequest.value = r.data
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
        _isLoading.value = false
    }

    fun submitOffer(price: Double, note: String?, onSuccess: () -> Unit) = viewModelScope.launch {
        _isLoading.value = true
        when (val r = repository.createOffer(price, note)) {
            is Result.Success -> { _successMessage.value = "Teklifiniz gönderildi!"; onSuccess() }
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
        _isLoading.value = false
    }

    fun applyProfessional(
        name: String, email: String, password: String, phone: String,
        category: String, district: String, bio: String
    ) = viewModelScope.launch {
        if (name.isBlank() || email.isBlank() || password.isBlank() ||
            phone.isBlank() || category.isBlank() || district.isBlank()) {
            _error.value = "Zorunlu alanları doldurun"
            return@launch
        }
        _isLoading.value = true
        val body = ProfessionalApplyBody(name, email, password, phone, category, district,
            bio.ifBlank { null })
        when (val r = repository.applyProfessional(body)) {
            is Result.Success -> _applySuccess.value = true
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
        _isLoading.value = false
    }

    fun logout() = viewModelScope.launch { repository.logout() }
    fun clearError() { _error.value = null }
    fun clearSuccess() { _successMessage.value = null }
}
