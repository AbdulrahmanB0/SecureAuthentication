package com.practise.secureauthentication.domain.usecases

import com.practise.secureauthentication.data.RemoteResource
import com.practise.secureauthentication.data.network.ApiErrors
import com.practise.secureauthentication.domain.model.UserUpdate
import com.practise.secureauthentication.domain.repository.CacheRepository
import com.practise.secureauthentication.domain.repository.KtorApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateUserInfoUseCase @Inject constructor(
    private val api: KtorApiRepository,
    private val cache: CacheRepository
) {
    suspend operator fun invoke(userUpdate: UserUpdate) = flow {
        emit(RemoteResource.Loading)
        api.updateUserInfo(userUpdate)
        cache.updateUserInfo {
            it?.copy(name = userUpdate.run { "$firstName $lastName" })
        }
        emit(RemoteResource.Success(Unit))
    }.catch {
        val error = ApiErrors.identifyError(it)
        emit(RemoteResource.Failure(error))
    }.flowOn(Dispatchers.IO)
}