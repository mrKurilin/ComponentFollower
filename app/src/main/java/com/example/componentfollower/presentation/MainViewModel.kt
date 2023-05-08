package com.example.componentfollower.presentation

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.componentfollower.PermissionsChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class MainViewModel {

    private val rootDirectory = Environment.getExternalStorageDirectory()

    private val _uiStateFlow = MutableStateFlow<UIStates>(UIStates.Loading)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _currentDirectoryFlow = MutableStateFlow<File>(rootDirectory)

    private val _filesFlow = MutableStateFlow<List<File>>(
        rootDirectory.listFiles()?.toList() ?: listOf()
    )
    val filesFlow = _filesFlow.asStateFlow()

    val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    private val permissionsChecker = PermissionsChecker()

    fun onBackPressed() {
        if (_currentDirectoryFlow.value == rootDirectory) {
            _uiStateFlow.value = UIStates.Finish
        } else {
            val files = _currentDirectoryFlow.value.parentFile?.listFiles()?.toList() ?: listOf()
            _currentDirectoryFlow.value = _currentDirectoryFlow.value.parentFile as File
            _filesFlow.value = files
        }
    }

    fun checkPermissions(activity: Activity) {
        _uiStateFlow.value = UIStates.Loading
        val deniedPermissions = permissionsChecker.getDeniedPermissions(activity)
        if (deniedPermissions.isEmpty()) {
            _uiStateFlow.value = UIStates.PermissionsGranted
        } else {
            _uiStateFlow.value = UIStates.PermissionsDenied(deniedPermissions)
        }
    }

    fun openFile(file: File, activity: Activity) {
        if (file.isDirectory) {
            _currentDirectoryFlow.value = file
            _filesFlow.value = file.listFiles()?.toList() ?: listOf()
            return
        }

        val mimeType: String = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            file.extension.lowercase()
        ) ?: "application/octet-stream"

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            FileProvider.getUriForFile(
                activity,
                activity.applicationContext.packageName + ".provider",
                file
            ),
            mimeType
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION

        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "Невозможно открыть файл", Toast.LENGTH_LONG).show()
        }
    }

    fun deniedPermissions(context: Context): Array<String> {
        return permissionsChecker.getDeniedPermissions(context)
    }
}