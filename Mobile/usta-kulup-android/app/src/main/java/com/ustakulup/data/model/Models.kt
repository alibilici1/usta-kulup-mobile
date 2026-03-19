package com.ustakulup.data.model

import com.google.gson.annotations.SerializedName

// ─── Auth ───────────────────────────────────────────────────────────────────

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)

data class AuthResponse(
    val success: Boolean,
    val user: User? = null,
    val message: String? = null
)

// ─── User ────────────────────────────────────────────────────────────────────

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val role: String,           // USER | PROFESSIONAL | ADMIN
    val createdAt: String? = null
)

// ─── Service Request ─────────────────────────────────────────────────────────

data class ServiceRequest(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val district: String,
    val status: String,         // PENDING | ASSIGNED | IN_PROGRESS | COMPLETED | CANCELLED
    val userId: String,
    val user: User? = null,
    val assignments: List<RequestAssignment>? = null,
    val offers: List<Offer>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class CreateRequestBody(
    val title: String,
    val description: String,
    val category: String,
    val district: String
)

data class UpdateRequestBody(
    val status: String
)

// ─── Assignment ───────────────────────────────────────────────────────────────

data class RequestAssignment(
    val id: String,
    val requestId: String,
    val professionalId: String,
    val professional: ProfessionalProfile? = null,
    val expiresAt: String,
    val createdAt: String? = null
)

// ─── Offer ───────────────────────────────────────────────────────────────────

data class Offer(
    val id: String,
    val requestId: String,
    val professionalId: String,
    val professional: ProfessionalProfile? = null,
    val price: Double,
    val note: String? = null,
    val isSelected: Boolean = false,
    val createdAt: String? = null
)

data class CreateOfferBody(
    val price: Double,
    val note: String? = null
)

data class SelectOfferBody(
    val offerId: String
)

// ─── Professional ─────────────────────────────────────────────────────────────

data class ProfessionalProfile(
    val id: String,
    val userId: String,
    val user: User? = null,
    val category: String,
    val district: String,
    val bio: String? = null,
    val approved: Boolean,
    @SerializedName("subscriptionActive")
    val subscriptionActive: Boolean,
    val rating: Double? = null,
    val ratingCount: Int? = null,
    val lastAssignedAt: String? = null,
    val createdAt: String? = null
)

data class ProfessionalApplyBody(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val category: String,
    val district: String,
    val bio: String? = null
)

data class ProfessionalDashboard(
    val professional: ProfessionalProfile,
    val pendingAssignments: List<RequestAssignment>,
    val activeRequests: List<ServiceRequest>,
    val completedCount: Int
)

// ─── Admin ────────────────────────────────────────────────────────────────────

data class ApproveBody(
    val professionalId: String,
    val approved: Boolean,
    @SerializedName("subscriptionActive")
    val subscriptionActive: Boolean? = null
)

data class DistrictQuota(
    val id: String,
    val district: String,
    val category: String,
    val max: Int,
    val current: Int? = null
)

data class SetQuotaBody(
    val district: String,
    val category: String,
    val max: Int
)

// ─── Rating ───────────────────────────────────────────────────────────────────

data class RatingBody(
    val requestId: String,
    val professionalId: String,
    val score: Int       // 1-5
)

// ─── Generic API Response ─────────────────────────────────────────────────────

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null
)

data class ListResponse<T>(
    val success: Boolean,
    val data: List<T>? = null,
    val message: String? = null
)

// ─── Constants ────────────────────────────────────────────────────────────────

object Categories {
    val all = listOf(
        "Elektrik", "Su Tesisatı", "Doğalgaz", "Boya",
        "Tadilat", "Temizlik", "Nakliyat", "Klima",
        "Çilingir", "Mobilya", "Çatı", "Zemin"
    )
}

object Districts {
    val istanbul = listOf(
        "Kadıköy", "Beşiktaş", "Şişli", "Beyoğlu", "Fatih",
        "Üsküdar", "Maltepe", "Kartal", "Pendik", "Ümraniye",
        "Ataşehir", "Bağcılar", "Bahçelievler", "Bakırköy",
        "Başakşehir", "Eyüpsultan", "Gaziosmanpaşa", "Güngören",
        "Kağıthane", "Küçükçekmece", "Sarıyer", "Sultangazi",
        "Zeytinburnu", "Arnavutköy", "Avcılar", "Bayrampaşa",
        "Büyükçekmece", "Çatalca", "Esenler", "Esenyurt",
        "Silivri", "Sultançiftliği", "Tuzla"
    )
}

object RequestStatus {
    const val PENDING = "PENDING"
    const val ASSIGNED = "ASSIGNED"
    const val IN_PROGRESS = "IN_PROGRESS"
    const val COMPLETED = "COMPLETED"
    const val CANCELLED = "CANCELLED"

    fun displayName(status: String) = when (status) {
        PENDING -> "Beklemede"
        ASSIGNED -> "Usta Atandı"
        IN_PROGRESS -> "Devam Ediyor"
        COMPLETED -> "Tamamlandı"
        CANCELLED -> "İptal Edildi"
        else -> status
    }
}

object UserRole {
    const val USER = "USER"
    const val PROFESSIONAL = "PROFESSIONAL"
    const val ADMIN = "ADMIN"
}
