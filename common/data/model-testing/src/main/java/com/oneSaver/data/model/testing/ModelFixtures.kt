package com.oneSaver.data.model.testing

import com.oneSaver.data.model.AccountId
import com.oneSaver.data.model.CategoryId
import com.oneSaver.data.model.TransactionId
import java.util.UUID

object ModelFixtures {
    val AccountId = AccountId(UUID.randomUUID())
    val CategoryId = CategoryId(UUID.randomUUID())
    val TransactionId = TransactionId(UUID.randomUUID())
}
