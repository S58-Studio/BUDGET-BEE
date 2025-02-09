package com.financeAndMoney.data.database.dao.fake

import com.financeAndMoney.data.database.dao.read.CategoryDao
import com.financeAndMoney.data.database.dao.write.WriteCategoryDao
import com.financeAndMoney.data.database.entities.KategoriEntity
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

@VisibleForTesting
class FakeCategoryDao : CategoryDao, WriteCategoryDao {
    private val items = mutableListOf<KategoriEntity>()

    override suspend fun findAll(deleted: Boolean): List<KategoriEntity> {
        return items.filter { it.isDeleted == deleted }
    }

    override suspend fun findById(id: UUID): KategoriEntity? {
        return items.find { it.id == id }
    }

    override suspend fun findMaxOrderNum(): Double? {
        return items.maxOfOrNull { it.orderNum }
    }

    override suspend fun save(value: KategoriEntity) {
        val existingItemIndex = items.indexOfFirst { it.id == value.id }
        if (existingItemIndex > -1) {
            items[existingItemIndex] = value
        } else {
            items.add(value)
        }
    }

    override suspend fun saveMany(values: List<KategoriEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun deleteById(id: UUID) {
        items.removeIf { it.id == id }
    }

    override suspend fun deleteAll() {
        items.clear()
    }
}