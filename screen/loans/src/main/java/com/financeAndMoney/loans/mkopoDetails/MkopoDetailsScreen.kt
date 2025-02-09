package com.financeAndMoney.loans.mkopoDetails

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.base.model.LoanRecordType
import com.financeAndMoney.base.model.TransactionType
import com.financeAndMoney.base.model.processByType
import com.financeAndMoney.base.utils.MySaveAdsManager
import com.financeAndMoney.data.model.LoanType
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.legacy.MySavePreview
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.legacy.datamodel.Loan
import com.financeAndMoney.legacy.datamodel.LoanRecord
import com.financeAndMoney.legacy.humanReadableType
import com.financeAndMoney.legacy.ui.component.ItemStatisticToolbar
import com.financeAndMoney.legacy.ui.component.transaction.TypeAmountCurrency
import com.financeAndMoney.legacy.utils.clickableNoIndication
import com.financeAndMoney.legacy.utils.drawColoredShadow
import com.financeAndMoney.legacy.utils.format
import com.financeAndMoney.legacy.utils.formatNicely
import com.financeAndMoney.legacy.utils.formatNicelyWithTime
import com.financeAndMoney.legacy.utils.isNotNullOrBlank
import com.financeAndMoney.legacy.utils.rememberInteractionSource
import com.financeAndMoney.legacy.utils.setStatusBarDarkTextCompat
import com.financeAndMoney.loans.mkopo.data.DisplayMkopoRekodi
import com.financeAndMoney.loans.mkopoDetails.events.DeleteMkopoModalEvent
import com.financeAndMoney.loans.mkopoDetails.events.MkopoDetailsScreenEvent
import com.financeAndMoney.loans.mkopoDetails.events.MkopoModalEvent
import com.financeAndMoney.loans.mkopoDetails.events.MkopoRecordModalEvent
import com.financeAndMoney.navigation.MkopoDetailsSkrin
import com.financeAndMoney.navigation.TransactScrin
import com.financeAndMoney.navigation.navigation
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.legacy.domain.data.MysaveCurrency
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Gradient
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Gray
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.MediumBlack
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.MediumWhite
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.BalanceRow
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.ItemIconMDefaultIcon
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.MysaveButton
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.mysaveIcon
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.ProgressBar
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.getCustomIconIdS
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.dynamicContrast
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.findContrastTextColor
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.isDarkColor
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.DeleteModal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.MkopoModal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.LoanRecordModal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.ProgressModal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.toComposeColor
import com.financeAndMoney.loans.mkopo.FakeActivity
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun BoxWithConstraintsScope.LoanDetailsScreen(screen: MkopoDetailsSkrin, activity: Activity) {
    val viewModel: MkopoDetailsVM = viewModel()
    viewModel.screen = screen
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
    state: MkopoDetailsScreenState,
    onEventHandler: (MkopoDetailsScreenEvent) -> Unit = {},
) {
    val itemColor = state.loan?.color?.toComposeColor() ?: Gray
    val mySaveAdsManager = remember { MySaveAdsManager.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(itemColor)
    ) {
        val listState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 16.dp)
                .clip(UI.shapes.r1Top)
                .background(UI.colors.pure),
            state = listState,
        ) {
            item {
                if (state.loan != null) {
                    Header(
                        loan = state.loan,
                        baseCurrency = state.baseCurrency,
                        loanTotalAmount = state.loanTotalAmount,
                        amountPaid = state.amountPaid,
                        loanAmountPaid = state.loanAmountPaid,
                        itemColor = itemColor,
                        selectedLoanAccount = state.selectedLoanAccount,
                        onAmountClick = {
                            onEventHandler.invoke(MkopoDetailsScreenEvent.OnAmountClick)
                        },
                        onDeleteLoan = {
                            onEventHandler.invoke(
                                DeleteMkopoModalEvent.OnDismissDeleteMkopo(
                                    isDeleteModalVisible = true
                                )
                            )
                        },
                        onEditLoan = {
                            if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                                val adCallback = MySaveAdsManager.OnAdsCallback {
                                    onEventHandler.invoke(MkopoDetailsScreenEvent.OnEditMkopoClick)
                                }
                                mySaveAdsManager.displayAds(activity, adCallback)
                            }
                        },
                        onAddRecord = {
                            if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                                val adCallback = MySaveAdsManager.OnAdsCallback {
                                    onEventHandler.invoke(MkopoDetailsScreenEvent.OnAddRecord)
                                }
                                mySaveAdsManager.displayAds(activity, adCallback)
                            }
                        }
                    )
                }
            }

            item {
                // Rounded corners top effect
                Spacer(
                    Modifier
                        .height(32.dp)
                        .fillMaxWidth()
                        .background(itemColor) // itemColor is displayed below the clip
                        .background(UI.colors.pure, UI.shapes.r1Top)
                )
            }

            if (state.loan != null) {
                loanRecords(
                    loan = state.loan,
                    displayMkopoRekodis = state.displayMkopoRekodis,
                    onClick = { displayLoanRecord ->
                        if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                            val adCallback = MySaveAdsManager.OnAdsCallback {
                                onEventHandler.invoke(
                                    MkopoRecordModalEvent.OnClickMkopoRecord(
                                        displayLoanRecord
                                    )
                                )
                            }
                            mySaveAdsManager.displayAds(activity, adCallback)
                        }

                    }
                )
                item {
                    InitialRecordItem(
                        loan = state.loan,
                        amount = state.loan.amount,
                        baseCurrency = state.baseCurrency,
                    )
                }
            }

            if (state.displayMkopoRekodis.isEmpty()) {
                item {
                    NoLoanRecordsEmptyState()
                    Spacer(Modifier.height(96.dp))
                }
            }

            item {
                // scroll hack
                Spacer(Modifier.height(96.dp))
            }
        }
    }

    MkopoModal(modal = state.loanModalData, onCreateLoan = {
        // do nothing
    }, onEditLoan = { loan, createLoanTransaction ->
        onEventHandler.invoke(MkopoModalEvent.OnEditMkopoModal(loan, createLoanTransaction))
    }, dismiss = {
        onEventHandler.invoke(MkopoModalEvent.OnDismissMkopoModal)
    }, onCreateAccount = { createAccountData ->
        onEventHandler.invoke(MkopoDetailsScreenEvent.OnCreateAccount(createAccountData))
    }, accounts = state.accounts, onPerformCalculations = {
        onEventHandler.invoke(MkopoModalEvent.PerformCalculation)
    })

    LoanRecordModal(modal = state.loanRecordModalData, onCreate = {
        onEventHandler.invoke(MkopoRecordModalEvent.OnCreateMkopoRecord(it))
    }, onEdit = {
        onEventHandler.invoke(MkopoRecordModalEvent.OnEditMkopoRecord(it))
    }, onDelete = { loanRecord ->
        onEventHandler.invoke(MkopoRecordModalEvent.OnDeleteMkopoRecord(loanRecord))
    }, accounts = state.accounts, dismiss = {
        onEventHandler.invoke(MkopoRecordModalEvent.OnDismissMkopoRecord)
    }, onCreateAccount = { createAccountData ->
        onEventHandler.invoke(MkopoDetailsScreenEvent.OnCreateAccount(createAccountData))
    })

    DeleteModal(
        visible = state.isDeleteModalVisible,
        title = stringResource(R.string.deletion_confirmation),
        description = stringResource(R.string.loan_confirm_deletion_description),
        dismiss = {
            onEventHandler.invoke(DeleteMkopoModalEvent.OnDismissDeleteMkopo(isDeleteModalVisible = false))
        }
    ) {
        onEventHandler.invoke(DeleteMkopoModalEvent.OnDeleteMkopo)
    }

    ProgressModal(
        title = stringResource(R.string.confirm_account_change),
        description = stringResource(R.string.confirm_account_loan_change),
        visible = state.waitModalVisible
    )
}

@Composable
private fun Header(
    loan: Loan,
    baseCurrency: String,
    loanTotalAmount: Double,
    amountPaid: Double,
    itemColor: Color,
    onAmountClick: () -> Unit,
    onEditLoan: () -> Unit,
    onDeleteLoan: () -> Unit,
    loanAmountPaid: Double = 0.0,
    selectedLoanAccount: Account? = null,
    onAddRecord: () -> Unit
) {
    val contrastColor = findContrastTextColor(itemColor)

    val darkColor = isDarkColor(itemColor)
    setStatusBarDarkTextCompat(darkText = !darkColor)

    Column(
        modifier = Modifier.background(itemColor)
    ) {
        Spacer(Modifier.height(20.dp))

        ItemStatisticToolbar(
            contrastColor = contrastColor,
            onEdit = onEditLoan,
            onDelete = onDeleteLoan
        )

        Spacer(Modifier.height(24.dp))

        LoanItem(
            loan = loan,
            contrastColor = contrastColor,
        ) {
            onEditLoan()
        }

        BalanceRow(
            modifier = Modifier
                .padding(start = 32.dp)
                .testTag("loan_amount")
                .clickableNoIndication(rememberInteractionSource()) {
                    onAmountClick()
                },
            textColor = contrastColor,
            currency = baseCurrency,
            balance = loanTotalAmount,
        )

        Spacer(Modifier.height(20.dp))

        LoanInfoCard(
            loan = loan,
            baseCurrency = baseCurrency,
            amountPaid = amountPaid,
            loanAmountPaid = loanAmountPaid,
            loanTotalAmount = loanTotalAmount,
            selectedLoanAccount = selectedLoanAccount,
            onAddRecord = onAddRecord
        )

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun LoanItem(
    loan: Loan,
    contrastColor: Color,

    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(start = 22.dp)
            .clickableNoIndication(rememberInteractionSource()) {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemIconMDefaultIcon(
            iconName = loan.icon,
            defaultIcon = R.drawable.ic_ms_custom_loan_m,
            tint = contrastColor
        )

        Spacer(Modifier.width(8.dp))

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.testTag("loan_name"),
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

            loan.dateTime?.let {
                Text(
                    text = it.formatNicely(
                        noWeekDay = false
                    ).uppercase(),
                    style = UI.typo.nC.style(
                        color = contrastColor
                    )
                )
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun LoanInfoCard(
    loan: Loan,
    baseCurrency: String,
    loanTotalAmount: Double,
    amountPaid: Double,
    loanAmountPaid: Double = 0.0,
    selectedLoanAccount: Account? = null,

    onAddRecord: () -> Unit
) {
    val backgroundColor = if (isDarkColor(loan.color)) {
        MediumBlack.copy(alpha = 0.9f)
    } else {
        MediumWhite.copy(alpha = 0.9f)
    }

    val contrastColor = findContrastTextColor(backgroundColor)
    val percentPaid = amountPaid / loanTotalAmount
    val loanPercentPaid = loanAmountPaid / loanTotalAmount
    val leftToPay = loanTotalAmount - amountPaid
    val nav = navigation()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .drawColoredShadow(
                color = backgroundColor,
                alpha = 0.1f
            )
            .background(backgroundColor, UI.shapes.r2),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp, start = 24.dp),
                text = stringResource(R.string.paid),
                style = UI.typo.c.style(
                    color = contrastColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            if (selectedLoanAccount != null) {
                MysaveButton(
                    modifier = Modifier.padding(end = 16.dp, top = 12.dp),
                    backgroundGradient = Gradient.solid(loan.color.toComposeColor()),
                    hasGlow = false,
                    iconTint = contrastColor,
                    text = selectedLoanAccount.name,
                    iconStart = getCustomIconIdS(
                        iconName = selectedLoanAccount.icon,
                        defaultIcon = R.drawable.ic_custom_account_s
                    ),
                    textStyle = UI.typo.c.style(
                        color = contrastColor,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    padding = 8.dp,
                    iconEdgePadding = 10.dp
                ) {
                    nav.navigateTo(
                        TransactScrin(
                            accountId = selectedLoanAccount.id,
                            categoryId = null
                        )
                    )
                }
            }
        }

        // Support UI for Old Versions where
        if (selectedLoanAccount == null) {
            Spacer(Modifier.height(12.dp))
        }

        Text(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .testTag("amount_paid"),
            text = "${amountPaid.format(baseCurrency)} / ${loanTotalAmount.format(baseCurrency)}",
            style = UI.typo.nB1.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = MysaveCurrency.fromCode(baseCurrency)?.name ?: "",
            style = UI.typo.b2.style(
                color = contrastColor,
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.testTag("percent_paid"),
                text = "${percentPaid.times(100).format(2)}%",
                style = UI.typo.nB1.style(
                    color = contrastColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.width(8.dp))

            Text(
                modifier = Modifier.testTag("left_to_pay"),
                text = stringResource(
                    R.string.left_to_pay,
                    leftToPay.format(baseCurrency),
                    baseCurrency
                ),
                style = UI.typo.nB2.style(
                    color = Gray,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.height(8.dp))

        ProgressBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(horizontal = 24.dp),
            notFilledColor = UI.colors.pure,
            percent = percentPaid
        )

        if (loanAmountPaid != 0.0) {
            Divider(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth(),
                thickness = 1.dp,
                color = contrastColor
            )

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = stringResource(R.string.loan_interest),
                style = UI.typo.c.style(
                    color = contrastColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.testTag("loan_interest_percent_paid"),
                    text = "${loanPercentPaid.times(100).format(2)}%",
                    style = UI.typo.nB1.style(
                        color = contrastColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    modifier = Modifier.testTag("interest_paid"),
                    text = stringResource(
                        R.string.interest_paid,
                        loanAmountPaid.format(baseCurrency),
                        baseCurrency
                    ),
                    style = UI.typo.nB2.style(
                        color = Gray,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            ProgressBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(horizontal = 24.dp),
                notFilledColor = UI.colors.pure,
                percent = loanPercentPaid
            )
        }

        Spacer(Modifier.height(24.dp))

        MysaveButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = stringResource(R.string.add_record),
            shadowAlpha = 0.1f,
            backgroundGradient = Gradient.solid(contrastColor),
            textStyle = UI.typo.b2.style(
                color = findContrastTextColor(contrastColor),
                fontWeight = FontWeight.Bold
            ),
            wrapContentMode = false
        ) {
            onAddRecord()
        }

        Spacer(Modifier.height(12.dp))
    }
}

fun LazyListScope.loanRecords(
    loan: Loan,
    displayMkopoRekodis: List<DisplayMkopoRekodi> = emptyList(),
    onClick: (DisplayMkopoRekodi) -> Unit
) {
    items(items = displayMkopoRekodis) { displayLoanRecord ->
        LoanRecordItem(
            loan = loan,
            loanRecord = displayLoanRecord.loanRecord,
            baseCurrency = displayLoanRecord.loanRecordCurrencyCode,
            account = displayLoanRecord.account,
            loanBaseCurrency = displayLoanRecord.loanCurrencyCode
        ) {
            onClick(displayLoanRecord)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LoanRecordItem(
    loan: Loan,
    loanRecord: LoanRecord,
    baseCurrency: String,
    loanBaseCurrency: String = "",
    account: Account? = null,
    onClick: () -> Unit
) {
    val nav = navigation()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.r4)
            .clickable {
                onClick()
            }
            .background(UI.colors.medium, UI.shapes.r4)
            .testTag("loan_record_item")
    ) {
        if (account != null || loanRecord.interest) {
            Row(Modifier.padding(16.dp)) {
                if (account != null) {
                    MysaveButton(
                        backgroundGradient = Gradient.solid(UI.colors.pure),
                        hasGlow = false,
                        iconTint = UI.colors.pureInverse,
                        text = account.name,
                        iconStart = getCustomIconIdS(
                            iconName = account.icon,
                            defaultIcon = R.drawable.ic_custom_account_s
                        ),
                        textStyle = UI.typo.c.style(
                            color = UI.colors.pureInverse,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        padding = 8.dp,
                        iconEdgePadding = 10.dp
                    ) {
                        nav.navigateTo(
                            TransactScrin(
                                accountId = account.id,
                                categoryId = null
                            )
                        )
                    }
                }

                if (loanRecord.interest) {
                    // Spacer(modifier = Modifier.width(8.dp))

                    val textIconColor = if (isDarkColor(loan.color)) MediumWhite else MediumBlack

                    MysaveButton(
                        modifier = Modifier.padding(start = 8.dp),
                        backgroundGradient = Gradient.solid(loan.color.toComposeColor()),
                        hasGlow = false,
                        iconTint = textIconColor,
                        text = stringResource(R.string.interest),
                        iconStart = getCustomIconIdS(
                            iconName = "currency",
                            defaultIcon = R.drawable.ic_ms_currency
                        ),
                        textStyle = UI.typo.c.style(
                            color = textIconColor,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        padding = 8.dp,
                        iconEdgePadding = 10.dp
                    ) {
                        // do Nothing
                    }
                }
            }
        } else {
            Spacer(Modifier.height(20.dp))
        }

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = loanRecord.dateTime.formatNicelyWithTime(
                noWeekDay = false
            ).uppercase(),
            style = UI.typo.nC.style(
                color = Gray,
                fontWeight = FontWeight.Bold
            )
        )

        if (loanRecord.note.isNotNullOrBlank()) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                text = loanRecord.note!!,
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.pureInverse
                )
            )
        }

        if (loanRecord.note.isNullOrEmpty()) {
            Spacer(Modifier.height(16.dp))
        }
        val transactionType = when (loan.type) {
            LoanType.LEND -> {
                loanRecord.loanRecordType.processByType(
                    increaseAction = { TransactionType.EXPENSE },
                    decreaseAction = { TransactionType.INCOME }
                )
            }

            LoanType.BORROW -> {
                loanRecord.loanRecordType.processByType(
                    increaseAction = { TransactionType.INCOME },
                    decreaseAction = { TransactionType.EXPENSE }
                )
            }
        }
        TypeAmountCurrency(
            transactionType = transactionType,
            dueDate = null,
            currency = baseCurrency,
            amount = loanRecord.amount
        )

        if (loanRecord.convertedAmount != null) {
            Text(
                modifier = Modifier.padding(start = 68.dp),
                text = loanRecord.convertedAmount!!.format(baseCurrency) + " $loanBaseCurrency",
                style = UI.typo.nB2.style(
                    color = Gray,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun InitialRecordItem(
    loan: Loan,
    amount: Double,
    baseCurrency: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.r4)
            .background(UI.colors.medium, UI.shapes.r4)
            .testTag("loan_record_item")
    ) {
        MysaveButton(
            modifier = Modifier.padding(16.dp),
            backgroundGradient = Gradient.solid(UI.colors.pure),
            text = stringResource(id = R.string.initial_loan_record),
            iconTint = UI.colors.pureInverse,
            iconStart = getCustomIconIdS(
                iconName = loan.icon,
                defaultIcon = R.drawable.ic_ms_custom_loan_s
            ),
            textStyle = UI.typo.c.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.ExtraBold
            ),
            padding = 8.dp,
        ) {}

        loan.dateTime?.formatNicelyWithTime(
            noWeekDay = false
        )?.let { nicelyFormattedDate ->
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = nicelyFormattedDate.uppercase(),
                style = UI.typo.nC.style(
                    color = Gray,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TypeAmountCurrency(
            transactionType = if (loan.type == LoanType.LEND) TransactionType.EXPENSE else TransactionType.INCOME,
            dueDate = null,
            currency = baseCurrency,
            amount = amount
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun NoLoanRecordsEmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        mysaveIcon(
            icon = R.drawable.ic_notransactions,
            tint = Gray
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.no_records),
            style = UI.typo.b1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(R.string.no_records_for_the_loan),
            style = UI.typo.b2.style(
                color = Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview
@Composable
private fun Preview_Empty() {
    MySavePreview {
        UI(
            activity = FakeActivity(),
            MkopoDetailsScreenState(
                baseCurrency = "BGN",
                loan = Loan(
                    name = "Loan 1",
                    amount = 4023.54,
                    color = Red.toArgb(),
                    type = LoanType.LEND,
                    dateTime = LocalDateTime.now()
                ),
                displayMkopoRekodis = persistentListOf(),
                amountPaid = 3821.00,
                loanTotalAmount = 4023.54,
                loanAmountPaid = 100.0,
                accounts = persistentListOf(),
                selectedLoanAccount = null,
                createLoanTransaction = false,
                isDeleteModalVisible = false,
                loanModalData = null,
                loanRecordModalData = null,
                waitModalVisible = false
            )
        ) {}
    }
}

/** For Preview purpose **/
private val testDateTime = LocalDateTime.of(2023, 4, 27, 0, 35)

@Preview
@Composable
private fun Preview_Records(theme: Theme = Theme.LIGHT) {
    MySavePreview(theme) {
        UI(
            activity = FakeActivity(),
            MkopoDetailsScreenState(
                baseCurrency = "BGN",
                loan = Loan(
                    name = "Loan 1",
                    amount = 4023.54,
                    color = Red.toArgb(),
                    type = LoanType.LEND,
                    dateTime = testDateTime,
                ),
                displayMkopoRekodis = persistentListOf(
                    DisplayMkopoRekodi(
                        LoanRecord(
                            amount = 123.45,
                            dateTime = testDateTime.minusDays(1),
                            note = "Cash",
                            loanId = UUID.randomUUID(),
                            loanRecordType = LoanRecordType.INCREASE
                        )
                    ),
                    DisplayMkopoRekodi(
                        LoanRecord(
                            amount = 0.50,
                            dateTime = testDateTime.minusYears(1),
                            loanId = UUID.randomUUID(),
                            loanRecordType = LoanRecordType.DECREASE
                        )
                    ),
                    DisplayMkopoRekodi(
                        LoanRecord(
                            amount = 1000.00,
                            dateTime = testDateTime.minusMonths(1),
                            note = "Revolut",
                            loanId = UUID.randomUUID(),
                            loanRecordType = LoanRecordType.INCREASE
                        )
                    ),
                ),
                loanTotalAmount = 4023.54,
                amountPaid = 3821.00,
                loanAmountPaid = 100.0,
                accounts = persistentListOf(),
                selectedLoanAccount = null,
                createLoanTransaction = false,
                isDeleteModalVisible = false,
                loanModalData = null,
                loanRecordModalData = null,
                waitModalVisible = false
            )
        ) {}
    }
}

/** For screenshot testing */
@Composable
fun LoanDetailScreenUiTest(isDark: Boolean) {
    val theme = when (isDark) {
        true -> Theme.DARK
        false -> Theme.LIGHT
    }
    Preview_Records(theme)
}