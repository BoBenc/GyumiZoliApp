package hu.bobenc.gyumizoli.data.model

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Int,
    val promotion: Int?,
    val discount_price: Int?,
    val category: String,
    val unit: String,
    val stock: Int,
    val image_url: String?
)