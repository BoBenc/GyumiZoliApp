package hu.bobenc.gyumizoli.data.remote.dto

data class LoginResponse(
    val token: String,
    val user: String
)