package com.ustakulup.data.network

import com.ustakulup.data.local.TokenDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenDataStore.token.firstOrNull() }
        val request = chain.request().newBuilder().apply {
            if (!token.isNullOrBlank()) {
                // Backend httpOnly cookie yerine Bearer token kullanıyoruz
                addHeader("Authorization", "Bearer $token")
                addHeader("Cookie", "token=$token")
            }
            addHeader("Content-Type", "application/json")
        }.build()
        return chain.proceed(request)
    }
}
