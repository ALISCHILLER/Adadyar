package com.msa.adadyar.core.common

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class OneTimeEvent<E> {
    private val channel = Channel<E>(Channel.BUFFERED)

    val events: Flow<E> = channel.receiveAsFlow()

    suspend fun emit(event: E) {
        channel.send(event)
    }
}