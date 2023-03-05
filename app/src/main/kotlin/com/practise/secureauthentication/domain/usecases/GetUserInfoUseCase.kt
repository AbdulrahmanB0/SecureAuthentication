package com.practise.secureauthentication.domain.usecases

import com.practise.secureauthentication.data.RemoteResource
import com.practise.secureauthentication.data.network.ApiErrors
import com.practise.secureauthentication.domain.repository.CacheRepository
import com.practise.secureauthentication.domain.repository.KtorApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val api: KtorApiRepository,
    private val cache: CacheRepository
){
    operator fun invoke() = channelFlow {
        send(RemoteResource.Loading)
        launch {
            cache.getUserInfo().mapNotNull { it }.collectLatest {
                send(RemoteResource.Success(it))
            }
        }

        launch {
            api.getUserInfo().let { response ->
                cache.updateUserInfo { response.data }
            }
        }

    }.catch {
        val error = ApiErrors.identifyError(it)
        emit(RemoteResource.Failure(error, data = cache.getUserInfo().firstOrNull()))
    }.flowOn(Dispatchers.IO)
}