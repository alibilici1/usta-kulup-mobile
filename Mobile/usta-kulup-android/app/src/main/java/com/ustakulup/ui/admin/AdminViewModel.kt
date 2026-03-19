package com.ustakulup.ui.admin

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
class AdminViewModel @Inject constructor(
    private val repository: UstaKulupRepository
) : ViewModel() {

    private val _professionals = MutableStateFlow<List<ProfessionalProfile>>(emptyList())
    val professionals = _professionals.asStateFlow()

    private val _quotas = MutableStateFlow<List<DistrictQuota>>(emptyList())
    val quotas = _quotas.asStateFlow()

    private val _requests = MutableStateFlow<List<ServiceRequest>>(emptyList())
    val requests = _requests.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage = _successMessage.asStateFlow()

    init { loadProfessionals() }

    fun loadProfessionals() = viewModelScope.launch {
        _isLoading.value = true
        when (val r = repository.getAllProfessionals()) {
            is Result.Success -> _professionals.value = r.data
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
        _isLoading.value = false
    }

    fun loadQuotas() = viewModelScope.launch {
        _isLoading.value = true
        when (val r = repository.getQuotas()) {
            is Result.Success -> _quotas.value = r.data
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
        _isLoading.value = false
    }

    fun loadRequests() = viewModelScope.launch {
        _isLoading.value = true
        when (val r = repository.getAllAdminRequests()) {
            is Result.Success -> _requests.value = r.data
            is Result.Error   -> _error.value = r.message
            else -> {}
        }
        _isLoading.value = false
    }

    fun approveProfessional(id: String, approved: Boolean) = viewModelScope.launch {
        when (val r = repository.approveProfessional(id, approved)) {
            is Result.Success -> {
                _successMessage.value = if (approved) "Usta onaylandı ✓" else "Usta reddedildi"
                loadProfessionals()
            }
            is Result.Error -> _error.value = r.message
            else -> {}
        }
    }

    fun setQuota(district: String, category: String, max: Int) = viewModelScope.launch {
        if (district.isBlank() || category.isBlank() || max <= 0) {
            _error.value = "Geçerli değerler girin"
            return@launch
        }
        when (val r = repository.setQuota(district, category, max)) {
            is Result.Success -> {
                _successMessage.value = "Kota güncellendi ✓"
                loadQuotas()
            }
            is Result.Error -> _error.value = r.message
            else -> {}
        }
    }

    fun logout() = viewModelScope.launch { repository.logout() }
    fun clearError() { _error.value = null }
    fun clearSuccess() { _successMessage.value = null }
}
