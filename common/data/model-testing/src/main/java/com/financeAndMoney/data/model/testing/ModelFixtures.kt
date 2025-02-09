package com.financeAndMoney.data.model.testing

import com.financeAndMoney.data.model.AccountId
import com.financeAndMoney.data.model.CategoryId
import com.financeAndMoney.data.model.TransactionId
import java.util.UUID

object ModelFixtures {
    val AccountId = AccountId(UUID.randomUUID())
    val CategoryId = CategoryId(UUID.randomUUID())
    val TransactionId = TransactionId(UUID.randomUUID())
}
