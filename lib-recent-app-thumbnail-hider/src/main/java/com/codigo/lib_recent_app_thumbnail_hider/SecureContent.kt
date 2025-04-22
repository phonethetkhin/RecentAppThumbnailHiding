package com.codigo.lib_recent_app_thumbnail_hider

import android.app.Activity
import android.util.Log
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
    content: @Composable () -> Unit
) {
    var isSecure by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val rootView = remember(activity) { activity?.window?.decorView?.rootView }
    

    // Reference to whether window has focus
    DisposableEffect(activity) {
        val listener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
            if (hasFocus) enableSecureFlag(activity) else clearSecureFlag(activity)
            isSecure = !hasFocus
        }

        rootView?.viewTreeObserver?.addOnWindowFocusChangeListener(listener)

        onDispose {
            rootView?.viewTreeObserver?.removeOnWindowFocusChangeListener(listener)
        }
    }

    // Lifecycle observer to manage secure flag
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    // Log for debugging
                    Log.e("testASDF", "TESTASDF ONSTART")
                }

                Lifecycle.Event.ON_STOP -> {
                    // Log for debugging
                    Log.e("testASDF", "TESTASDF ONSTOP")
                }

                Lifecycle.Event.ON_RESUME -> {
                    // Log for debugging
                    Log.e("testASDF", "TESTASDF ONRESUME")
                    isSecure = false
                    enableSecureFlag(activity) // Re-enable secure flag when resuming
                }

                Lifecycle.Event.ON_PAUSE -> {
                    // Log for debugging
                    Log.e("testASDF", "TESTASDF ONPAUSE")
                    clearSecureFlag(activity) // Remove the secure flag when pausing
                    isSecure = true
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Log.e("testASDF", "TESTASDF isSecure $isSecure")

    Box(modifier = modifier.fillMaxSize()) {
        if (isSecure) {
            secureOverlay() // Show the secure overlay when needed
        } else {
            content() // Show the actual content otherwise
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
