package hu.bobenc.gyumizoli.data.remote.dto

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val address: String,
    val birth_date: String
)