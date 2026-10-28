package com.example.hw213


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BroadcastViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BroadcastRepository(application)
    private val _events = MutableStateFlow<List<BroadcastEvent>>(emptyList())

    // Поки є підписник (UI), слухаємо бродкасти й кладемо нові події в _events,
    // а назовні віддаємо актуальний список. Через 5 с без підписників
    // callbackFlow скасовується і спрацьовує awaitClose { unregisterReceiver }.
    val events: StateFlow<List<BroadcastEvent>> = channelFlow {
        launch {
            repository.observeEvents().collect { newEvent ->
                // Нова подія — на ПОЧАТОК списку, щоб з'являлася згори
                _events.update { currentList -> listOf(newEvent) + currentList }
            }
        }
        _events.collect { send(it) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun deleteEvent(event: BroadcastEvent) {
        _events.update { currentList ->
            currentList.filterNot { it.id == event.id }
        }
    }

    fun clearAll() {
        _events.value = emptyList()
    }
}
