package hu.bobenc.gyumizoli.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BarionItem(
    val Name: String,
    val Description: String,
    val Quantity: Double,
    val Unit: String,
    val UnitPrice: Double,
    val ItemTotal: Double,
    val SKU: String
)

data class BarionPaymentRequest(
    val POSKey: String,
    val PaymentType: String = "Immediate",
    @SerializedName("GuestCheckOut")
    val GuestCheckOut: Boolean = true,
    val FundingSources: List<String> = listOf("All"),
    val PaymentRequestId: String,
    val PayerHint: String,
    val PayerPhoneNumber: String? = null,
    val OrderNumber: String? = null,
    val Locale: String = "hu-HU",
    val Currency: String = "HUF",
    val RedirectUrl: String,
    val Transactions: List<BarionTransaction>
)

data class BarionTransaction(
    val POSTransactionId: String,
    val Payee: String,
    val Total: Double,
    val Comment: String? = null,
    val Items: List<BarionItem> = listOf()
)

data class BarionPaymentResponse(
    val PaymentId: String,
    val GatewayUrl: String,
    val Status: String,
    val ClientSecret: String?
)