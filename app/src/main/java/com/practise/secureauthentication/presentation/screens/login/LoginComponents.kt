package com.practise.secureauthentication.presentation.screens.login

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.practise.secureauthentication.R
import com.practise.secureauthentication.domain.model.Resource
import com.practise.secureauthentication.domain.model.TokenId
import com.practise.secureauthentication.ui.theme.LoadingBlue
import com.practise.secureauthentication.util.OneTapSignInResource

@Composable
fun GoogleButton(
    modifier: Modifier = Modifier,
    loadingState: Boolean,
    primaryText: String,
    secondaryText: String,
    onClick: () -> Unit
) {

    val buttonText = rememberSaveable(loadingState) {
        if(loadingState)
            secondaryText
        else primaryText
    }

    OutlinedButton(
        modifier = modifier
            .height(40.dp)
            .animateContentSize(tween(300)),
        onClick = {
            if(!loadingState)
                onClick()
        },
        border = BorderStroke(1.dp, color = Color.LightGray),
        contentPadding = PaddingValues(8.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google icon",
            modifier = Modifier
                .size(18.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = buttonText,
        )
        if(loadingState)
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Color.LoadingBlue,
                strokeWidth = 2.dp
            )
    }
}
@Preview(showBackground = true)
@Composable
fun DividerWithCenteredText(
    modifier: Modifier = Modifier,
    dividerThickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
    text: String = "OR",
    textStyle: TextStyle = TextStyle.Default
) {
    val color = DividerDefaults.color
    Row(
        modifier = modifier.padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier.weight(0.4f),
            thickness = dividerThickness,
            color = color
        )
        Text(
            modifier = Modifier.padding(10.dp).weight(.1f),
            text = text,
            style = textStyle,
            color = color
        )
        Divider(
            modifier = Modifier.weight(.4f),
            thickness = dividerThickness,
            color = color
        )
    }
}


@Composable
fun StartActivityForSignInWithGoogleResult(
    onTokenReceived: (TokenId) -> Unit,
    onDialogDismissed: () -> Unit,
    onNoCredentialsFound: () -> Unit,
    oneTapClient: SignInClient,
    oneTapSignInResource: OneTapSignInResource,
    detectBlockedCaller: suspend (Throwable) -> Boolean,
    onCallerBlocked: () -> Unit,
) {
    val TAG = "StartActivityForResult"
    val clipboardManager = LocalClipboardManager.current
    val activityLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        try {
            if(activityResult.resultCode == Activity.RESULT_OK) {
                val credentials = oneTapClient.getSignInCredentialFromIntent(activityResult.data)
                val tokenId = credentials.googleIdToken
                Log.d(TAG, "Token ID: $tokenId")
                tokenId?.let {
                    clipboardManager.setText(AnnotatedString(it))
                    onTokenReceived(TokenId(it))
                }
            }
            else{
                Log.d(TAG, "BLACK SCRIM CLICKED, DIALOG CLOSED")
                onDialogDismissed()
            }
        } catch (e: ApiException) {
            when(e.statusCode) {
                CommonStatusCodes.CANCELED -> Log.d(TAG, "ONE-TAP DIALOG CANCELED")
                CommonStatusCodes.NETWORK_ERROR -> Log.d(TAG, "ONE-TAP NETWORK ERROR.")
                else -> Log.d(TAG, "${e.message}")
            }
            onDialogDismissed()
        }
    }

    when(oneTapSignInResource) {
        is Resource.Success -> oneTapSignInResource.data?.let {
            LaunchedEffect(key1 = it) {
                val intent = IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
                activityLauncher.launch(intent)
            }
        }
        is Resource.Failure -> {
            LaunchedEffect(key1 = oneTapSignInResource) {
                val isBlocked = detectBlockedCaller(oneTapSignInResource.throwable)
                Log.d(TAG, "StartActivityForSignInResult: ${oneTapSignInResource.throwable.message}")
                if(isBlocked)
                    onCallerBlocked()
                else
                    onNoCredentialsFound()
            }
        }
        else -> {}
    }

}

@Composable
fun TokenVerificationStateHandler(
    tokenVerificationResource: Resource<Unit>,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val TAG = "TokenVerificationStateHandler"
    when (tokenVerificationResource) {
        is Resource.Idle -> {}
        is Resource.Loading -> Log.d(TAG, "Token Verification in Loading state")
        is Resource.Success ->
            LaunchedEffect(key1 = Unit) {
                Log.d(TAG, "Token Verification Success!")
                onSuccess()
            }

        is Resource.Failure ->
            LaunchedEffect(key1 = Unit) {
                Log.d(TAG, "Token Verification Failed!")
                onFailure()
            }
    }
}
