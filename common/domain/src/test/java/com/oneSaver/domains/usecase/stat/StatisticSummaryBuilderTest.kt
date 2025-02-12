package com.oneSaver.domains.usecase.stat

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.oneSaver.data.model.PositiveValue
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.model.primitive.NonNegativeInt
import com.oneSaver.data.model.primitive.PositiveDouble
import com.oneSaver.domains.model.StatisticSummary
import com.oneSaver.domains.usecase.StatSummaryBuilder
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class StatisticSummaryBuilderTest {

    enum class ValuesTestCase(
        val values: List<PositiveValue>,
        val expected: StatisticSummary,
    ) {
        Empty(
            values = emptyList(),
            expected = StatisticSummary.Zero
        ),
        One(
            values = listOf(
                value(1.0, AssetCode.EUR)
            ),
            expected = StatisticSummary(
                trnCount = count(1),
                values = mapOf(AssetCode.EUR to amount(1.0))
            )
        ),
        TwoInDiffCurrency(
            values = listOf(
                value(3.14, AssetCode.EUR),
                value(42.0, AssetCode.USD),
            ),
            expected = StatisticSummary(
                trnCount = count(2),
                values = mapOf(
                    AssetCode.EUR to amount(3.14),
                    AssetCode.USD to amount(42.0)
                )
            )
        ),
        TwoInSameCurrency(
            values = listOf(
                value(6.0, AssetCode.EUR),
                value(4.0, AssetCode.EUR),
            ),
            expected = StatisticSummary(
                trnCount = count(2),
                values = mapOf(
                    AssetCode.EUR to amount(10.0),
                )
            )
        ),
        TwoInSameCurrencyAndInDiffCurrency(
            values = listOf(
                value(6.0, AssetCode.EUR),
                value(4.0, AssetCode.EUR),
                value(50.0, AssetCode.USD),
            ),
            expected = StatisticSummary(
                trnCount = count(3),
                values = mapOf(
                    AssetCode.EUR to amount(10.0),
                    AssetCode.USD to amount(50.0),
                )
            )
        ),
        ThreeInSameCurrency(
            values = listOf(
                value(6.0, AssetCode.EUR),
                value(4.0, AssetCode.EUR),
                value(0.5, AssetCode.EUR),
            ),
            expected = StatisticSummary(
                trnCount = count(3),
                values = mapOf(
                    AssetCode.EUR to amount(10.5),
                )
            )
        ),
        ThreeInDiffCurrency(
            values = listOf(
                value(6.0, AssetCode.EUR),
                value(4.0, AssetCode.USD),
                value(0.5, AssetCode.GBP),
            ),
            expected = StatisticSummary(
                trnCount = count(3),
                values = mapOf(
                    AssetCode.EUR to amount(6.0),
                    AssetCode.USD to amount(4.0),
                    AssetCode.GBP to amount(0.5),
                )
            )
        ),
    }

    @Test
    fun `builds stats summary`(
        @TestParameter testCase: ValuesTestCase
    ) {
        // given
        val statSummaryBuilder = StatSummaryBuilder()

        // when
        testCase.values.forEach(statSummaryBuilder::process)
        val statSummary = statSummaryBuilder.build()

        // then
        statSummary shouldBe testCase.expected
    }

    @Test
    fun `handles 1x double overflow`() {
        // given
        val statSummaryBuilder = StatSummaryBuilder()

        // when
        statSummaryBuilder.process(value(3.14, AssetCode.EUR))
        statSummaryBuilder.process(value(Double.MAX_VALUE, AssetCode.EUR))
        val statSummary = statSummaryBuilder.build()

        // then
        statSummary shouldBe StatisticSummary(
            trnCount = count(2),
            values = mapOf(
                AssetCode.EUR to amount(Double.MAX_VALUE)
            )
        )
    }

    @Test
    fun `handles 2x double overflow`() {
        // given
        val statSummaryBuilder = StatSummaryBuilder()

        // when
        statSummaryBuilder.process(value(3.14, AssetCode.EUR))
        statSummaryBuilder.process(value(Double.MAX_VALUE, AssetCode.EUR))
        statSummaryBuilder.process(value(Double.MAX_VALUE, AssetCode.EUR))
        val statSummary = statSummaryBuilder.build()

        // then
        statSummary shouldBe StatisticSummary(
            trnCount = count(3),
            values = mapOf(
                AssetCode.EUR to amount(Double.MAX_VALUE)
            )
        )
    }

    companion object {
        private fun value(
            amount: Double,
            asset: AssetCode
        ): PositiveValue = PositiveValue(PositiveDouble.unsafe(amount), asset)

        private fun count(count: Int): NonNegativeInt = NonNegativeInt.unsafe(count)

        private fun amount(amount: Double): PositiveDouble = PositiveDouble.unsafe(amount)
    }
}