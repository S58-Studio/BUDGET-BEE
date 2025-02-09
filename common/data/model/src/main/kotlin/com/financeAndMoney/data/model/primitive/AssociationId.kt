package com.financeAndMoney.data.model.primitive

import com.financeAndMoney.data.model.sync.UniqueId
import java.util.UUID

@JvmInline
value class AssociationId(override val value: UUID) : UniqueId