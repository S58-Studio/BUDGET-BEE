package com.oneSaver.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oneSaver.base.legacy.Theme
import com.oneSaver.data.model.AccountId
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.IconAsset
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.MySavePreview
import com.oneSaver.legacy.data.model.AccountData
import com.oneSaver.legacy.utils.clickableNoIndication
import com.oneSaver.legacy.utils.horizontalSwipeListener
import com.oneSaver.legacy.utils.rememberInteractionSource
import com.oneSaver.legacy.utils.rememberSwipeListenerState
import com.oneSaver.navigation.AkauntiTabSkrin
import com.oneSaver.navigation.TransactScrin
import com.oneSaver.navigation.navigation
import com.oneSaver.navigation.screenScopedViewModel
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.Green
import com.oneSaver.allStatus.userInterface.theme.GreenLight
import com.oneSaver.allStatus.userInterface.theme.components.BalanceRow
import com.oneSaver.allStatus.userInterface.theme.components.BalanceRowMini
import com.oneSaver.allStatus.userInterface.theme.components.ItemIconSDefaultIcon
import com.oneSaver.allStatus.userInterface.theme.components.ReorderButton
import com.oneSaver.allStatus.userInterface.theme.components.ReorderModalSingleType
import com.oneSaver.allStatus.userInterface.theme.dynamicContrast
import com.oneSaver.allStatus.userInterface.theme.findContrastTextColor
import com.oneSaver.allStatus.userInterface.theme.toComposeColor
import kotlinx.collections.immutable.persistentListOf
import com.oneSaver.userInterface.rememberScrollPositionListState
import java.util.UUID

@Composable
fun BoxWithConstraintsScope.AccountsTab(
    screen: AkauntiTabSkrin
) {
    val viewModel: AccountsVM = screenScopedViewModel()
    val uiState = viewModel.uiState()

    UI(
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    state: ACState,
    onEvent: (ACEventss) -> Unit = {}
) {
    val nav = navigation()
    val ivyContext = com.oneSaver.legacy.mySaveCtx()
    var listState = rememberLazyListState()
    if (!state.accountsData.isEmpty()) {
        listState = rememberScrollPositionListState(
            key = "accounts_lazy_column",
            initialFirstVisibleItemIndex = ivyContext.accountsListState?.firstVisibleItemIndex ?: 0,
            initialFirstVisibleItemScrollOffset = ivyContext.accountsListState?.firstVisibleItemScrollOffset
                ?: 0
        )
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .horizontalSwipeListener(
                sensitivity = 200,
                state = rememberSwipeListenerState(),
                onSwipeLeft = {
                    ivyContext.selectMainTab(com.oneSaver.legacy.data.model.MainTab.HOME)
                },
                onSwipeRight = {
                    ivyContext.selectMainTab(com.oneSaver.legacy.data.model.MainTab.HOME)
                }
            ),
        state = listState
    ) {
        item {
            Spacer(Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(24.dp))

                Column {
                    Text(
                        text = stringResource(R.string.all_accounts),
                        style = UI.typo.b1.style(
                            color = UI.colors.pureInverse,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }

                Spacer(Modifier.weight(1f))

                ReorderButton {
                    onEvent(
                        ACEventss.OnReorderModalVisible(reorderVisible = true)
                    )
                }

                Spacer(Modifier.width(24.dp))
            }
            Column {
                Spacer(Modifier.height(16.dp))
                IncomeExpensesRow(
                    currency = state.baseCurrency,
                    incomeLabel = stringResource(id = R.string.total_balance),
                    income = state.totalBalanceWithoutExcluded.toDoubleOrNull() ?: 0.00,
                    expensesLabel = stringResource(id = R.string.total_balance_excluded),
                    expenses = state.totalBalanceWithExcluded.toDoubleOrNull() ?: 0.00
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        items(state.accountsData) {
            Spacer(Modifier.height(16.dp))
            AccountCard(
                baseCurrency = state.baseCurrency,
                accountData = it,
                onBalanceClick = {
                    nav.navigateTo(
                        TransactScrin(
                            accountId = it.account.id.value,
                            categoryId = null
                        )
                    )
                }
            ) {
                nav.navigateTo(
                    TransactScrin(
                        accountId = it.account.id.value,
                        categoryId = null
                    )
                )
            }
        }

        item {
            Spacer(Modifier.height(150.dp)) // scroll hack
        }
    }

    ReorderModalSingleType(
        visible = state.reorderVisible,
        initialItems = state.accountsData,
        dismiss = {
            onEvent(ACEventss.OnReorderModalVisible(reorderVisible = false))
        },
        onReordered = {
            onEvent(ACEventss.OnReorder(reorderedList = it))
        }
    ) { _, item ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp)
                .padding(vertical = 8.dp),
            text = item.account.name.value,
            style = UI.typo.b1.style(
                color = item.account.color.value.toComposeColor(),
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun AccountCard(
    baseCurrency: String,
    accountData: AccountData,
    onBalanceClick: () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .border(2.dp, UI.colors.medium, UI.shapes.r4)
            .clickable(
                onClick = onClick
            )
    ) {
        val account = accountData.account
        val contrastColor = findContrastTextColor(account.color.value.toComposeColor())
        val currency = account.asset.code

        AccountHeader(
            accountData = accountData,
            currency = currency,
            baseCurrency = baseCurrency,
            contrastColor = contrastColor,

            onBalanceClick = onBalanceClick
        )

        Spacer(Modifier.height(12.dp))

        IncomeExpensesRow(
            currency = currency,
            incomeLabel = stringResource(R.string.income_monthly),
            income = accountData.monthlyIncome,
            expensesLabel = stringResource(R.string.expenses_monthly),
            expenses = accountData.monthlyExpenses
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun AccountHeader(
    accountData: AccountData,
    currency: String,
    baseCurrency: String,
    contrastColor: Color,
    onBalanceClick: () -> Unit
) {
    val account = accountData.account

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(account.color.value.toComposeColor(), UI.shapes.r4Top)
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(20.dp))

            ItemIconSDefaultIcon(
                iconName = account.icon?.id,
                defaultIcon = R.drawable.ic_custom_account_s,
                tint = contrastColor
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = account.name.value,
                style = UI.typo.b1.style(
                    color = contrastColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            if (!account.includeInBalance) {
                Spacer(Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.excluded),
                    style = UI.typo.c.style(
                        color = account.color.value.toComposeColor().dynamicContrast()
                    )
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        BalanceRow(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickableNoIndication(rememberInteractionSource()) {
                    onBalanceClick()
                },
            textColor = contrastColor,
            currency = currency,
            balance = accountData.balance,

            balanceFontSize = 30.sp,
            currencyFontSize = 30.sp,

            currencyUpfront = false
        )

        if (currency != baseCurrency && accountData.balanceBaseCurrency != null) {
            BalanceRowMini(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickableNoIndication(rememberInteractionSource()) {
                        onBalanceClick()
                    }
                    .testTag("baseCurrencyEquivalent"),
                textColor = account.color.value.toComposeColor().dynamicContrast(),
                currency = baseCurrency,
                balance = accountData.balanceBaseCurrency!!,
                currencyUpfront = false
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Preview
@Composable
private fun PreviewAccountsTab(theme: Theme = Theme.LIGHT) {
    MySavePreview(theme = theme) {
        val acc1 = com.oneSaver.data.model.Account(
            id = AccountId(UUID.randomUUID()),
            name = NotBlankTrimmedString.unsafe("Phyre"),
            color = ColorInt(Green.toArgb()),
            asset = AssetCode.unsafe("USD"),
            icon = null,
            includeInBalance = true,
            orderNum = 0.0,
        )

        val acc2 = com.oneSaver.data.model.Account(
            id = AccountId(UUID.randomUUID()),
            name = NotBlankTrimmedString.unsafe("DSK"),
            color = ColorInt(GreenLight.toArgb()),
            asset = AssetCode.unsafe("USD"),
            icon = null,
            includeInBalance = true,
            orderNum = 0.0,
        )

        val acc3 = com.oneSaver.data.model.Account(
            id = AccountId(UUID.randomUUID()),
            name = NotBlankTrimmedString.unsafe("M-Pesa"),
            color = ColorInt(Green.toArgb()),
            asset = AssetCode.unsafe("USD"),
            icon = IconAsset.unsafe("mpesa"),
            includeInBalance = true,
            orderNum = 0.0,
        )

        val acc4 = com.oneSaver.data.model.Account(
            id = AccountId(UUID.randomUUID()),
            name = NotBlankTrimmedString.unsafe("Cash"),
            color = ColorInt(Green.toArgb()),
            asset = AssetCode.unsafe("USD"),
            icon = IconAsset.unsafe("cash"),
            includeInBalance = true,
            orderNum = 0.0,
        )
        val state = ACState(
            baseCurrency = "BGN",
            accountsData = persistentListOf(
                AccountData(
                    account = acc1,
                    balance = 2125.0,
                    balanceBaseCurrency = null,
                    monthlyExpenses = 920.0,
                    monthlyIncome = 3045.0
                ),
                AccountData(
                    account = acc2,
                    balance = 12125.21,
                    balanceBaseCurrency = null,
                    monthlyExpenses = 1350.50,
                    monthlyIncome = 8000.48
                ),
                AccountData(
                    account = acc3,
                    balance = 2400.0,
                    balanceBaseCurrency = 1979.64,
                    monthlyExpenses = 8890.0,
                    monthlyIncome = 11000.30
                ),
                AccountData(
                    account = acc4,
                    balance = 820.0,
                    balanceBaseCurrency = null,
                    monthlyExpenses = 340.0,
                    monthlyIncome = 400.0
                ),
            ),
            totalBalanceWithExcluded = "25.54",
            totalBalanceWithExcludedText = "BGN 25.54",
            totalBalanceWithoutExcluded = "25.54",
            totalBalanceWithoutExcludedText = "BGN 25.54",
            reorderVisible = false
        )
        UI(state = state)
    }
}

/** For screen shot testing **/
@Composable
fun AccountsTabUITest(dark: Boolean) {
    val theme = when (dark) {
        true -> Theme.DARK
        false -> Theme.LIGHT
    }
    PreviewAccountsTab(theme)
}