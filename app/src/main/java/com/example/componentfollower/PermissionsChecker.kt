package com.example.componentfollower

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsChecker {

    fun getDeniedPermissions(context: Context): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions = listOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
            )

            val deniedPermissions = mutableListOf<String>()

            permissions.forEach { permission ->
                if (ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    deniedPermissions.add(permission)
                }
            }
            deniedPermissions.toTypedArray()
        } else {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                arrayOf()
            }
        }
    }

    fun isSomePermissionSetToNeverAskAgain(
        activity: Activity,
        permissions: Array<out String>,
    ): Boolean {
        for (permission in permissions) {
            val shouldNotShowRequestPermissionRationale =
                !ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    permission
                )

            val permissionDenied = ActivityCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_DENIED

            val permissionSetToNeverAskAgain = shouldNotShowRequestPermissionRationale
                    && permissionDenied

            if (permissionSetToNeverAskAgain) {
                return true
            }
        }
        return false
    }
}