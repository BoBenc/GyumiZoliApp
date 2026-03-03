package hu.bobenc.gyumizoli.data.model

data class Order(
    val id: Int,
    val totalPrice: Int,
    val payment_method: String,
    val status: String,
    val customers_name: String,
    val customers_phone: String,
    val customers_email: String,
    val delivery_address: String,
    val delivery_date: String?,
    val created_at: String,
    val updated_at: String?,
    val items: String
)

data class OrderItem(
    val product: OrderProduct,
    val quantity: Int
)

data class OrderProduct(
    val id: Int,
    val name: String,
    val image_url: String?,
    val price: Int,
    val unit: String,
    val discount_price: Int? = null
)