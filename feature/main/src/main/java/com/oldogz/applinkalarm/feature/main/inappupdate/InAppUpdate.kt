package com.oldogz.applinkalarm.feature.main.inappupdate

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.oldogz.applinkalarm.feature.main.R
import com.oldogz.core.firebase.LocalFirebaseManager
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Composable
internal fun InAppUpdate(
    snackBarHostState: SnackbarHostState,
    rejectFlexibleUpdateDate: ZonedDateTime?,
    setRejectFlexibleUpdateDate: () -> Unit,
) {
    val context = LocalContext.current
    val firebaseManager = LocalFirebaseManager.current
    val scope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val lifecycleState by lifecycle.currentStateFlow.collectAsStateWithLifecycle()

    val appUpdateManager: AppUpdateManager = remember { AppUpdateManagerFactory.create(context) }

    val appUpdateFlexibleResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            setRejectFlexibleUpdateDate()
        }
    }

    val installStateUpdatedListener = remember {
        InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                scope.launch {
                    showFlexibleUpdateSnackBar(
                        snackBarHostState,
                        appUpdateManager,
                        context.getString(R.string.feature_main_text_update_download_complete),
                        context.getString(R.string.feature_main_text_install)
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        appUpdateManager.registerListener(installStateUpdatedListener)
        onDispose {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }

    LaunchedEffect(rejectFlexibleUpdateDate) {
        try {
            val now = ZonedDateTime.now(ZoneOffset.UTC)

            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                val availableFlexible =
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                            && rejectFlexibleUpdateDate?.plusDays(1)?.isBefore(now) ?: true

                if (availableFlexible) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        appUpdateFlexibleResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                    )
                }
            }
        } catch (e: Exception) {
            firebaseManager.reportNonFatalError(e)
        }
    }

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            try {
                val appUpdateInfoTask = appUpdateManager.appUpdateInfo

                appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        scope.launch {
                            showFlexibleUpdateSnackBar(
                                snackBarHostState,
                                appUpdateManager,
                                context.getString(R.string.feature_main_text_update_download_complete),
                                context.getString(R.string.feature_main_text_install)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                firebaseManager.reportNonFatalError(e)
            }
        }
    }
}

private suspend fun showFlexibleUpdateSnackBar(
    snackBarHostState: SnackbarHostState,
    appUpdateManager: AppUpdateManager,
    message: String,
    actionLabel: String,
) {
    val snackBarResult = snackBarHostState.showSnackbar(
        message = message,
        actionLabel = actionLabel,
        duration = SnackbarDuration.Indefinite,
    )

    if (snackBarResult == SnackbarResult.ActionPerformed) {
        appUpdateManager.completeUpdate()
    }
}