package com.oneSaver.domains.model

import com.oneSaver.data.model.testing.shouldBeApprox
import io.kotest.matchers.shouldBe

infix fun StatisticSummary.shouldBeApprox(other: StatisticSummary) {
    trnCount shouldBe other.trnCount
    values.keys shouldBe other.values.keys
    values.keys.forEach { key ->
        values[key]!!.value shouldBeApprox other.values[key]!!.value
    }
}