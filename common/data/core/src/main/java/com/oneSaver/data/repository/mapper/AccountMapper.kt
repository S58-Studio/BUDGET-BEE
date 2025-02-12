package com.oneSaver.data.repository.mapper

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.oneSaver.data.database.entities.AkauntiEntity
import com.oneSaver.data.model.Account
import com.oneSaver.data.model.AccountId
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.IconAsset
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.data.repository.CurrencyRepository
import javax.inject.Inject

class AccountMapper @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {
    suspend fun AkauntiEntity.toDomain(): Either<String, Account> = either {
        ensure(!isDeleted) { "Account is deleted" }

        Account(
            id = AccountId(id),
            name = NotBlankTrimmedString.from(name).bind(),
            asset = currency?.let(AssetCode::from)?.getOrNull()
                ?: currencyRepository.getBaseCurrency(),
            color = ColorInt(color),
            icon = icon?.let(IconAsset::from)?.getOrNull(),
            includeInBalance = includeInBalance,
            orderNum = orderNum,
        )
    }

    fun Account.toEntity(): AkauntiEntity {
        return AkauntiEntity(
            name = name.value,
            currency = asset.code,
            color = color.value,
            icon = icon?.id,
            orderNum = orderNum,
            includeInBalance = includeInBalance,
            id = id.value,
            isSynced = true, // TODO: Delete this
        )
    }
}
