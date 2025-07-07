package com.oldogz.core.billing.model

import com.android.billingclient.api.ProductDetails

data class Product(
    val productId: String,
    val title: String,
    val name: String,
    val subscriptionOfferDetails: List<SubscriptionOffer>,
    val productDetails: ProductDetails
)

data class SubscriptionOffer(
    val basePlanId: String,
    val offerId: String?,
    val offerIdToken: String,
    val pricingPhases: List<PricingPhase>,
)

data class PricingPhase(
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val formattedPrice: String,
    val billingPeriod: String,
    val recurrenceMode: Int,
)