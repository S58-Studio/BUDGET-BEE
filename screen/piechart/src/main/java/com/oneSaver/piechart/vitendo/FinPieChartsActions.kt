package com.oneSaver.piechart.vitendo

import androidx.compose.ui.graphics.toArgb
import com.oneSaver.base.legacy.Transaction
import com.oneSaver.base.legacy.stringRes
import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.CategoryId
import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.IconAsset
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.data.repository.CategoryRepository
import com.oneSaver.design.l0_system.RedLight
import com.oneSaver.frp.Pure
import com.oneSaver.frp.SideEffect
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.action.thenFilter
import com.oneSaver.frp.action.thenMap
import com.oneSaver.frp.then
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.piechart.KategoriAmount
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.domain.action.account.AccountsAct
import com.oneSaver.allStatus.domain.action.category.LegacyCategoryIncomeWithAccountFiltersAct
import com.oneSaver.allStatus.domain.action.transaction.LegacyCalcTrnsIncomeExpenseAct
import com.oneSaver.allStatus.domain.action.transaction.TrnsWithRangeAndAccFiltersAct
import com.oneSaver.allStatus.domain.pure.account.filterExcluded
import com.oneSaver.allStatus.domain.pure.data.IncomeExpenseTransferPair
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class FinPieChartsActions @Inject constructor(
    private val accountsAct: AccountsAct,
    private val trnsWithRangeAndAccFiltersAct: TrnsWithRangeAndAccFiltersAct,
    private val calcTrnsIncomeExpenseAct: LegacyCalcTrnsIncomeExpenseAct,
    private val categoryRepository: CategoryRepository,
    private val categoryIncomeWithAccountFiltersAct: LegacyCategoryIncomeWithAccountFiltersAct,
) : FPAction<FinPieChartsActions.Input, FinPieChartsActions.Output>() {

    private val accountTransfersCategory =
        Category(
            name = NotBlankTrimmedString.unsafe(stringRes(R.string.account_transfers)),
            color = ColorInt(RedLight.toArgb()),
            icon = IconAsset.unsafe("transfer"),
            id = CategoryId(UUID.randomUUID()),
            orderNum = 0.0,
        )

    override suspend fun Input.compose(): suspend () -> Output = suspend {
        getUsableAccounts(
            accountIdFilterList = accountIdFilterList,
            allAccounts = suspend { accountsAct(Unit) }
        )
    } then {
        val accountsUsed = it.first
        val accountIdFilterSet = it.second

        val transactions = existingTransactions.ifEmpty {
            trnsWithRangeAndAccFiltersAct(
                TrnsWithRangeAndAccFiltersAct.Input(
                    range = range,
                    accountIdFilterSet = accountIdFilterSet
                )
            )
        }

        Pair(accountsUsed, transactions)
    } then {
        val accountsUsed = it.first
        val transactions = it.second

        val incomeExpenseTransfer = calcTrnsIncomeExpenseAct(
            LegacyCalcTrnsIncomeExpenseAct.Input(
                transactions = transactions,
                accounts = accountsUsed,
                baseCurrency = baseCurrency
            )
        )

        val categoryAmounts = suspend {
            calculateCategoryAmounts(
                type = type,
                baseCurrency = baseCurrency,
                allCategories = suspend {
                    // add null element for unspecified category
                    categoryRepository.findAll().plus(null)
                },
                transactions = suspend { transactions },
                accountsUsed = suspend { accountsUsed },
                addAssociatedTransToCategoryAmt = existingTransactions.isNotEmpty()
            )
        } then {
            addAccountTransfersCategory(
                showAccountTransfersCategory = showAccountTransfersCategory,
                type = type,
                accountTransfersCategory = accountTransfersCategory,
                accountIdFilterSet = accountIdFilterList.toHashSet(),
                incomeExpenseTransfer = suspend { incomeExpenseTransfer },
                kategoriAmounts = suspend { it },
                transactions = suspend { transactions }
            )
        }

        Pair(incomeExpenseTransfer, categoryAmounts())
    } then {
        val totalAmount = calculateTotalAmount(
            type = type,
            treatTransferAsIncExp = treatTransferAsIncExp,
            incomeExpenseTransfer = suspend { it.first }
        )

        val catAmountList = it.second

        Pair(totalAmount, catAmountList)
    } then {
        Output(it.first.toDouble(), it.second.toImmutableList())
    }

    @Pure
    private suspend fun getUsableAccounts(
        accountIdFilterList: List<UUID>,

        @SideEffect
        allAccounts: suspend () -> List<Account>,
    ): Pair<List<Account>, Set<UUID>> {
        val accountsUsed = if (accountIdFilterList.isEmpty()) {
            allAccounts then ::filterExcluded
        } else {
            allAccounts thenFilter {
                accountIdFilterList.contains(it.id)
            }
        }

        val accountsUsedIDSet = accountsUsed thenMap { it.id } then { it.toHashSet() }

        return Pair(accountsUsed(), accountsUsedIDSet())
    }

    @Pure
    private suspend fun calculateCategoryAmounts(
        type: TransactionType,
        baseCurrency: String,
        addAssociatedTransToCategoryAmt: Boolean = false,

        @SideEffect
        allCategories: suspend () -> List<Category?>,

        @SideEffect
        transactions: suspend () -> List<Transaction>,

        @SideEffect
        accountsUsed: suspend () -> List<Account>,
    ): List<KategoriAmount> {
        val trans = transactions()
        val accUsed = accountsUsed()

        val catAmtList = allCategories thenMap { category ->
            val categoryTransactions = asyncIo {
                if (addAssociatedTransToCategoryAmt) {
                    trans.filter {
                        it.type == type && it.categoryId == category?.id?.value
                    }
                } else {
                    emptyList()
                }
            }

            val catIncomeExpense = categoryIncomeWithAccountFiltersAct(
                LegacyCategoryIncomeWithAccountFiltersAct.Input(
                    transactions = trans,
                    accountFilterList = accUsed,
                    category = category,
                    baseCurrency = baseCurrency
                )
            )

            KategoriAmount(
                category = category,
                amount = when (type) {
                    TransactionType.INCOME -> catIncomeExpense.income.toDouble()
                    TransactionType.EXPENSE -> catIncomeExpense.expense.toDouble()
                    else -> error("not supported transactionType - $type")
                },
                associatedTransactions = categoryTransactions.await(),
                isCategoryUnspecified = category == null
            )
        } thenFilter { catAmt ->
            catAmt.amount != 0.0
        } then {
            it.sortedByDescending { ca -> ca.amount }
        }

        return catAmtList()
    }

    @Pure
    private suspend fun calculateTotalAmount(
        type: TransactionType,
        treatTransferAsIncExp: Boolean,

        @SideEffect
        incomeExpenseTransfer: suspend () -> IncomeExpenseTransferPair,
    ): BigDecimal {
        val incExpQuad = incomeExpenseTransfer()
        return when (type) {
            TransactionType.INCOME -> {
                incExpQuad.income +
                        if (treatTransferAsIncExp) {
                            incExpQuad.transferIncome
                        } else {
                            BigDecimal.ZERO
                        }
            }

            TransactionType.EXPENSE -> {
                incExpQuad.expense +
                        if (treatTransferAsIncExp) {
                            incExpQuad.transferExpense
                        } else {
                            BigDecimal.ZERO
                        }
            }

            else -> BigDecimal.ZERO
        }
    }

    @Pure
    private suspend fun addAccountTransfersCategory(
        showAccountTransfersCategory: Boolean,
        type: TransactionType,
        accountTransfersCategory: Category,
        accountIdFilterSet: Set<UUID>,

        @SideEffect
        transactions: suspend () -> List<Transaction>,

        @SideEffect
        incomeExpenseTransfer: suspend () -> IncomeExpenseTransferPair,

        @SideEffect
        kategoriAmounts: suspend () -> List<KategoriAmount>,
    ): List<KategoriAmount> {
        val incExpQuad = incomeExpenseTransfer()

        val catAmtList =
            if (!showAccountTransfersCategory || incExpQuad.transferIncome == BigDecimal.ZERO && incExpQuad.transferExpense == BigDecimal.ZERO) {
                kategoriAmounts then { it.sortedByDescending { ca -> ca.amount } }
            } else {
                val amt = if (type == TransactionType.INCOME) {
                    incExpQuad.transferIncome.toDouble()
                } else {
                    incExpQuad.transferExpense.toDouble()
                }

                val categoryTrans = transactions().filter {
                    it.type == TransactionType.TRANSFER
                }.filter {
                    if (type == TransactionType.EXPENSE) {
                        accountIdFilterSet.contains(it.accountId)
                    } else {
                        accountIdFilterSet.contains(it.toAccountId)
                    }
                }

                kategoriAmounts then {
                    it.plus(
                        KategoriAmount(
                            category = accountTransfersCategory,
                            amount = amt,
                            associatedTransactions = categoryTrans,
                            isCategoryUnspecified = true
                        )
                    )
                } then {
                    it.sortedByDescending { ca -> ca.amount }
                }
            }

        return catAmtList()
    }

    data class Input(
        val baseCurrency: String,
        val range: com.oneSaver.legacy.data.model.FromToTimeRange,
        val type: TransactionType,
        val accountIdFilterList: List<UUID>,
        val treatTransferAsIncExp: Boolean = false,
        val showAccountTransfersCategory: Boolean = treatTransferAsIncExp,
        val existingTransactions: List<Transaction> = emptyList(),
    )

    data class Output(val totalAmount: Double, val kategoriAmounts: ImmutableList<KategoriAmount>)
}
