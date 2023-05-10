package com.example.componentfollower.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.example.componentfollower.R
import com.example.componentfollower.databinding.ActivityMainBinding
import com.example.componentfollower.util.comparators.Comparation
import com.example.componentfollower.util.comparators.SORTING_KEY
import com.example.componentfollower.util.comparators.dataStore
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var radioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            mainViewModel.uiStateFlow.collect { uiState ->
                updateUI(uiState)
            }
        }

        radioGroup = findViewById(R.id.sort_group)

        if (mainViewModel.deniedPermissions(this).isNotEmpty()) {
            requestPermissions(mainViewModel.deniedPermissions(this), 1)
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mainViewModel.onBackPressed()
                }
            }
        )

        val adapter = FilesRecyclerViewAdapter(
            openFile = { file ->
                mainViewModel.openFile(file, this)
            },
            fileIconResourceProvider = mainViewModel.fileIconResourceProvider,
        )

        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            mainViewModel.filesFlow.collect { files ->
                adapter.setItems(files)
            }
        }

        lifecycleScope.launch {
            dataStore.data.collect {
                invalidateMenu()
            }
        }



        binding.exitButton.setOnClickListener {
            finish()
        }

        binding.requestPermissionsButton.setOnClickListener {
            requestPermissions(mainViewModel.deniedPermissions(this), 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuItem: MenuItem
        when (mainViewModel.sortingBy) {
            Comparation.BY_NAME -> {
                menuItem = menu.findItem(R.id.menu_sort_by_name)
            }
            Comparation.BY_SIZE -> {
                menuItem = menu.findItem(R.id.menu_sort_by_size)
            }
            Comparation.BY_EXTENSION -> {
                menuItem = menu.findItem(R.id.menu_sort_by_extension)
            }
            Comparation.BY_DATE -> {
                menuItem = menu.findItem(R.id.menu_sort_by_last_edit)
            }
            else -> {
                menuItem = menu.findItem(R.id.menu_sort_by_name)
            }
        }
        menuItem.isChecked = true
        menuInflater.inflate(R.menu.app_menu, menu)
        return true
    }

    private fun updateUI(uiState: UIStates) = when (uiState) {
        is UIStates.PermissionsDenied -> {
            binding.permissionsRationaleText.text = getString(
                R.string.permissions_rationale,
                mainViewModel.deniedPermissions(this).joinToString("/n")
            )
            binding.permissionsDeniedGroup.visibility = View.INVISIBLE

            binding.progressBar.visibility = View.INVISIBLE
            binding.emptyFolderGroup.visibility = View.INVISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
            binding.systemFolderGroup.visibility = View.INVISIBLE
        }

        UIStates.Loading -> {
            binding.progressBar.visibility = View.VISIBLE

            binding.emptyFolderGroup.visibility = View.INVISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
            binding.systemFolderGroup.visibility = View.INVISIBLE
            binding.permissionsDeniedGroup.visibility = View.INVISIBLE
        }

        UIStates.EmptyFolder -> {
            binding.emptyFolderGroup.visibility = View.VISIBLE

            binding.recyclerView.visibility = View.INVISIBLE
            binding.systemFolderGroup.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.INVISIBLE
            binding.permissionsDeniedGroup.visibility = View.INVISIBLE
        }

        UIStates.FilesLoaded -> {
            binding.recyclerView.visibility = View.VISIBLE

            binding.systemFolderGroup.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.INVISIBLE
            binding.permissionsDeniedGroup.visibility = View.INVISIBLE
            binding.emptyFolderGroup.visibility = View.INVISIBLE
        }

        UIStates.SystemFolder -> {
            binding.systemFolderGroup.visibility = View.VISIBLE

            binding.progressBar.visibility = View.INVISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
            binding.permissionsDeniedGroup.visibility = View.INVISIBLE
            binding.emptyFolderGroup.visibility = View.INVISIBLE
        }

        UIStates.SomePermissionSetToNeverAskAgain -> {
            binding.permissionsRationaleText.text = getString(
                R.string.permissions_rationale,
                mainViewModel.deniedPermissions(this).joinToString("/n")
            )

            binding.requestPermissionsButton.text = getString(R.string.open_settings)

            binding.requestPermissionsButton.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }

            binding.permissionsDeniedGroup.visibility = View.VISIBLE

            binding.progressBar.visibility = View.INVISIBLE
            binding.emptyFolderGroup.visibility = View.INVISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
            binding.systemFolderGroup.visibility = View.INVISIBLE
        }

        UIStates.Finish -> {
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.quit) {
            finish()
        }

        val x: Int = when (item.itemId) {
            R.id.menu_sort_by_name -> {
                Comparation.BY_NAME
            }

            R.id.menu_sort_by_size -> {
                Comparation.BY_SIZE
            }

            R.id.menu_sort_by_extension -> {
                Comparation.BY_EXTENSION
            }

            R.id.menu_sort_by_last_edit -> {
                Comparation.BY_DATE
            }

            else -> {
                throw IllegalStateException()
            }
        }

        lifecycleScope.launch {
            applicationContext.dataStore.edit { preferences ->
                preferences[intPreferencesKey(SORTING_KEY)] = x
            }
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.checkPermissions(this)
    }
}