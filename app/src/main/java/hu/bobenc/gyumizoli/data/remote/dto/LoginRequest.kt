package hu.bobenc.gyumizoli.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String
)