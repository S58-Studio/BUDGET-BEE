package com.oneSaver.data.repository.mapper

import com.oneSaver.data.database.entities.CategoryEntity
import com.oneSaver.data.model.CategoryId
import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test
import java.util.UUID

class CategoryMapperTest {
    private lateinit var mapper: CategoryMapper

    @Before
    fun setup() {
        mapper = CategoryMapper()
    }

    @Test
    fun `maps domain to entity`() {
        // given
        val category = com.oneSaver.data.model.Category(
            name = NotBlankTrimmedString.unsafe("Home"),
            color = ColorInt(42),
            icon = null,
            orderNum = 1.0,
            id = CategoryId
        )

        // when
        val res = with(mapper) { category.toEntity() }

        // then
        res shouldBe CategoryEntity(
            name = "Home",
            color = 42,
            icon = null,
            orderNum = 1.0,
            isSynced = true,
            isDeleted = false,
            id = CategoryId.value
        )
    }

    @Test
    fun `maps entity to domain - valid entity`() {
        // when
        val res = with(mapper) { ValidEntity.toDomain() }

        // then
        res.shouldBeRight() shouldBe com.oneSaver.data.model.Category(
            name = NotBlankTrimmedString.unsafe("Home"),
            color = ColorInt(42),
            icon = null,
            orderNum = 1.0,
            id = CategoryId
        )
    }

    @Test
    fun `maps entity to domain - name missing`() {
        // given
        val corruptedEntity = ValidEntity.copy(name = "")

        // when
        val res = with(mapper) { corruptedEntity.toDomain() }

        // then
        res.shouldBeLeft()
    }

    @Test
    fun `maps entity to domain - deleted categories are not valid`() {
        // given
        val corruptedEntity = ValidEntity.copy(isDeleted = true)

        // when
        val res = with(mapper) { corruptedEntity.toDomain() }

        // then
        res.shouldBeLeft()
    }

    @Test
    fun `maps entity to domain - missing icon is okay`() {
        // given
        val missingIconEntity = ValidEntity.copy(icon = null)

        // when
        val res = with(mapper) { missingIconEntity.toDomain() }

        // then
        res.shouldBeRight()
    }

    @Test
    fun `maps entity to domain - invalid icon is okay`() {
        // given
        val invalidIconEntity = ValidEntity.copy(icon = "invalid icon")

        // when
        val result = with(mapper) { invalidIconEntity.toDomain() }

        // then
        result.shouldBeRight()
    }

    companion object {
        val CategoryId = CategoryId(UUID.randomUUID())

        val ValidEntity = CategoryEntity(
            name = "Home",
            color = 42,
            icon = null,
            orderNum = 1.0,
            isSynced = true,
            isDeleted = false,
            id = CategoryId.value
        )
    }
}
