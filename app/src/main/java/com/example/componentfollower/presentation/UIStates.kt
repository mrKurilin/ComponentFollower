package com.example.componentfollower.presentation

sealed class UIStates {

    object PermissionsDenied : UIStates()

    object Loading : UIStates()

    object FilesLoaded : UIStates()

    object EmptyFolder : UIStates()

    object SystemFolder : UIStates()

    object SomePermissionSetToNeverAskAgain : UIStates()

    object Finish : UIStates()
}