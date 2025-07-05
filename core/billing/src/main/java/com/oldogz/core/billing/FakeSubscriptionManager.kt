package com.oldogz.core.billing

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import com.oldogz.core.billing.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeSubscriptionManager : SubscriptionManager {
    override val availableProducts: StateFlow<List<Product>> = MutableStateFlow(emptyList())
    override val subscriptionState: StateFlow<Boolean?> = MutableStateFlow(false)

    override fun initialize() {}

    override fun queryAvailableProducts(productIds: List<String>) {}

    override fun launchPurchaseFlow(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String
    ) {
    }

    override fun queryPurchases(productId: String, onPurchaseResult: (Boolean) -> Unit) {}

    override fun endConnection() {}
}