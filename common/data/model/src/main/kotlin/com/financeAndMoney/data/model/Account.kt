package com.financeAndMoney.data.model

import com.financeAndMoney.data.model.primitive.AssetCode
import com.financeAndMoney.data.model.primitive.ColorInt
import com.financeAndMoney.data.model.primitive.IconAsset
import com.financeAndMoney.data.model.primitive.NotBlankTrimmedString
import com.financeAndMoney.data.model.sync.Identifiable
import com.financeAndMoney.data.model.sync.UniqueId
import java.util.UUID

@JvmInline
value class AccountId(override val value: UUID) : UniqueId

data class Account(
    override val id: AccountId,
    val name: NotBlankTrimmedString,
    val asset: AssetCode,
    val color: ColorInt,
    val icon: IconAsset?,
    val includeInBalance: Boolean,
    override val orderNum: Double,
) : Identifiable<AccountId>, Reorderable
