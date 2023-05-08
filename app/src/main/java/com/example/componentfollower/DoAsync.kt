package com.example.componentfollower

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

inline fun <T, reified R : View> R.doAsync(
    crossinline backgroundTask: suspend () -> T?,
    crossinline onResult: R.(T?) -> Unit
) {
    val job = CoroutineScope(Dispatchers.Default)
    val attachListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(p0: View) {}
        override fun onViewDetachedFromWindow(p0: View) {
            job.cancel()
        }
    }
    addOnAttachStateChangeListener(attachListener)
    job.launch {
        val data = try {
            backgroundTask()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        if (isActive) {
            try {
                withContext(Dispatchers.Main) { onResult(data) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        removeOnAttachStateChangeListener(attachListener)
    }
}