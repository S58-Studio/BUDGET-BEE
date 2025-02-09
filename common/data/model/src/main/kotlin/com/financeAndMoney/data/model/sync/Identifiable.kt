package com.financeAndMoney.data.model.sync

interface Identifiable<ID : UniqueId> {
    val id: ID
}
