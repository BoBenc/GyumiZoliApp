package hu.bobenc.gyumizoli.data.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val email_verified_at: String?,
    val phone: String?,
    val address: String?,
    val birth_date: String?,
    val admin: Int,
    val login_counter: Int,
    val banning_time: String?
)