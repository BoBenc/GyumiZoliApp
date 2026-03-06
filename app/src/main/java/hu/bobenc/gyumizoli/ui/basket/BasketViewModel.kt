package hu.bobenc.gyumizoli.ui.basket

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bobenc.gyumizoli.data.model.BasketItem
import hu.bobenc.gyumizoli.data.model.Product
import hu.bobenc.gyumizoli.data.repository.AuthRepository
import hu.bobenc.gyumizoli.data.repository.BasketRepository
import hu.bobenc.gyumizoli.data.repository.OrderRepository
import hu.bobenc.gyumizoli.data.repository.BarionRepository
import hu.bobenc.gyumizoli.data.remote.dto.OrderItemDto
import hu.bobenc.gyumizoli.data.remote.dto.OrderProductDto
import hu.bobenc.gyumizoli.data.remote.dto.OrderRequest
import hu.bobenc.gyumizoli.data.remote.dto.BarionItem
import hu.bobenc.gyumizoli.data.remote.dto.BarionPaymentRequest
import hu.bobenc.gyumizoli.data.remote.dto.BarionTransaction
import hu.bobenc.gyumizoli.util.Constants
import hu.bobenc.gyumizoli.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BasketViewModel @Inject constructor(
    private val basketRepository: BasketRepository,
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    private val barionRepository: BarionRepository
) : ViewModel() {
    private val _basketItems = MutableStateFlow<List<BasketItem>>(emptyList())
    val basketItems = _basketItems.asStateFlow()

    private val _lastOrderItems = MutableStateFlow<List<BasketItem>>(emptyList())
    val lastOrderItems = _lastOrderItems.asStateFlow()

    private val _lastOrderTotal = MutableStateFlow(0.0)
    val lastOrderTotal = _lastOrderTotal.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice = _totalPrice.asStateFlow()

    var name by mutableStateOf("")
    var phone by mutableStateOf("")
    var email by mutableStateOf("")
    var address by mutableStateOf("")
    var paymentMethod by mutableStateOf("cash")

    private val _orderStatus = MutableStateFlow<Resource<String>?>(null)
    val orderStatus = _orderStatus.asStateFlow()

    private val _barionClientSecret = MutableStateFlow<String?>(null)
    val barionClientSecret = _barionClientSecret.asStateFlow()

    private val _paymentError = MutableStateFlow<String?>(null)
    val paymentError = _paymentError.asStateFlow()

    var currentBackendOrderId by mutableStateOf<String?>(null)

    init {
        loadBasket()
        loadUserData()
    }

    fun loadUserData() {
        viewModelScope.launch {
            when (val result = authRepository.getCurrentUser()) {
                is Resource.Success -> {
                    result.data?.let { user ->
                        name = user.name
                        email = user.email
                        phone = user.phone ?: ""
                        address = user.address ?: ""
                    }
                }
                else -> {
                    Log.d("BasketViewModel", "Nincs bejelentkezett felhasználó vagy hiba történt: ${result.message}")
                }
            }
        }
    }

    fun clearShippingFields() {
        name = ""
        email = ""
        phone = ""
        address = ""
    }

    fun loadBasket() {
        viewModelScope.launch {
            val items = basketRepository.getBasketItems()
            _basketItems.value = items
            _totalPrice.value = items.sumOf { basketItem ->
                val actualPrice = (basketItem.product.discount_price ?: basketItem.product.price).toDouble()
                actualPrice * basketItem.quantity
            }
        }
    }

    fun updateQuantity(product: Product, newQuantity: Int) {
        if (newQuantity < 1) return
        viewModelScope.launch {
            val currentItems = basketRepository.getBasketItems().toMutableList()
            val index = currentItems.indexOfFirst { it.product.id == product.id }

            if (index >= 0) {
                val unitPrice = currentItems[index].product.discount_price ?: currentItems[index].product.price
                currentItems[index] = currentItems[index].copy(
                    quantity = newQuantity,
                    totalPrice = unitPrice.toDouble() * newQuantity
                )

                basketRepository.clearBasket()
                currentItems.forEach { basketRepository.addToBasket(it) }
                loadBasket()
            }
        }
    }

    fun removeItem(product: Product) {
        viewModelScope.launch {
            val currentItems = basketRepository.getBasketItems().toMutableList()
            currentItems.removeAll { it.product.id == product.id }
            basketRepository.clearBasket()
            currentItems.forEach { basketRepository.addToBasket(it) }
            loadBasket()
        }
    }

    fun resetOrderStatus() {
        _orderStatus.value = null
        _barionClientSecret.value = null
        _paymentError.value = null

    }

    fun submitOrder() {
        viewModelScope.launch {
            _orderStatus.value = Resource.Loading()
            _barionClientSecret.value = null
            _paymentError.value = null

            val user = (authRepository.getCurrentUser() as? Resource.Success)?.data
            if (user == null) {
                _orderStatus.value = Resource.Error("Kérjük, jelentkezzen be a vásárláshoz!")
                return@launch
            }

            val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3) }
            val deliveryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            val request = OrderRequest(
                user_id = user.id,
                items = _basketItems.value.map { OrderItemDto(OrderProductDto(it.product.id), it.quantity) },
                totalPrice = _totalPrice.value.toInt(),
                customers_name = name,
                customers_phone = phone,
                customers_email = email,
                delivery_address = address,
                payment_method = paymentMethod,
                delivery_date = deliveryDate
            )

            val result = orderRepository.submitOrder(request)
            _orderStatus.value = result

            if (result is Resource.Success) {
                currentBackendOrderId = result.data

                _lastOrderItems.value = _basketItems.value.toList()
                _lastOrderTotal.value = _totalPrice.value

                if (paymentMethod == "cash") {
                    basketRepository.clearBasket()
                    loadBasket()
                }
            }
        }
    }

    fun startBarionPayment() {
        viewModelScope.launch {
            _barionClientSecret.value = null
            _paymentError.value = null

            if (email.isBlank() || !email.contains("@")) {
                _paymentError.value = "Kérjük, adjon meg egy érvényes e-mail címet a szállításhoz!"
                return@launch
            }

            val barionItems = _basketItems.value.map { basketItem ->
                val originalPrice = basketItem.product.price.toDouble()
                val discountPrice = basketItem.product.discount_price?.toDouble()
                val hasDiscount = discountPrice != null && discountPrice < originalPrice

                val actualUnitPrice = if (hasDiscount) discountPrice!! else originalPrice

                val descriptionText = if (hasDiscount) {
                    "Akciós! Eredeti ár: ${originalPrice.toInt()} Ft | " + (basketItem.product.description ?: "")
                } else {
                    basketItem.product.description ?: "Friss termék"
                }

                BarionItem(
                    Name = basketItem.product.name.take(250),
                    Description = descriptionText.take(500),
                    Quantity = basketItem.quantity.toDouble(),
                    Unit = basketItem.product.unit,
                    UnitPrice = actualUnitPrice,
                    ItemTotal = actualUnitPrice * basketItem.quantity,
                    SKU = ""
                )
            }

            val calculatedTotal = barionItems.sumOf { it.ItemTotal }

            if (calculatedTotal <= 0) {
                _paymentError.value = "A kosár összege nem lehet 0 Ft!"
                return@launch
            }

            val barionRequest = BarionPaymentRequest(
                POSKey = Constants.BARION_POS_KEY,
                PaymentRequestId = "REQ-${System.currentTimeMillis()}",

                OrderNumber = currentBackendOrderId ?: "REND-${System.currentTimeMillis()}",
                PayerHint = email,
                PayerPhoneNumber = phone,

                Locale = "hu-HU",
                Currency = "HUF",
                RedirectUrl = Constants.BARION_REDIRECT_URL,
                Transactions = listOf(
                    BarionTransaction(
                        POSTransactionId = "TRANS-${System.currentTimeMillis()}",
                        Payee = Constants.BARION_PAYEE_EMAIL,
                        Total = calculatedTotal,
                        Comment = "Vásárló neve: $name | GyümiZöli Webshop Rendelés",
                        Items = barionItems
                    )
                )
            )

            val barionResult = barionRepository.startPayment(barionRequest)
            if (barionResult is Resource.Success) {
                _barionClientSecret.value = barionResult.data?.ClientSecret
            } else {
                _paymentError.value = barionResult.message ?: "Hiba a banki kapcsolódáskor!"
            }
        }
    }

    fun clearBasketAfterPayment() {
        viewModelScope.launch {
            basketRepository.clearBasket()
            loadBasket()
        }
    }
}