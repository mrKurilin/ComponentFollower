package com.example.componentfollower.presentation

import android.app.Activity
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.componentfollower.ComponentFollowerApp
import com.example.componentfollower.PermissionsChecker
import com.example.componentfollower.domain.model.FileToShow
import com.example.componentfollower.util.comparators.Comparation
import com.example.componentfollower.util.comparators.FilesByDateComparator
import com.example.componentfollower.util.comparators.FilesByDirectoryComparator
import com.example.componentfollower.util.comparators.FilesByExtensionComparator
import com.example.componentfollower.util.comparators.FilesByNameComparator
import com.example.componentfollower.util.comparators.FilesBySizeComparator
import com.example.componentfollower.util.comparators.SORTING_KEY
import com.example.componentfollower.util.comparators.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val componentFollowerApp = app as ComponentFollowerApp
    private val fileConverter = componentFollowerApp.fileConverter
    val fileIconResourceProvider = componentFollowerApp.fileIconResourceProvider

    private val rootDirectory = Environment.getExternalStorageDirectory()

    private val _uiStateFlow = MutableStateFlow<UIStates>(UIStates.Loading)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private var currentDirectory = rootDirectory

    private val _filesFlow = MutableStateFlow(
        rootDirectory.listFiles()?.map {
            fileConverter.convertToFileToShow(it)
        } ?: listOf()
    )
    val filesFlow = _filesFlow.asStateFlow()

    private val permissionsChecker = PermissionsChecker()

    var sortingBy: Int = 0

    init {
        viewModelScope.launch {
            app.dataStore.data.collect { preferences ->
                sortingBy = preferences[intPreferencesKey(SORTING_KEY)] ?: 0
                updateShownFiles(currentDirectory)
            }
        }
    }

    fun onBackPressed() {
        _uiStateFlow.value = UIStates.Loading

        if (currentDirectory == rootDirectory) {
            _uiStateFlow.value = UIStates.Finish
        } else if (currentDirectory.parentFile != null) {
            val parentFile = currentDirectory.parentFile!!
            currentDirectory = parentFile
            updateShownFiles(parentFile)
        }
    }

    fun checkPermissions(activity: Activity) {
        _uiStateFlow.value = UIStates.Loading

        val deniedPermissions = permissionsChecker.getDeniedPermissions(activity)

        if (deniedPermissions.isEmpty()) {
            updateShownFiles(currentDirectory)
        } else if (
            permissionsChecker.isSomePermissionSetToNeverAskAgain(activity, deniedPermissions)
        ) {
            _uiStateFlow.value = UIStates.SomePermissionSetToNeverAskAgain
        } else {
            _uiStateFlow.value = UIStates.PermissionsDenied
        }
    }

    fun openFile(fileToShow: FileToShow, activity: Activity) {
        _uiStateFlow.value = UIStates.Loading

        val file = File(fileToShow.path)

        if (file.isDirectory) {
            currentDirectory = file
            updateShownFiles(file)
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
                File(file.path)
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

    private fun updateShownFiles(file: File) {
        _uiStateFlow.value = UIStates.Loading
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
            &&
            (file.path == rootDirectory.path + "/Android/data"
                    ||
                    file.path == rootDirectory.path + "/Android/obb")
        ) {
            _uiStateFlow.value = UIStates.SystemFolder
            return
        }

        val filesList = file.listFiles()?.sortedWith(
            when (sortingBy) {
                Comparation.BY_DATE -> {
                    FilesByDateComparator()
                }

                Comparation.BY_EXTENSION -> {
                    FilesByExtensionComparator()
                }

                Comparation.BY_SIZE -> {
                    FilesBySizeComparator()
                }

                else -> {
                    FilesByNameComparator()
                }
            }
        )?.sortedWith(FilesByDirectoryComparator())?.map {
            fileConverter.convertToFileToShow(it)
        } ?: listOf()

        if (filesList.isEmpty()) {
            _uiStateFlow.value = UIStates.EmptyFolder
            return
        }

        _filesFlow.value = filesList
        _uiStateFlow.value = UIStates.FilesLoaded
    }

    fun deniedPermissions(context: Context): Array<String> {
        return permissionsChecker.getDeniedPermissions(context)
    }
}