package com.codigo.lib_recent_app_thumbnail_hider

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

