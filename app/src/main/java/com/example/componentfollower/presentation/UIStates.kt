package com.example.componentfollower.presentation

sealed class UIStates {

    class PermissionsDenied(val deniedPermissions: Array<String>) : UIStates()

    object Loading : UIStates()

    object PermissionsGranted : UIStates()

    object Finish : UIStates()
}