package com.financeAndMoney.domains.usecase.stat

import arrow.core.NonEmptyList
import com.financeAndMoney.base.TestDispatchersProvider
import com.financeAndMoney.data.model.AccountId
import com.financeAndMoney.data.model.Expense
import com.financeAndMoney.data.model.Income
import com.financeAndMoney.data.model.PositiveValue
import com.financeAndMoney.data.model.Transaction
import com.financeAndMoney.data.model.Transfer
import com.financeAndMoney.data.model.getFromAccount
import com.financeAndMoney.data.model.getToAccount
import com.financeAndMoney.data.model.primitive.AssetCode
import com.financeAndMoney.data.model.primitive.NonNegativeInt
import com.financeAndMoney.data.model.testing.ModelFixtures
import com.financeAndMoney.data.model.testing.transaction
import com.financeAndMoney.domains.model.StatisticSummary
import com.financeAndMoney.domains.model.shouldBeApprox
import com.financeAndMoney.domains.nonEmptyExpenses
import com.financeAndMoney.domains.nonEmptyIncomes
import com.financeAndMoney.domains.nonEmptyTransfersIn
import com.financeAndMoney.domains.nonEmptyTransfersOut
import com.financeAndMoney.domains.sum
import com.financeAndMoney.domains.usecase.akaunti.AccountStats
import com.financeAndMoney.domains.usecase.akaunti.AccountStatsUseCase
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AccountStatsUseCasePropertyTest {

    private lateinit var useCase: AccountStatsUseCase

    @Before
    fun setup() {
        useCase = AccountStatsUseCase(
            dispatchers = TestDispatchersProvider,
            accountRepository = mockk(),
            exchangeUseCase = mockk(),
        )
    }

    @Test
    fun `property - ignores irrelevant transactions`() = runTest {
        // given
        val account = ModelFixtures.AccountId
        val arbIrrelevantTransaction = Arb.transaction().filter { trn ->
            trn.getFromAccount() != account && trn.getToAccount() != account
        }

        checkAll(Arb.list(arbIrrelevantTransaction)) { trns ->
            // when
            val stats = useCase.calculate(account, trns)

            // then
            stats shouldBe AccountStats.Zero
        }
    }

    @Test
    fun `property - aggregates incomes for account`() = aggregationTestsCase(
        arbTrns = { acc, asset -> Arb.nonEmptyIncomes(acc, asset) },
        extractValue = Income::value,
        expectedResultSelector = AccountStats::income
    )

    @Test
    fun `property - aggregates expenses for account`() = aggregationTestsCase(
        arbTrns = { acc, asset -> Arb.nonEmptyExpenses(acc, asset) },
        extractValue = Expense::value,
        expectedResultSelector = AccountStats::expense
    )

    @Test
    fun `property - aggregates transfer-out for account`() = aggregationTestsCase(
        arbTrns = { acc, asset -> Arb.nonEmptyTransfersOut(acc, asset) },
        extractValue = Transfer::fromValue,
        expectedResultSelector = AccountStats::transfersOut
    )

    @Test
    fun `property - aggregates transfer-in for account`() = aggregationTestsCase(
        arbTrns = { acc, asset -> Arb.nonEmptyTransfersIn(acc, asset) },
        extractValue = Transfer::toValue,
        expectedResultSelector = AccountStats::transfersIn
    )

    private fun <T : Transaction> aggregationTestsCase(
        arbTrns: (AccountId, AssetCode) -> Arb<NonEmptyList<T>>,
        extractValue: (T) -> PositiveValue,
        expectedResultSelector: (AccountStats) -> StatisticSummary,
    ) = runTest {
        // given
        val account = ModelFixtures.AccountId
        val arbEurTrns = arbTrns(account, AssetCode.EUR)
        val arbUsdTrns = arbTrns(account, AssetCode.USD)
        val arbGpbTrns = arbTrns(account, AssetCode.GBP)

        checkAll(
            arbEurTrns,
            arbUsdTrns,
            arbGpbTrns
        ) { eurTrns, usdTrns, gbpTrns ->
            // given
            val trns = (eurTrns + usdTrns + gbpTrns).shuffled()
            val expectedEur = eurTrns.map(extractValue).sum()
            val expectedUsd = usdTrns.map(extractValue).sum()
            val expectedGbp = gbpTrns.map(extractValue).sum()
            val extractedTrnsCount = eurTrns.size + usdTrns.size + gbpTrns.size

            // when
            val accStats = useCase.calculate(account, trns)

            // then
            expectedResultSelector(accStats) shouldBeApprox StatisticSummary(
                trnCount = NonNegativeInt.unsafe(extractedTrnsCount),
                values = mapOf(
                    AssetCode.EUR to expectedEur,
                    AssetCode.USD to expectedUsd,
                    AssetCode.GBP to expectedGbp,
                ),
            )
        }
    }
}