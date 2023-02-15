package com.practise.secureauthentication.presentation.activityStarters

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@Composable
fun RequestNotificationPermision(
) {
    val context = LocalContext.current
    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permissionGranted ->

        if(permissionGranted)
            Toast.makeText(context, "Permission Granted!", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
    }


}