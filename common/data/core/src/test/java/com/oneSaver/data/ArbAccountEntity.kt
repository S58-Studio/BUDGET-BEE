package com.oneSaver.data

import com.oneSaver.data.database.entities.AkauntiEntity
import com.oneSaver.data.model.testing.colorInt
import com.oneSaver.data.model.testing.iconAsset
import com.oneSaver.data.model.testing.maybe
import com.oneSaver.data.model.testing.notBlankTrimmedString
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.removeEdgecases
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uuid

fun Arb.Companion.invalidAccountEntity(): Arb<AkauntiEntity> = arbitrary {
    val validEntity = validAccountEntity().bind()
    validEntity.copy(
        name = Arb.of("", " ", "  ").bind()
    )
}

fun Arb.Companion.validAccountEntity(): Arb<AkauntiEntity> = arbitrary {
    AkauntiEntity(
        name = Arb.notBlankTrimmedString().bind().value,
        currency = Arb.maybe(Arb.string()).bind(),
        color = Arb.colorInt().bind().value,
        icon = Arb.iconAsset().bind().id,
        orderNum = Arb.double().removeEdgecases().bind(),
        includeInBalance = Arb.boolean().bind(),
        isSynced = Arb.boolean().bind(),
        isDeleted = false,
        id = Arb.uuid().bind()
    )
}