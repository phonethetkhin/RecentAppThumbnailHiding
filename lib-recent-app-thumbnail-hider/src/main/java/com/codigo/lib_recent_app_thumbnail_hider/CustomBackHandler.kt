package com.codigo.lib_recent_app_thumbnail_hider

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.LocalLifecycleOwner

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CustomBackHandler(
    enabled: Boolean = true,
    onBackBeforeDefault: () -> Unit
) {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val currentCallback by rememberUpdatedState(newValue = onBackBeforeDefault)

    val callback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                // Run custom logic

                // Delay briefly to let Compose update before triggering system back
                scope.launch {
                    currentCallback()
                    isEnabled = false
                    dispatcher?.onBackPressed()
                    isEnabled = true
                }
            }
        }
    }

    SideEffect {
        callback.isEnabled = enabled
    }

    DisposableEffect(lifecycleOwner, dispatcher) {
        dispatcher?.addCallback(lifecycleOwner, callback)
        onDispose {
            callback.remove()
        }
    }
}

