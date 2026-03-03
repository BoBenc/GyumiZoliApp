package hu.bobenc.gyumizoli.data.model

data class BasketItem(
    val product: Product,
    var quantity: Int = 1,
    val totalPrice: Double
)