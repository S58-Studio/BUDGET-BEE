package com.oneSaver.data.model.sync

interface Identifiable<ID : UniqueId> {
    val id: ID
}
