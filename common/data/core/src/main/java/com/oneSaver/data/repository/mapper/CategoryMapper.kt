package com.oneSaver.data.repository.mapper

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.oneSaver.data.database.entities.KategoriEntity
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.IconAsset
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import javax.inject.Inject

class CategoryMapper @Inject constructor() {
    fun KategoriEntity.toDomain(): Either<String, Category> = either {
        ensure(!isDeleted) { "Category is deleted" }

        Category(
            id = com.oneSaver.data.model.CategoryId(id),
            name = NotBlankTrimmedString.from(name).bind(),
            color = ColorInt(color),
            icon = icon?.let(IconAsset::from)?.getOrNull(),
            orderNum = orderNum,
        )
    }

    fun Category.toEntity(): KategoriEntity {
        return KategoriEntity(
            name = name.value,
            color = color.value,
            icon = icon?.id,
            orderNum = orderNum,
            isSynced = true,
            id = id.value
        )
    }
}
