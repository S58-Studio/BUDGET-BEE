package com.oneSaver.importdata.csv.domain

import com.oneSaver.importdata.csv.CSVRow
import com.oneSaver.importdata.csv.ColumnMapping
import com.oneSaver.importdata.csv.MappingStatus

const val SAMPLE_SIZE = 20

fun <T, M> List<CSVRow>.parseStatus(
    mapping: ColumnMapping<M>,
    parse: (String, M) -> T?
): MappingStatus = tryStatus {
    val parsed = this.mapNotNull {
        parse(it.values[mapping.index], mapping.metadata)
    }

    MappingStatus(
        sampleValues = parsed.map { it.toString() },
        success = parsed.isNotEmpty()
    )
}

private fun tryStatus(block: () -> MappingStatus): MappingStatus = try {
    block()
} catch (e: Exception) {
    MappingStatus(sampleValues = emptyList(), success = false)
}

fun mappingFailure(): MappingStatus = MappingStatus(sampleValues = emptyList(), success = false)
