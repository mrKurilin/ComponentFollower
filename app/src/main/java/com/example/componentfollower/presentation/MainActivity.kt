package com.example.componentfollower.presentation

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.componentfollower.R
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var mainViewModel = MainViewModel()

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var permissionsRationaleGroup: Group
    private lateinit var exitButton: Button
    private lateinit var requestPermissionsButton: Button
    private lateinit var deniedPermissionsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        permissionsRationaleGroup = findViewById(R.id.permissions_rationale_group)
        exitButton = findViewById(R.id.exit_button)
        requestPermissionsButton = findViewById(R.id.request_permissions_button)
        deniedPermissionsTextView = findViewById(R.id.permissions_rationale_text)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mainViewModel.onBackPressed()
                }
            }
        )

        val adapter = FilesRecyclerViewAdapter(
            simpleDateFormat = mainViewModel.simpleDateFormat,
            openFile = { file ->
                mainViewModel.openFile(file, this)
            }
        )

        recyclerView.adapter = adapter

        lifecycleScope.launch {
            launch {
                mainViewModel.uiStateFlow.collect { uiState ->
                    updateUI(uiState)
                }
            }

            launch {
                mainViewModel.filesFlow.collect { files ->
                    adapter.setItems(files)
                }
            }
        }

        exitButton.setOnClickListener {
            finish()
        }

        requestPermissionsButton.setOnClickListener {
            requestPermissions(mainViewModel.deniedPermissions(this), 1)
        }

        if (mainViewModel.deniedPermissions(this).isNotEmpty()) {
            requestPermissions(mainViewModel.deniedPermissions(this), 1)
        }
    }

    private fun updateUI(uiState: UIStates) {
        when (uiState) {
            is UIStates.PermissionsDenied -> {
                deniedPermissionsTextView.text = getString(
                    R.string.permissions_rationale,
                    uiState.deniedPermissions.joinToString("/n")
                )

                progressBar.visibility = View.INVISIBLE
                recyclerView.visibility = View.INVISIBLE
                permissionsRationaleGroup.visibility = View.VISIBLE
            }

            UIStates.Loading -> {
                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.INVISIBLE
                permissionsRationaleGroup.visibility = View.INVISIBLE
            }

            UIStates.PermissionsGranted -> {
                progressBar.visibility = View.INVISIBLE
                recyclerView.visibility = View.VISIBLE
                permissionsRationaleGroup.visibility = View.INVISIBLE
            }

            UIStates.Finish -> {
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (permission in permissions) {
            if (
                !ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                &&
                ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            ) {
                //set to never ask again
                requestPermissionsButton.text = getString(R.string.open_settings)
                requestPermissionsButton.setOnClickListener {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.checkPermissions(this)
    }
}