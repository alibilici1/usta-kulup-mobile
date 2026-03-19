package com.ustakulup.ui.user

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

// ─── ViewModel ────────────────────────────────────────────────────────────────

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UstaKulupRepository
) : ViewModel() {

    private val _requests = MutableStateFlow<List<ServiceRequest>>(emptyList())
    val requests = _requests.asStateFlow()

    private val _selectedRequest = MutableStateFlow<ServiceRequest?>(null)
    val selectedRequest = _selectedRequest.asStateFlow()

    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers = _offers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage = _successMessage.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    init {
        loadUser()
        loadRequests()
    }

    fun loadUser() = viewModelScope.launch {
        _currentUser.value = repository.getCurrentUser()
    }

    fun loadRequests() = viewModelScope.launch {
        _isLoading.value = true
        when (val r = repository.getUserRequests()) {
            is Result.Success -> _requests.value = r.data
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
        _isLoading.value = false
    }

    fun loadRequest(id: String) = viewModelScope.launch {
        _isLoading.value = true
        when (val r = repository.getRequest(id)) {
            is Result.Success -> {
                _selectedRequest.value = r.data
                loadOffers(id)
            }
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
        _isLoading.value = false
    }

    fun loadOffers(requestId: String) = viewModelScope.launch {
        when (val r = repository.getOffersForRequest(requestId)) {
            is Result.Success -> _offers.value = r.data
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
    }

    fun createRequest(title: String, description: String, category: String, district: String,
                      onSuccess: () -> Unit) = viewModelScope.launch {
        _isLoading.value = true
        when (val r = repository.createRequest(title, description, category, district)) {
            is Result.Success -> { loadRequests(); onSuccess() }
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
        _isLoading.value = false
    }

    fun selectOffer(requestId: String, offerId: String) = viewModelScope.launch {
        when (val r = repository.selectOffer(requestId, offerId)) {
            is Result.Success -> {
                _successMessage.value = "Teklif seçildi!"
                loadRequest(requestId)
            }
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
    }

    fun cancelRequest(requestId: String) = viewModelScope.launch {
        when (val r = repository.updateRequest(requestId, RequestStatus.CANCELLED)) {
            is Result.Success -> { _successMessage.value = "Talep iptal edildi"; loadRequests() }
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
    }

    fun submitRating(requestId: String, professionalId: String, score: Int) = viewModelScope.launch {
        when (val r = repository.submitRating(requestId, professionalId, score)) {
            is Result.Success -> _successMessage.value = "Değerlendirmeniz kaydedildi!"
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
    }

    fun logout() = viewModelScope.launch { repository.logout() }
    fun clearError() { _error.value = null }
    fun clearSuccess() { _successMessage.value = null }
}
