package com.financeAndMoney.home.clientJourney

import com.financeAndMoney.base.legacy.SharedPrefs
import com.financeAndMoney.base.legacy.stringRes
import com.financeAndMoney.base.model.TransactionType
import com.financeAndMoney.data.database.dao.read.PlannedPaymentRuleDao
import com.financeAndMoney.data.repository.TransactionRepository
import com.financeAndMoney.design.l0_system.Gradient
import com.financeAndMoney.design.l0_system.Green
import com.financeAndMoney.design.l0_system.GreenLight
import com.financeAndMoney.design.l0_system.Ivy
import com.financeAndMoney.design.l0_system.Orange
import com.financeAndMoney.design.l0_system.Red
import com.financeAndMoney.design.l0_system.Red3
import com.financeAndMoney.legacy.MySaveCtx
import com.financeAndMoney.legacy.data.model.MainTab
import com.financeAndMoney.navigation.ModifyScheduledSkrin
import com.financeAndMoney.navigation.FinPieChartStatisticSkrin
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.widget.walleTransaction.AddTransactionWidgetCompact
import javax.inject.Inject

@Deprecated("Legacy code")
class ClientJourneyCardsProvider @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val sharedPrefs: SharedPrefs,
    private val ivyContext: MySaveCtx
) {

    suspend fun loadCards(): List<ClientJourneyCardModel> {
        val trnCount = transactionRepository.countHappenedTransactions().value
        val plannedPaymentsCount = plannedPaymentRuleDao.countPlannedPayments()

        return ACTIVE_CARDS
            .filter {
                it.condition(trnCount, plannedPaymentsCount, ivyContext) && !isCardDismissed(it)
            }
    }

    private fun isCardDismissed(cardData: ClientJourneyCardModel): Boolean {
        return sharedPrefs.getBoolean(sharedPrefsKey(cardData), false)
    }

    fun dismissCard(cardData: ClientJourneyCardModel) {
        sharedPrefs.putBoolean(sharedPrefsKey(cardData), true)
    }

    private fun sharedPrefsKey(cardData: ClientJourneyCardModel): String {
        return "${cardData.id}${SharedPrefs._CARD_DISMISSED}"
    }

    companion object {
        val ACTIVE_CARDS = listOf(
            adjustBalanceCard(),
            addPlannedPaymentCard(),
            didYouKnow_pinAddTransactionWidgetCard(),
            didYouKnow_expensesPieChart(),
            rateUsCard(),
            shareMySaveCard(),
            rateUsCard_2(),
        )

        fun adjustBalanceCard() = ClientJourneyCardModel(
            id = "adjust_balance",
            condition = { trnCount, _, _ ->
                trnCount == 0L
            },
            title = stringRes(R.string.adjust_initial_balance),
            description = stringRes(R.string.adjust_initial_balance_description),
            cta = stringRes(R.string.to_accounts),
            ctaIcon = R.drawable.ic_custom_account_s,
            background = Gradient.solid(Ivy),
            hasDismiss = false,
            onAction = { _, ivyContext, _ ->
                ivyContext.selectMainTab(MainTab.ACCOUNTS)
            }
        )

        fun addPlannedPaymentCard() = ClientJourneyCardModel(
            id = "add_planned_payment",
            condition = { trnCount, plannedPaymentCount, _ ->
                trnCount >= 1 && plannedPaymentCount == 0L
            },
            title = stringRes(R.string.create_first_planned_payment),
            description = stringRes(R.string.create_first_planned_payment_description),
            cta = stringRes(R.string.add_planned_payment),
            ctaIcon = R.drawable.ic_planned_payments,
            background = Gradient.solid(Orange),
            hasDismiss = true,
            onAction = { navigation, _, _ ->
                navigation.navigateTo(
                    ModifyScheduledSkrin(
                        type = TransactionType.EXPENSE,
                        plannedPaymentRuleId = null
                    )
                )
            }
        )

        fun didYouKnow_pinAddTransactionWidgetCard() = ClientJourneyCardModel(
            id = "add_transaction_widget",
            condition = { trnCount, _, _ ->
                trnCount >= 3
            },
            title = stringRes(R.string.did_you_know),
            description = stringRes(R.string.widget_description),
            cta = stringRes(R.string.add_widget),
            ctaIcon = R.drawable.ic_ms_custom_atom_s,
            background = Gradient.solid(GreenLight),
            hasDismiss = true,
            onAction = { _, _, ivyActivity ->
                ivyActivity.pinWidget(AddTransactionWidgetCompact::class.java)
            }
        )

        fun didYouKnow_expensesPieChart() = ClientJourneyCardModel(
            id = "expenses_pie_chart",
            condition = { trnCount, _, _ ->
                trnCount >= 7
            },
            title = stringRes(R.string.did_you_know),
            description = stringRes(R.string.you_can_see_a_piechart),
            cta = stringRes(R.string.expenses_piechart),
            ctaIcon = R.drawable.ic_ms_custom_bills_s,
            background = Gradient.solid(Red),
            hasDismiss = true,
            onAction = { navigation, _, _ ->
                navigation.navigateTo(FinPieChartStatisticSkrin(type = TransactionType.EXPENSE))
            }
        )

        fun rateUsCard() = ClientJourneyCardModel(
            id = "rate_us",
            condition = { trnCount, _, _ ->
                trnCount >= 10
            },
            title = stringRes(R.string.review_mysave_wallet),
            description = stringRes(R.string.review_mysave_wallet_description),
            cta = stringRes(R.string.rate_us_on_google_play),
            ctaIcon = R.drawable.ic_custom_star_s,
            background = Gradient.solid(Green),
            hasDismiss = true,
            onAction = { _, _, ivyActivity ->
                ivyActivity.reviewMySave(dismissReviewCard = true)
            }
        )

        fun shareMySaveCard() = ClientJourneyCardModel(
            id = "share_ivy_wallet",
            condition = { trnCount, _, _ ->
                trnCount >= 11
            },
            title = stringRes(R.string.share_mysave_wallet),
            description = stringRes(R.string.help_us_grow),
            cta = stringRes(R.string.share_with_friends),
            ctaIcon = R.drawable.ic_ms_custom_family_s,
            background = Gradient.solid(Red3),
            hasDismiss = true,
            onAction = { _, _, ivyActivity ->
                ivyActivity.shareMySave()
            }
        )

        fun rateUsCard_2() = ClientJourneyCardModel(
            id = "rate_us_2",
            condition = { trnCount, _, _ ->
                trnCount >= 22
            },
            title = stringRes(R.string.review_mysave_wallet),
            description = stringRes(R.string.make_mysave_wallet_better_description),
            cta = stringRes(R.string.rate_us_on_google_play),
            ctaIcon = R.drawable.ic_custom_star_s,
            background = Gradient.solid(GreenLight),
            hasDismiss = true,
            onAction = { _, _, mysaveActivity ->
                mysaveActivity.reviewMySave(dismissReviewCard = true)
            }
        )
    }
}
