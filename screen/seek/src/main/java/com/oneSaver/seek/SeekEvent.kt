package com.oneSaver.seek

sealed interface SeekEvent {
    data class Seek(val query: String) : SeekEvent
}
