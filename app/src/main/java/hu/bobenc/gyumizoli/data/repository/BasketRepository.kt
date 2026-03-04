package hu.bobenc.gyumizoli.data.repository

import hu.bobenc.gyumizoli.data.local.BasketStorage
import hu.bobenc.gyumizoli.data.model.BasketItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BasketRepository @Inject constructor(
    private val basketStorage: BasketStorage
) {
    fun addToBasket(item: BasketItem) = basketStorage.addItem(item)

    fun getBasketItems(): List<BasketItem> = basketStorage.getItems()

    fun clearBasket() = basketStorage.clear()

    fun getTotalPrice(): Double = basketStorage.getTotalPrice()
}