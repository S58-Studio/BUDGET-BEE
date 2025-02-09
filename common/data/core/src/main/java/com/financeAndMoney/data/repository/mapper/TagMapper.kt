package com.financeAndMoney.data.repository.mapper

import android.graphics.Color
import arrow.core.Either
import arrow.core.raise.either
import com.financeAndMoney.data.database.entities.TagAssociationEntity
import com.financeAndMoney.data.database.entities.TagEntity
import com.financeAndMoney.data.model.Tag
import com.financeAndMoney.data.model.TagAssociation
import com.financeAndMoney.data.model.primitive.AssociationId
import com.financeAndMoney.data.model.primitive.ColorInt
import com.financeAndMoney.data.model.primitive.IconAsset
import com.financeAndMoney.data.model.primitive.NotBlankTrimmedString
import com.financeAndMoney.data.model.TagId
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class TagMapper @Inject constructor() {
    companion object {
        fun createNewTagId(): TagId = TagId(UUID.randomUUID())
    }

    fun TagEntity.toDomain(): Either<String, Tag> = either {
        Tag(
            id = TagId(id),
            name = NotBlankTrimmedString.from(name).bind(),
            description = description,
            color = ColorInt(color),
            icon = icon?.let(IconAsset::from)?.getOrNull(),
            orderNum = orderNum,
            creationTimestamp = dateTime,
        )
    }

    fun Tag.toEntity(): TagEntity {
        return TagEntity(
            id = id.value,
            name = name.value,
            description = description,
            color = color.value,
            icon = icon?.id,
            orderNum = orderNum,
            dateTime = creationTimestamp,
            lastSyncedTime = Instant.EPOCH,
            isDeleted = false,
        )
    }

    fun TagAssociation.toEntity(): TagAssociationEntity {
        return TagAssociationEntity(
            tagId = id.value,
            associatedId = associatedId.value,
            lastSyncedTime = Instant.EPOCH,
            isDeleted = false,
        )
    }

    fun TagAssociationEntity.toDomain(): TagAssociation {
        return TagAssociation(
            id = TagId(tagId),
            associatedId = AssociationId(associatedId),
        )
    }

    fun createNewTag(tagId: TagId = createNewTagId(), name: NotBlankTrimmedString): Tag {
        return Tag(
            id = tagId,
            name = name,
            description = null,
            color = ColorInt(Color.TRANSPARENT),
            icon = null,
            orderNum = 0.0,
            creationTimestamp = Instant.now(),
        )
    }

    fun createNewTagAssociation(tagId: TagId, associationId: AssociationId): TagAssociation {
        return TagAssociation(
            id = tagId,
            associatedId = associationId,
        )
    }
}