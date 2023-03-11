package com.practise.secureauthentication.data.repository

import com.google.android.gms.auth.api.identity.SignInClient
import com.practise.secureauthentication.data.datasource.user.UserLocalDataSource
import com.practise.secureauthentication.data.datasource.user.UserRemoteDataSource
import com.practise.secureauthentication.data.model.ResourceError
import com.practise.secureauthentication.data.model.user.User
import com.practise.secureauthentication.data.model.user.UserUpdate
import com.practise.secureauthentication.data.util.suspendRunCatching
import com.practise.secureauthentication.domain.repository.UserRepository
import domain.model.ApiResponse
import io.ktor.client.call.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
    private val oneTapClient: SignInClient
): UserRepository {

    override fun getUserInfo(): Flow<Result<User>> = channelFlow {
        launch {
            val user = remoteDataSource.getUserInfo().body<ApiResponse<User>>().data
            localDataSource.updateUserInfo { user }
        }

        launch {
            localDataSource.getUserInfo().mapNotNull { it }.collectLatest {
                send(Result.success(it))
            }
        }
    }.catch {
        getCachedUserInfo().getOrNull()?.let { user ->
            emit(Result.success(user))
        } ?: kotlin.run {
            val error = ResourceError.identifyError(it)
            emit(Result.failure(error))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun getCachedUserInfo() = suspendRunCatching {
        localDataSource.getUserInfo().firstOrNull()
    }

    override suspend fun updateUserInfo(userUpdate: UserUpdate) = suspendRunCatching<Unit> {
        remoteDataSource.updateUserInfo(userUpdate)
        localDataSource.updateUserInfo {
            it?.copy(name = "${userUpdate.firstName} ${userUpdate.lastName}")
        }
    }.recoverCatching { throw ResourceError.identifyError(it) }


    override suspend fun deleteUser() = suspendRunCatching<Unit> {
        remoteDataSource.deleteUser()
        localDataSource.updateUserInfo { null }
        oneTapClient.signOut().await()
    }.recoverCatching { throw ResourceError.identifyError(it) }


    override suspend fun signOut() = suspendRunCatching<Unit> {
        remoteDataSource.signOutUser()
        localDataSource.updateUserInfo { null }
        oneTapClient.signOut().await()
    }.recoverCatching { throw ResourceError.identifyError(it) }


}