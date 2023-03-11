package com.practise.secureauthentication.data.repository

import android.net.ConnectivityManager
import android.net.Network
import com.practise.secureauthentication.domain.repository.NetworkStatusRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class NetworkStatusRepositoryImpl @Inject constructor(
    private val connectivityManager: ConnectivityManager
): NetworkStatusRepository {

    override fun observe(): Flow<NetworkStatusRepository.Status> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(NetworkStatusRepository.Status.Available) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(NetworkStatusRepository.Status.Unavailable) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(NetworkStatusRepository.Status.Lost) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(NetworkStatusRepository.Status.Losing) }
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose { connectivityManager.unregisterNetworkCallback(callback) }

        }.distinctUntilChanged()
    }
}