package com.oldogz.core.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.oldogz.core.billing.model.PricingPhase
import com.oldogz.core.billing.model.Product
import com.oldogz.core.billing.model.SubscriptionOffer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class SubscriptionManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SubscriptionManager {

    private val _availableProducts = MutableStateFlow<List<Product>>(emptyList())
    override val availableProducts = _availableProducts.asStateFlow()

    private val _subscriptionState = MutableStateFlow<Boolean?>(null)
    override val subscriptionState = _subscriptionState.asStateFlow()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    acknowledgePurchase(purchase)
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.core_billing_text_payment_canceled),
                    Toast.LENGTH_SHORT
                ).show()
            }

            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {}

            else -> {
                Toast.makeText(
                    context,
                    context.getString(
                        R.string.core_billing_text_payment_error,
                        billingResult.responseCode
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
        .enableOneTimeProducts()
        .build()

    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(pendingPurchasesParams)
        .enableAutoServiceReconnection()
        .build()

    override fun initialize(onSetupFinished: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing client setup finished successfully")
                } else {
                    Log.e(TAG, "Billing client setup failed : ${billingResult.responseCode}")
                }
                onSetupFinished()
            }

            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "Billing client disconnected")
            }
        })
    }

    override fun queryAvailableProducts(
        productIds: List<String>,
    ) {
        val productList = productIds.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                val products = productDetailsList.productDetailsList.map { productDetails ->
                    val subscriptionOfferDetails =
                        productDetails.subscriptionOfferDetails?.map { offerDetails ->
                            val pricingPhases =
                                offerDetails.pricingPhases.pricingPhaseList.map { pricingPhase ->
                                    PricingPhase(
                                        priceAmountMicros = pricingPhase.priceAmountMicros,
                                        priceCurrencyCode = pricingPhase.priceCurrencyCode,
                                        formattedPrice = pricingPhase.formattedPrice,
                                        billingPeriod = pricingPhase.billingPeriod,
                                        recurrenceMode = pricingPhase.recurrenceMode,
                                    )
                                }.sortedBy { it.priceAmountMicros }

                            SubscriptionOffer(
                                basePlanId = offerDetails.basePlanId,
                                offerId = offerDetails.offerId,
                                offerIdToken = offerDetails.offerToken,
                                pricingPhases = pricingPhases,
                            )
                        } ?: emptyList()
                    Product(
                        productId = productDetails.productId,
                        title = productDetails.title,
                        name = productDetails.name,
                        subscriptionOfferDetails = subscriptionOfferDetails,
                        productDetails = productDetails,
                    )
                }
                _availableProducts.update { products }
            } else {
                _availableProducts.update { emptyList() }
            }
        }
    }

    override fun launchPurchaseFlow(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String
    ) {
        println("billingClient.isReady: ${billingClient.isReady}")
        if (billingClient.isReady) {
            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParams))
                .build()

            billingClient.launchBillingFlow(activity, billingFlowParams)
        } else {
            Log.e(TAG, "Billing client is not ready")
            Toast.makeText(
                context,
                context.getString(R.string.core_billing_text_connection_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun queryPurchases(
        productId: String,
        onPurchaseResult: (Boolean) -> Unit,
    ) {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val activePurchase = purchases.firstOrNull {
                    it.isAcknowledged && it.purchaseState == Purchase.PurchaseState.PURCHASED
                            && it.products.contains(productId)
                }
                if (activePurchase != null) {
                    _subscriptionState.update { true }
                    onPurchaseResult(true)
                } else {
                    _subscriptionState.update { false }
                    onPurchaseResult(false)
                }
            } else {
                _subscriptionState.update { false }
                onPurchaseResult(false)
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        _subscriptionState.update { true }
                    } else {
                        _subscriptionState.update { false }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "SubscriptionManager"
    }
}