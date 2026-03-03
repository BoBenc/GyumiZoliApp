package hu.bobenc.gyumizoli.data.remote.dto

data class OrderRequest(
    val user_id: Int,
    val items: List<OrderItemDto>,
    val totalPrice: Int,
    val customers_name: String,
    val customers_phone: String,
    val customers_email: String,
    val delivery_address: String,
    val payment_method: String,
    val status: String = "pending",
    val delivery_date: String
)

data class OrderItemDto(
    val product: OrderProductDto,
    val quantity: Int
)

data class OrderProductDto(
    val id: Int
)