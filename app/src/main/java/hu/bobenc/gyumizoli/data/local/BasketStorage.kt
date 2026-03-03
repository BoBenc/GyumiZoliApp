package hu.bobenc.gyumizoli.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import hu.bobenc.gyumizoli.data.model.BasketItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BasketStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("basket_prefs", Context.MODE_PRIVATE)
    private val ITEMS_KEY = "basket_items"

    private fun itemsToJson(items: List<BasketItem>): String {
        return Gson().toJson(items)
    }

    private fun jsonToItems(json: String): List<BasketItem> {
        val type = object : TypeToken<List<BasketItem>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }

    fun addItem(item: BasketItem) {
        val items = getItems().toMutableList()
        val existingIndex = items.indexOfFirst { it.product.id == item.product.id }
        if (existingIndex >= 0) {
            items[existingIndex] = items[existingIndex].copy(quantity = items[existingIndex].quantity + item.quantity)
        } else {
            items.add(item)
        }
        prefs.edit().putString(ITEMS_KEY, itemsToJson(items)).apply()
    }

    fun getItems(): List<BasketItem> {
        val json = prefs.getString(ITEMS_KEY, null) ?: return emptyList()
        return jsonToItems(json)
    }

    fun clear() {
        prefs.edit().remove(ITEMS_KEY).apply()
    }

    fun getTotalPrice(): Double {
        return getItems().sumOf { (it.product.price * it.quantity).toDouble() }
    }
}