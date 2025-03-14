package com.oneSaver.data.repository

import com.oneSaver.base.TestDispatchersProvider
import com.oneSaver.data.database.dao.read.CategoryDao
import com.oneSaver.data.database.dao.write.WriteCategoryDao
import com.oneSaver.data.database.entities.CategoryEntity
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.CategoryId
import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.data.repository.fake.fakeRepositoryMemoFactory
import com.oneSaver.data.repository.mapper.CategoryMapper
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID

class CategoryRepositoryTest {
    private val categoryDao = mockk<CategoryDao>()
    private val writeCategoryDao = mockk<WriteCategoryDao>()

    private lateinit var repository: CategoryRepository

    @Before
    fun setup() {
        repository = CategoryRepository(
            mapper = CategoryMapper(),
            categoryDao = categoryDao,
            writeCategoryDao = writeCategoryDao,
            dispatchersProvider = TestDispatchersProvider,
            memoFactory = fakeRepositoryMemoFactory(),
        )
    }

    @Test
    fun `find all not deleted - empty list`() = runTest {
        // given
        coEvery { categoryDao.findAll(false) } returns emptyList()

        // when
        val res = repository.findAll()

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find all not deleted - valid and invalid categories`() = runTest {
        // given
        val id1 = UUID.randomUUID()
        val id3 = UUID.randomUUID()
        coEvery { categoryDao.findAll(false) } returns listOf(
            CategoryEntity(
                name = "Home",
                color = 42,
                icon = null,
                orderNum = 0.0,
                isSynced = true,
                isDeleted = false,
                id = id1
            ),
            CategoryEntity(
                name = "",
                color = 42,
                icon = null,
                orderNum = 1.0,
                isSynced = true,
                isDeleted = false,
                id = UUID.randomUUID()
            ),
            CategoryEntity(
                name = "Fun",
                color = 42,
                icon = null,
                orderNum = 2.0,
                isSynced = true,
                isDeleted = false,
                id = id3
            )
        )

        // when
        val res = repository.findAll()

        // then
        res shouldBe listOf(
            Category(
                name = NotBlankTrimmedString.unsafe("Home"),
                color = ColorInt(42),
                icon = null,
                orderNum = 0.0,
                id = CategoryId(id1)
            ),
            Category(
                name = NotBlankTrimmedString.unsafe("Fun"),
                color = ColorInt(42),
                icon = null,
                orderNum = 2.0,
                id = CategoryId(id3)
            )
        )
    }

    @Test
    fun `find by id - null CategoryEntity`() = runTest {
        // given
        val id = UUID.randomUUID()
        coEvery { categoryDao.findById(id) } returns null

        // when
        val category = repository.findById(CategoryId(id))

        // then
        category shouldBe null
    }

    @Test
    fun `find by id - valid CategoryEntity`() = runTest {
        // given
        val id = UUID.randomUUID()
        coEvery { categoryDao.findById(id) } returns CategoryEntity(
            name = "Home",
            color = 42,
            icon = null,
            orderNum = 0.0,
            isSynced = true,
            isDeleted = false,
            id = id
        )

        // when
        val category = repository.findById(CategoryId(id))

        // then
        category shouldBe Category(
            name = NotBlankTrimmedString.unsafe("Home"),
            color = ColorInt(42),
            icon = null,
            orderNum = 0.0,
            id = CategoryId(id)
        )
    }

    @Test
    fun `find by id - invalid CategoryEntity`() = runTest {
        // given
        val id = UUID.randomUUID()
        coEvery { categoryDao.findById(id) } returns CategoryEntity(
            name = "",
            color = 42,
            icon = null,
            orderNum = 1.0,
            isSynced = true,
            isDeleted = false,
            id = UUID.randomUUID()
        )

        // when
        val category = repository.findById(CategoryId(id))

        // then
        category shouldBe null
    }

    @Test
    fun `find max order num - null from the source`() = runTest {
        // given
        coEvery { categoryDao.findMaxOrderNum() } returns null

        // when
        val num = repository.findMaxOrderNum()

        // then
        num shouldBe 0.0
    }

    @Test
    fun `find max order num - number from the source`() = runTest {
        // given
        coEvery { categoryDao.findMaxOrderNum() } returns 15.0

        // when
        val num = repository.findMaxOrderNum()

        // then
        num shouldBe 15.0
    }

    @Test
    fun save() = runTest {
        // given
        val id = UUID.randomUUID()
        val category = Category(
            name = NotBlankTrimmedString.unsafe("Home"),
            color = ColorInt(42),
            icon = null,
            orderNum = 3.0,
            id = CategoryId(id)
        )
        coEvery { writeCategoryDao.save(any()) } just runs

        // when
        repository.save(category)

        // then
        coVerify(exactly = 1) {
            writeCategoryDao.save(
                CategoryEntity(
                    name = "Home",
                    color = 42,
                    icon = null,
                    orderNum = 3.0,
                    isSynced = true,
                    isDeleted = false,
                    id = id
                )
            )
        }
    }

    @Test
    fun `save many`() = runTest {
        // given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val id3 = UUID.randomUUID()
        val categories = listOf(
            Category(
                name = NotBlankTrimmedString.unsafe("Home"),
                color = ColorInt(42),
                icon = null,
                orderNum = 3.0,
                id = CategoryId(id1)
            ),
            Category(
                name = NotBlankTrimmedString.unsafe("Fun"),
                color = ColorInt(42),
                icon = null,
                orderNum = 4.0,
                id = CategoryId(id2)
            ),
            Category(
                name = NotBlankTrimmedString.unsafe("Health"),
                color = ColorInt(42),
                icon = null,
                orderNum = 5.0,
                id = CategoryId(id3)
            )
        )
        coEvery { writeCategoryDao.saveMany(any()) } just runs

        // when
        repository.saveMany(categories)

        // then
        coVerify(exactly = 1) {
            writeCategoryDao.saveMany(
                listOf(
                    CategoryEntity(
                        name = "Home",
                        color = 42,
                        icon = null,
                        orderNum = 3.0,
                        isSynced = true,
                        isDeleted = false,
                        id = id1
                    ),

                    CategoryEntity(
                        name = "Fun",
                        color = 42,
                        icon = null,
                        orderNum = 4.0,
                        isSynced = true,
                        isDeleted = false,
                        id = id2
                    ),

                    CategoryEntity(
                        name = "Health",
                        color = 42,
                        icon = null,
                        orderNum = 5.0,
                        isSynced = true,
                        isDeleted = false,
                        id = id3
                    )
                )
            )
        }
    }

    @Test
    fun `delete by id`() = runTest {
        // given
        val categoryId = CategoryId(UUID.randomUUID())
        coEvery { writeCategoryDao.deleteById(any()) } just runs

        // when
        repository.deleteById(categoryId)

        // then
        coVerify(exactly = 1) {
            writeCategoryDao.deleteById(categoryId.value)
        }
    }

    @Test
    fun `delete all`() = runTest {
        // given
        coEvery { writeCategoryDao.deleteAll() } just runs

        // when
        repository.deleteAll()

        // then
        coVerify(exactly = 1) {
            writeCategoryDao.deleteAll()
        }
    }
}
