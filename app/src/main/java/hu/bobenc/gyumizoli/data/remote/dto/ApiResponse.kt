package hu.bobenc.gyumizoli.data.remote.dto

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
    val error: String? = null
)