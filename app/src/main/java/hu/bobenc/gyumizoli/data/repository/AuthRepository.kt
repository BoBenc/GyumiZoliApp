package hu.bobenc.gyumizoli.data.repository

import hu.bobenc.gyumizoli.data.local.TokenManager
import hu.bobenc.gyumizoli.data.model.User
import hu.bobenc.gyumizoli.data.remote.api.AuthApiService
import hu.bobenc.gyumizoli.util.Resource
import hu.bobenc.gyumizoli.data.remote.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.Response

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(email: String, password: String): Resource<User> = withContext(Dispatchers.IO) {
        try {
            val request = LoginRequest(email, password)
            val response: Response<ApiResponse<LoginResponse>> = authApi.login(request)

            if (response.isSuccessful) {
                response.body()?.data?.let { loginResp ->
                    tokenManager.saveToken(loginResp.token)
                    return@withContext getCurrentUser()
                } ?: return@withContext Resource.Error("Bejelentkezés sikertelen!")
            } else {
                return@withContext Resource.Error("HTTP ${response.code()}: Hibás adatok!")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Hálózati hiba!")
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String,
        birthDate: String
    ): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequest(name, email, password, phone, address, birthDate)
            val response = authApi.register(request)

            if (response.isSuccessful) {
                response.body()?.data?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Regisztráció sikertelen!")
            } else {
                Resource.Error("HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Hálózati hiba!")
        }
    }

    suspend fun logout(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.logout()
            tokenManager.clearToken()
            Resource.Success(Unit)
        } catch (e: Exception) {
            tokenManager.clearToken()
            Resource.Success(Unit)
        }
    }

    suspend fun getCurrentUser(): Resource<User> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.getUser()
            if (response.isSuccessful) {
                response.body()?.data?.let { Resource.Success(it) }
                    ?: Resource.Error("Felhasználó betöltése sikertelen!")
            } else {
                if (response.code() == 500) {
                    Resource.Error("Ez az e-mail cím vagy név már foglalt!")
                } else {
                    Resource.Error("HTTP ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Hálózati hiba!")
        }
    }
}