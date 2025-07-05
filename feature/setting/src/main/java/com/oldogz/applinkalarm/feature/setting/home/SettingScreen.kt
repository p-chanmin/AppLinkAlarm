package com.oldogz.applinkalarm.feature.setting.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oldogz.applinkalarm.feature.setting.R
import com.oldogz.applinkalarm.feature.setting.model.SettingUiState
import com.oldogz.core.billing.BuildConfig
import com.oldogz.core.billing.LocalSubscriptionManager
import com.oldogz.core.designsystem.component.AppLinkAlarmButton
import com.oldogz.core.designsystem.component.AppLinkAlarmIconButton
import com.oldogz.core.designsystem.component.AppLinkAlarmTopAppBar
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.designsystem.theme.Paddings
import com.oldogz.core.firebase.LocalFirebaseManager
import kotlinx.coroutines.launch

@Composable
internal fun SettingScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToOpenSource: () -> Unit,
    popBackStack: () -> Unit,
    settingViewModel: SettingViewModel = hiltViewModel(),
) {
    val firebaseManager = LocalFirebaseManager.current
    val configuration = LocalConfiguration.current

    val settingUiState by settingViewModel.settingUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        firebaseManager.screenLogEvent("SettingScreen", configuration.orientation)
        settingViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    SettingContent(
        settingUiState = settingUiState,
        paddingValues = paddingValues,
        popBackStack = popBackStack,
        navigateToOpenSource = navigateToOpenSource,
        updatePermission = settingViewModel::updatePermission,
        onShowErrorSnackBar = onShowErrorSnackBar,
    )
}

@Composable
private fun SettingContent(
    settingUiState: SettingUiState,
    paddingValues: PaddingValues,
    navigateToOpenSource: () -> Unit,
    popBackStack: () -> Unit,
    updatePermission: (Boolean) -> Unit,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppLinkAlarmTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.feature_setting_top_app_bar_title),
                navigationIcon = {
                    AppLinkAlarmIconButton(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.feature_setting_icon_description_close),
                        onClick = popBackStack
                    )
                },
                actions = {}
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                PermissionSetting(
                    notificationPermission = settingUiState.notificationPermission,
                    exactAlarmPermission = settingUiState.exactAlarmPermission,
                    updatePermission = updatePermission
                )
                SubscriptionSetting(
                    onShowErrorSnackBar = onShowErrorSnackBar,
                )
                SupportSetting(
                    navigateToOpenSource = navigateToOpenSource,
                )
            }
        }
    }
}

@Composable
private fun PermissionSetting(
    notificationPermission: Boolean?,
    exactAlarmPermission: Boolean?,
    updatePermission: (Boolean) -> Unit,
) {

    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val lifecycleState by lifecycle.currentStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            val notificationState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

            updatePermission(notificationState)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Paddings.xlarge, vertical = Paddings.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.feature_setting_text_permission),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent =
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    context.startActivity(intent)
                }
                .padding(horizontal = Paddings.xlarge, vertical = Paddings.xlarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.feature_setting_text_notification_status),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                modifier = Modifier,
                text = notificationPermission?.let {
                    if (it) {
                        stringResource(R.string.feature_setting_text_granted)
                    } else {
                        stringResource(R.string.feature_setting_text_denied)
                    }
                } ?: "",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val intent = Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            "package:${context.packageName}".toUri(),
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
                }
                .padding(horizontal = Paddings.xlarge, vertical = Paddings.xlarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.feature_setting_text_alarms_reminders_status),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                modifier = Modifier,
                text = exactAlarmPermission?.let {
                    if (it) {
                        stringResource(R.string.feature_setting_text_granted)
                    } else {
                        stringResource(R.string.feature_setting_text_denied)
                    }
                } ?: "",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubscriptionSetting(
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val tooltipState = rememberTooltipState()

    val context = LocalContext.current
    val activity = context as? Activity
    val subscriptionManager = LocalSubscriptionManager.current

    val products = subscriptionManager.availableProducts.collectAsStateWithLifecycle().value
    val subscriptionState =
        subscriptionManager.subscriptionState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        subscriptionManager.queryAvailableProducts(listOf(BuildConfig.PREMIUM_MEMBERSHIP_PRODUCT_ID))
        subscriptionManager.queryPurchases(BuildConfig.PREMIUM_MEMBERSHIP_PRODUCT_ID) {}
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Paddings.xlarge, vertical = Paddings.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.feature_setting_text_subscription),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            TooltipBox(
                modifier = Modifier,
                positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                tooltip = {
                    RichTooltip(
                        title = {
                            Text(
                                text = stringResource(R.string.feature_setting_text_premium_subscription)
                            )
                        },
                        colors = TooltipDefaults.richTooltipColors().copy(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            titleContentColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.feature_setting_text_premium_subscription_tool_tip)
                        )
                    }
                },
                state = tooltipState
            ) {
                AppLinkAlarmIconButton(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.feature_setting_icon_description_subscription_info),
                    onClick = {
                        coroutineScope.launch {
                            tooltipState.show()
                        }
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Paddings.xlarge, vertical = Paddings.large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.feature_setting_text_premium_subscription),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                modifier = Modifier,
                text = subscriptionState?.let {
                    if (it) {
                        stringResource(R.string.feature_setting_text_subscribed)
                    } else {
                        stringResource(R.string.feature_setting_text_not_subscribed)
                    }
                } ?: "",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        val product = products.find { it.productId == BuildConfig.PREMIUM_MEMBERSHIP_PRODUCT_ID }
        val offer =
            product?.subscriptionOfferDetails?.find { it.offerId == BuildConfig.PREMIUM_MONTHLY_TRIAL_7D_OFFER_ID }
                ?: product?.subscriptionOfferDetails?.firstOrNull()

        subscriptionState?.let { state ->
            if (!state && product != null && offer != null) {
                AppLinkAlarmButton(
                    modifier = Modifier
                        .padding(horizontal = Paddings.xlarge)
                        .padding(top = Paddings.medium),
                    enabled = true,
                    content = if (offer.pricingPhases.size > 1) {
                        val priceText = offer.pricingPhases.last().formattedPrice
                        stringResource(R.string.feature_setting_text_7_day_free_trial, priceText)
                    } else {
                        val priceText = offer.pricingPhases.last().formattedPrice
                        stringResource(R.string.feature_setting_text_join_premium, priceText)
                    },
                    onClick = {
                        activity?.let { activity ->
                            subscriptionManager.launchPurchaseFlow(
                                activity,
                                product.productDetails,
                                offer.offerIdToken
                            )
                        }
                    }
                )
            }

            AppLinkAlarmButton(
                modifier = Modifier
                    .padding(horizontal = Paddings.xlarge)
                    .padding(bottom = Paddings.medium),
                enabled = true,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onBackground,
                content = if (state) {
                    stringResource(R.string.feature_setting_text_google_play_subscriptions_center)
                } else {
                    stringResource(R.string.feature_setting_text_restore_purchases)
                },
                onClick = {
                    if (state) {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://play.google.com/store/account/subscriptions".toUri()
                            )
                        )
                    } else {
                        subscriptionManager.queryPurchases(BuildConfig.PREMIUM_MEMBERSHIP_PRODUCT_ID) { hasPremium ->
                            if (!hasPremium) {
                                onShowErrorSnackBar(Throwable(context.getString(R.string.feature_setting_text_no_premium_subscription)))
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun SupportSetting(
    navigateToOpenSource: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Paddings.xlarge, vertical = Paddings.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.feature_setting_text_support),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { openPlayStore(context) }
                .padding(start = Paddings.xlarge)
                .padding(vertical = Paddings.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.feature_setting_text_review),
                style = MaterialTheme.typography.bodyLarge
            )

            AppLinkAlarmIconButton(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = stringResource(R.string.feature_setting_text_review),
                onClick = { openPlayStore(context) }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { openPrivacyPolicy(context) }
                .padding(start = Paddings.xlarge)
                .padding(vertical = Paddings.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.feature_setting_text_privacy_policy),
                style = MaterialTheme.typography.bodyLarge
            )

            AppLinkAlarmIconButton(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = stringResource(R.string.feature_setting_text_privacy_policy),
                onClick = { openPrivacyPolicy(context) }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navigateToOpenSource() }
                .padding(start = Paddings.xlarge)
                .padding(vertical = Paddings.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.feature_setting_text_open_source_license),
                style = MaterialTheme.typography.bodyLarge
            )

            AppLinkAlarmIconButton(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = stringResource(R.string.feature_setting_text_open_source_license),
                onClick = navigateToOpenSource
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { openContactUs(context) }
                .padding(start = Paddings.xlarge)
                .padding(vertical = Paddings.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.feature_setting_text_contact_us),
                style = MaterialTheme.typography.bodyLarge
            )

            AppLinkAlarmIconButton(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = stringResource(R.string.feature_setting_text_contact_us),
                onClick = { openContactUs(context) }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Paddings.xlarge, vertical = Paddings.xlarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val versionName =
                context.packageManager.getPackageInfo(context.packageName, 0)?.versionName
            Text(
                modifier = Modifier,
                text = stringResource(R.string.feature_setting_text_version),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                modifier = Modifier,
                text = "$versionName",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun openPrivacyPolicy(context: Context) {
    context.startActivity(
        Intent(
            Intent.ACTION_VIEW,
            "https://sites.google.com/view/applinkalarmprivacypolicy".toUri()
        )
    )
}

private fun openPlayStore(context: Context) {
    context.startActivity(
        Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=com.oldogz.applinkalarm".toUri()
        )
    )
}

private fun openContactUs(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf("oldogz7358@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "[AppLink Alarm]: Support Request")
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingContentPreview() {
    AppLinkAlarmTheme {
        SettingContent(
            settingUiState = SettingUiState(
                notificationPermission = true,
                exactAlarmPermission = false,
            ),
            paddingValues = PaddingValues(),
            navigateToOpenSource = {},
            popBackStack = {},
            updatePermission = {},
            onShowErrorSnackBar = {}
        )
    }
}