package com.practise.secureauthentication.util

import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.practise.secureauthentication.domain.model.Resource

typealias OneTapSignInResource = Resource<BeginSignInResult?>

typealias TokenVerificationResource = Resource<Unit>