package com.oldogz.core.billing

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import com.oldogz.core.billing.model.Product
import kotlinx.coroutines.flow.StateFlow

interface SubscriptionManager {

    val availableProducts: StateFlow<List<Product>>
    val subscriptionState: StateFlow<Boolean?>

    fun initialize(onSetupFinished: () -> Unit)
    fun queryAvailableProducts(productIds: List<String>)
    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails, offerToken: String)
    fun queryPurchases(productId: String, onPurchaseResult: (Boolean) -> Unit)
}