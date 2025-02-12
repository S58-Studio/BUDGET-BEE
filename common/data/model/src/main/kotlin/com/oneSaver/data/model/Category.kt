package com.oneSaver.data.model

import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.IconAsset
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.data.model.sync.Identifiable
import com.oneSaver.data.model.sync.UniqueId
import java.util.UUID

@JvmInline
value class CategoryId(override val value: UUID) : UniqueId

data class Category(
    override val id: CategoryId,
    val name: NotBlankTrimmedString,
    val color: ColorInt,
    val icon: IconAsset?,
    override val orderNum: Double,
) : Identifiable<CategoryId>, Reorderable