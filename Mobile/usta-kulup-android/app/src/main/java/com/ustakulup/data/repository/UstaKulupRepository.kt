package com.ustakulup.data.repository

import com.ustakulup.data.local.TokenDataStore
import com.ustakulup.data.model.*
import com.ustakulup.data.network.ApiService
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

@Singleton
class UstaKulupRepository @Inject constructor(
    private val api: ApiService,
    private val tokenDataStore: TokenDataStore
) {
    // ─── Auth ─────────────────────────────────────────────────────────────────

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.success == true) {
                val user = response.body()!!.user!!
                // Cookie'yi header'dan al ve sakla
                val cookie = response.headers()["Set-Cookie"]
                val token = cookie?.substringAfter("token=")?.substringBefore(";") ?: ""
                if (token.isNotBlank()) tokenDataStore.saveToken(token)
                tokenDataStore.saveUser(user)
                Result.Success(user)
            } else {
                Result.Error(response.body()?.message ?: "Giriş başarısız")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    suspend fun register(name: String, email: String, password: String, phone: String): Result<User> {
        return try {
            val response = api.register(RegisterRequest(name, email, password, phone))
            if (response.isSuccessful && response.body()?.success == true) {
                val user = response.body()!!.user!!
                tokenDataStore.saveUser(user)
                Result.Success(user)
            } else {
                Result.Error(response.body()?.message ?: "Kayıt başarısız")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    suspend fun logout() {
        try { api.logout() } catch (_: Exception) {}
        tokenDataStore.clear()
    }

    suspend fun getCurrentUser(): User? = tokenDataStore.currentUser.firstOrNull()

    // ─── Service Requests ─────────────────────────────────────────────────────

    suspend fun createRequest(
        title: String, description: String, category: String, district: String
    ): Result<ServiceRequest> {
        return try {
            val response = api.createRequest(CreateRequestBody(title, description, category, district))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(response.body()!!.data!!)
            } else {
                Result.Error(response.body()?.message ?: "Talep oluşturulamadı")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    suspend fun getUserRequests(): Result<List<ServiceRequest>> {
        return try {
            val response = api.getUserRequests()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(response.body()!!.data ?: emptyList())
            } else {
                Result.Error(response.body()?.message ?: "Talepler alınamadı")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    suspend fun getRequest(id: String): Result<ServiceRequest> {
        return try {
            val response = api.getRequest(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(response.body()!!.data!!)
            } else {
                Result.Error(response.body()?.message ?: "Talep bulunamadı")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    suspend fun updateRequest(id: String, status: String): Result<ServiceRequest> {
        return try {
            val response = api.updateRequest(id, UpdateRequestBody(status))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(response.body()!!.data!!)
            } else {
                Result.Error(response.body()?.message ?: "Güncellenemedi")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    // ─── Offers ───────────────────────────────────────────────────────────────

    suspend fun createOffer(price: Double, note: String?): Result<Offer> {
        return try {
            val response = api.createOffer(CreateOfferBody(price, note))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(response.body()!!.data!!)
            } else {
                Result.Error(response.body()?.message ?: "Teklif gönderilemedi")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    suspend fun getOffersForRequest(requestId: String): Result<List<Offer>> {
        return try {
            val response = api.getOffersForRequest(requestId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(response.body()!!.data ?: emptyList())
            } else {
                Result.Error(response.body()?.message ?: "Teklifler alınamadı")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    suspend fun selectOffer(requestId: String, offerId: String): Result<Offer> {
        return try {
            val response = api.selectOffer(requestId, SelectOfferBody(offerId))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(response.body()!!.data!!)
            } else {
                Result.Error(response.body()?.message ?: "Teklif seçilemedi")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    // ─── Rating ───────────────────────────────────────────────────────────────

    suspend fun submitRating(requestId: String, professionalId: String, score: Int): Result<Unit> {
        return try {
            val response = api.submitRating(RatingBody(requestId, professionalId, score))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(Unit)
            } else {
                Result.Error(response.body()?.message ?: "Değerlendirme gönderilemedi")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    // ─── Professional ─────────────────────────────────────────────────────────

    suspend fun applyProfessional(body: ProfessionalApplyBody): Result<Unit> {
        return try {
            val response = api.applyProfessional(body)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(Unit)
            } else {
                Result.Error(response.body()?.message ?: "Başvuru gönderilemedi")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    suspend fun getProfessionalDashboard(): Result<ProfessionalDashboard> {
        return try {
            val response = api.getProfessionalDashboard()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(response.body()!!.data!!)
            } else {
                Result.Error(response.body()?.message ?: "Dashboard alınamadı")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    // ─── Admin ────────────────────────────────────────────────────────────────

    suspend fun approveProfessional(professionalId: String, approved: Boolean): Result<Unit> {
        return try {
            val response = api.approveProfessional(ApproveBody(professionalId, approved))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(Unit)
            } else {
                Result.Error(response.body()?.message ?: "İşlem başarısız")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    suspend fun getQuotas(): Result<List<DistrictQuota>> {
        return try {
            val response = api.getQuotas()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(response.body()!!.data ?: emptyList())
            } else {
                Result.Error(response.body()?.message ?: "Kotalar alınamadı")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    suspend fun setQuota(district: String, category: String, max: Int): Result<DistrictQuota> {
        return try {
            val response = api.setQuota(SetQuotaBody(district, category, max))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(response.body()!!.data!!)
            } else {
                Result.Error(response.body()?.message ?: "Kota ayarlanamadı")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    suspend fun getAllProfessionals(): Result<List<ProfessionalProfile>> {
        return try {
            val response = api.getAllProfessionals()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(response.body()!!.data ?: emptyList())
            } else {
                Result.Error(response.body()?.message ?: "Ustalar alınamadı")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }

    suspend fun getAllAdminRequests(): Result<List<ServiceRequest>> {
        return try {
            val response = api.getAllRequests()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(response.body()!!.data ?: emptyList())
            } else {
                Result.Error(response.body()?.message ?: "Talepler alınamadı")
            }
        } catch (e: Exception) {
            Result.Error("Bağlantı hatası: ${e.message}")
        }
    }
}
