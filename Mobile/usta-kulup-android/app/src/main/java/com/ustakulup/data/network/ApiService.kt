package com.ustakulup.data.network

import com.ustakulup.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ─── Auth ─────────────────────────────────────────────────────────────────

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @DELETE("auth/login")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // ─── Service Requests ─────────────────────────────────────────────────────

    @POST("requests")
    suspend fun createRequest(@Body body: CreateRequestBody): Response<ApiResponse<ServiceRequest>>

    @GET("requests/user")
    suspend fun getUserRequests(): Response<ApiResponse<List<ServiceRequest>>>

    @GET("requests/{id}")
    suspend fun getRequest(@Path("id") id: String): Response<ApiResponse<ServiceRequest>>

    @PATCH("requests/{id}")
    suspend fun updateRequest(
        @Path("id") id: String,
        @Body body: UpdateRequestBody
    ): Response<ApiResponse<ServiceRequest>>

    // ─── Offers ───────────────────────────────────────────────────────────────

    @POST("offers")
    suspend fun createOffer(@Body body: CreateOfferBody): Response<ApiResponse<Offer>>

    @GET("offers/request/{id}")
    suspend fun getOffersForRequest(@Path("id") requestId: String): Response<ApiResponse<List<Offer>>>

    @POST("offers/request/{id}")
    suspend fun selectOffer(
        @Path("id") requestId: String,
        @Body body: SelectOfferBody
    ): Response<ApiResponse<Offer>>

    // ─── Rating ───────────────────────────────────────────────────────────────

    @POST("rating")
    suspend fun submitRating(@Body body: RatingBody): Response<ApiResponse<Unit>>

    // ─── Professional ─────────────────────────────────────────────────────────

    @POST("professional/apply")
    suspend fun applyProfessional(@Body body: ProfessionalApplyBody): Response<ApiResponse<Unit>>

    @GET("professional/dashboard")
    suspend fun getProfessionalDashboard(): Response<ApiResponse<ProfessionalDashboard>>

    // ─── Admin ────────────────────────────────────────────────────────────────

    @POST("admin/approve")
    suspend fun approveProfessional(@Body body: ApproveBody): Response<ApiResponse<Unit>>

    @GET("admin/set-quota")
    suspend fun getQuotas(): Response<ApiResponse<List<DistrictQuota>>>

    @POST("admin/set-quota")
    suspend fun setQuota(@Body body: SetQuotaBody): Response<ApiResponse<DistrictQuota>>

    @GET("admin/professionals")
    suspend fun getAllProfessionals(): Response<ApiResponse<List<ProfessionalProfile>>>

    @GET("admin/requests")
    suspend fun getAllRequests(): Response<ApiResponse<List<ServiceRequest>>>
}
