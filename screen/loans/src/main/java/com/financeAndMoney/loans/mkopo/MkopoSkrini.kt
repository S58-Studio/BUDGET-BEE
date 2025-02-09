package com.financeAndMoney.loans.mkopo

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.base.utils.MySaveAdsManager
import com.financeAndMoney.data.model.LoanType
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.legacy.MySavePreview
import com.financeAndMoney.legacy.datamodel.Loan
import com.financeAndMoney.legacy.humanReadableType
import com.financeAndMoney.legacy.mySaveCtx
import com.financeAndMoney.legacy.utils.getDefaultFIATCurrency
import com.financeAndMoney.loans.mkopo.data.DisplayMkopoo
import com.financeAndMoney.navigation.MkopoDetailsSkrin
import com.financeAndMoney.navigation.LoanScreen
import com.financeAndMoney.navigation.navigation
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Blue
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Gray
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Orange
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Red
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.BalanceRow
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.ItemIconSDefaultIcon
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.mysaveIcon
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.ProgressBar
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.ReorderButton
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.ReorderModalSingleType
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.dynamicContrast
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.findContrastTextColor
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.MkopoModal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.LoanModalData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.toComposeColor
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDateTime

@Composable
fun BoxWithConstraintsScope.MkopoSkrini(screen: LoanScreen, activity: Activity) {
    val viewModel: MkopoVM = viewModel()
    val state = viewModel.uiState()
    UI(
        activity = activity,
        state = state,
        onEventHandler = viewModel::onEvent
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    activity: Activity,
    state: MkopoSkrinState,
    onEventHandler: (MkopoScriniEventi) -> Unit = {},
) {
    val nav = navigation()
    val mySaveAdsManager = remember { MySaveAdsManager.getInstance() }
    val scrollState = mySaveCtx().loansScrollState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(scrollState),
    ) {
        Spacer(Modifier.height(32.dp))

        Toolbar(
            setReorderModalVisible = {
                onEventHandler.invoke(MkopoScriniEventi.OnReOrderModalShow(show = it))
            },
            state.totalOweAmount,
            state.totalOwedAmount
        )

        Spacer(Modifier.height(8.dp))

        for (item in state.loans) {
            Spacer(Modifier.height(16.dp))

            MkopoItem(
                displayMkopoo = item
            ) {
                if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                    val adCallback = MySaveAdsManager.OnAdsCallback {
                        nav.navigateTo(
                            screen = MkopoDetailsSkrin(
                                loanId = item.loan.id
                            )
                        )
                    }
                    mySaveAdsManager.displayAds(activity, adCallback)
                }
            }
        }

        if (state.loans.isEmpty()) {
            Spacer(Modifier.weight(1f))

            NoLoansEmptyState(
                emptyStateTitle = stringResource(R.string.no_loans),
                emptyStateText = stringResource(R.string.no_loans_description)
            )

            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(150.dp)) // scroll hack
    }

    MkopoBottomBar(
        isPaidOffLoanVisible = state.paidOffLoanVisibility,
        onAdd = {
            onEventHandler.invoke(MkopoScriniEventi.OnAddLoan)
        },
        onTogglePaidOffLoanVisibility = {
            onEventHandler.invoke(MkopoScriniEventi.OnTogglePaidOffLoanVisibility)
        },
        onClose = {
            nav.back()
        },
    )

    ReorderModalSingleType(
        visible = state.reorderModalVisible,
        initialItems = state.loans,
        dismiss = {
            onEventHandler.invoke(MkopoScriniEventi.OnReOrderModalShow(show = false))
        },
        onReordered = {
            onEventHandler.invoke(MkopoScriniEventi.OnReordered(reorderedList = it))
        }
    ) { _, item ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp)
                .padding(vertical = 8.dp),
            text = item.loan.name,
            style = UI.typo.b1.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.Bold
            )
        )
    }

    MkopoModal(
        accounts = state.accounts,
        onCreateAccount = {
            onEventHandler.invoke(MkopoScriniEventi.OnCreateAccount(accountData = it))
        },
        modal = state.loanModalData,
        onCreateLoan = {
            onEventHandler.invoke(MkopoScriniEventi.OnLoanCreate(createLoanData = it))
        },
        onEditLoan = { _, _ -> },
        dismiss = {
            onEventHandler.invoke(MkopoScriniEventi.OnLoanModalDismiss)
        },
    )

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Toolbar(
    setReorderModalVisible: (Boolean) -> Unit,
    totalOweAmount: String,
    totalOwedAmount: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp, end = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.loans),
                style = UI.typo.h2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            if (totalOweAmount.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.you_owe, totalOweAmount),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            if (totalOwedAmount.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.you_are_owed, totalOwedAmount),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        ReorderButton {
            setReorderModalVisible(true)
        }

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
private fun MkopoItem(
    displayMkopoo: DisplayMkopoo,
    onClick: () -> Unit
) {
    val loan = displayMkopoo.loan
    val contrastColor = findContrastTextColor(loan.color.toComposeColor())

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .border(2.dp, UI.colors.medium, UI.shapes.r4)
            .testTag("loan_item")
            .clickable(
                onClick = onClick
            )
    ) {
        MkopoHeader(
            displayMkopoo = displayMkopoo,
            contrastColor = contrastColor,
        )

        Spacer(Modifier.height(12.dp))

        MkopoInfo(
            displayMkopoo = displayMkopoo
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun MkopoHeader(
    displayMkopoo: DisplayMkopoo,
    contrastColor: Color,
) {
    val loan = displayMkopoo.loan

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(loan.color.toComposeColor(), UI.shapes.r4Top)
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(20.dp))

            ItemIconSDefaultIcon(
                iconName = loan.icon,
                defaultIcon = R.drawable.ic_ms_custom_loan_s,
                tint = contrastColor
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = loan.name,
                style = UI.typo.b1.style(
                    color = contrastColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Spacer(Modifier.width(8.dp))

            Text(
                text = loan.humanReadableType(),
                style = UI.typo.c.style(
                    color = loan.color.toComposeColor().dynamicContrast()
                )
            )
        }

        Spacer(Modifier.height(4.dp))

        val leftToPay = displayMkopoo.loanTotalAmount - displayMkopoo.amountPaid
        BalanceRow(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            textColor = contrastColor,
            currency = displayMkopoo.currencyCode ?: getDefaultFIATCurrency().currencyCode,
            balance = leftToPay,

            balanceFontSize = 30.sp,
            currencyFontSize = 30.sp,

            currencyUpfront = false
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ColumnScope.MkopoInfo(
    displayMkopoo: DisplayMkopoo
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        text = displayMkopoo.formattedDisplayText,
        style = UI.typo.nB2.style(
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    )

    Spacer(Modifier.height(12.dp))

    ProgressBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .padding(horizontal = 24.dp),
        notFilledColor = UI.colors.medium,
        percent = displayMkopoo.percentPaid
    )
}

@Composable
private fun NoLoansEmptyState(
    emptyStateTitle: String,
    emptyStateText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        mysaveIcon(
            icon = R.drawable.ic_ms_custom_loan_l,
            tint = Gray
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = emptyStateTitle,
            style = UI.typo.b1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = emptyStateText,
            style = UI.typo.b2.style(
                color = Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(96.dp))
    }
}

/** For Preview purpose **/
private val testDateTime = LocalDateTime.of(2023, 4, 20, 0, 35)

@Preview
@Composable
private fun Preview(theme: Theme = Theme.LIGHT) {
    val state = MkopoSkrinState(
        baseCurrency = "BGN",
        loans = persistentListOf(
            DisplayMkopoo(
                loan = Loan(
                    name = "Loan 1",
                    icon = "rocket",
                    color = Red.toArgb(),
                    amount = 5000.0,
                    type = LoanType.BORROW,
                    dateTime = testDateTime
                ),
                loanTotalAmount = 5500.0,
                amountPaid = 0.0,
                percentPaid = 0.4
            ),
            DisplayMkopoo(
                loan = Loan(
                    name = "Loan 2",
                    icon = "atom",
                    color = Orange.toArgb(),
                    amount = 252.36,
                    type = LoanType.BORROW,
                    dateTime = testDateTime
                ),
                loanTotalAmount = 252.36,
                amountPaid = 124.23,
                percentPaid = 0.2
            ),
            DisplayMkopoo(
                loan = Loan(
                    name = "Loan 3",
                    icon = "bank",
                    color = Blue.toArgb(),
                    amount = 7000.0,
                    type = LoanType.LEND,
                    dateTime = testDateTime
                ),
                loanTotalAmount = 7000.0,
                amountPaid = 8000.0,
                percentPaid = 0.8
            ),
        ),
        accounts = persistentListOf(),
        totalOweAmount = "1000.00 INR",
        totalOwedAmount = "1500.0 INR",
        loanModalData = LoanModalData(
            loan = Loan(
                name = "",
                color = Blue.toArgb(),
                amount = 0.0,
                type = LoanType.LEND,
                dateTime = testDateTime
            ),
            baseCurrency = "INR"
        ),
        reorderModalVisible = false,
        selectedAccount = null,
        paidOffLoanVisibility = true
    )
    MySavePreview(theme) {
        UI(
            activity = FakeActivity(),
            state = state
        ) {}
    }
}

/** For screenshot testing */
@Composable
fun LoanScreenUiTest(isDark: Boolean) {
    val theme = when (isDark) {
        true -> Theme.DARK
        false -> Theme.LIGHT
    }
    Preview(theme)
}
class FakeActivity: Activity()