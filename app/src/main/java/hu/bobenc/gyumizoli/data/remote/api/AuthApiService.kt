package hu.bobenc.gyumizoli.data.remote.api

import hu.bobenc.gyumizoli.data.remote.dto.ApiResponse
import hu.bobenc.gyumizoli.data.remote.dto.LoginRequest
import hu.bobenc.gyumizoli.data.remote.dto.RegisterRequest
import hu.bobenc.gyumizoli.data.remote.dto.LoginResponse
import hu.bobenc.gyumizoli.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<String>>

    @POST("logout")
    suspend fun logout(): Response<ApiResponse<Any>>

    @GET("getuser")
    suspend fun getUser(): Response<ApiResponse<User>>
}