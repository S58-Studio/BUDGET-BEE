package com.oneSaver.data.model.primitive

import com.oneSaver.data.model.sync.UniqueId
import java.util.UUID

@JvmInline
value class AssociationId(override val value: UUID) : UniqueId