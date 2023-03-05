package com.practise.secureauthentication.domain.usecases

import javax.inject.Inject

class UserUseCases @Inject constructor(
    val beginSignWithGoogleUseCase: BeginSignWithGoogleUseCase,
    val getUserInfoUseCase: GetUserInfoUseCase,
    val updateUserInfoUseCase: UpdateUserInfoUseCase,
    val signOutUseCase: SignOutUseCase,
    val deleteUserUseCase: DeleteUserUseCase
)