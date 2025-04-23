package com.codigo.lib_recent_app_thumbnail_hider

import android.app.Activity
import android.util.Log
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun SecureContent(
    modifier: Modifier = Modifier,
    secureOverlay: @Composable () -> Unit = { DefaultSecureOverlay() },
    content: @Composable (onCloseClicked: () -> Unit) -> Unit
) {
    var isSecure by remember { mutableStateOf(false) }
    var isClickBack by remember { mutableStateOf(false) }
    var isClickClose by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val rootView = remember(activity) { activity?.window?.decorView?.rootView }

    // Callback to trigger from close button
    val onCloseClicked = {
        isClickClose = true
    }

    CustomBackHandler {
        isClickBack = true
    }

    DisposableEffect(activity) {
        val listener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
            if (hasFocus) {
                enableSecureFlag(activity)
            } else {
                clearSecureFlag(activity)
            }

            isSecure = if (isClickBack || isClickClose) false else !hasFocus
        }

        rootView?.viewTreeObserver?.addOnWindowFocusChangeListener(listener)

        onDispose {
            rootView?.viewTreeObserver?.removeOnWindowFocusChangeListener(listener)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    enableSecureFlag(activity)
                    isSecure = false
                    isClickBack = false
                    isClickClose = false
                }

                Lifecycle.Event.ON_PAUSE -> {
                    clearSecureFlag(activity)
                    isSecure = !(isClickBack || isClickClose)
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isSecure) {
            secureOverlay()
        } else {
            content(onCloseClicked)
        }
    }
}


// Helper functions to manage FLAG_SECURE
fun enableSecureFlag(activity: Activity?) {
    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
}

fun clearSecureFlag(activity: Activity?) {
    Log.e("testASDF", "FLAG CLEARED")
    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
}
