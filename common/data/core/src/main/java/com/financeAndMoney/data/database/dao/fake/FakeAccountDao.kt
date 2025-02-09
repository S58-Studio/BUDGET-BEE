package com.financeAndMoney.data.database.dao.fake

import com.financeAndMoney.data.database.dao.read.AccountDao
import com.financeAndMoney.data.database.dao.write.WriteAccountDao
import com.financeAndMoney.data.database.entities.AkauntiEntity
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

@VisibleForTesting
class FakeAccountDao : AccountDao, WriteAccountDao {
    private val accounts = mutableMapOf<UUID, AkauntiEntity>()

    override suspend fun findAll(deleted: Boolean): List<AkauntiEntity> {
        return accounts.filterValues { it.isDeleted == deleted }.values.toList()
    }

    override suspend fun findById(id: UUID): AkauntiEntity? {
        return accounts[id]
    }

    override suspend fun findMaxOrderNum(): Double? {
        return accounts.maxOfOrNull { (_, entity) -> entity.orderNum }
    }

    override suspend fun save(value: AkauntiEntity) {
        accounts[value.id] = value
    }

    override suspend fun saveMany(values: List<AkauntiEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun deleteById(id: UUID) {
        accounts.remove(id)
    }

    override suspend fun deleteAll() {
        accounts.clear()
    }
}