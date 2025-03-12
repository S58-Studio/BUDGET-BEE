package com.oneSaver.accounts.accountTabs

import android.app.Activity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oneSaver.base.legacy.stringRes
import com.oneSaver.base.utils.MySaveAdsManager
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.frp.forward
import com.oneSaver.frp.then2
import com.oneSaver.home.HomeEvent
import com.oneSaver.home.HomeState
import com.oneSaver.legacy.utils.clickableNoIndication
import com.oneSaver.legacy.utils.rememberInteractionSource
import com.oneSaver.navigation.AkauntiTabSkrin
import com.oneSaver.navigation.BudgetScreen
import com.oneSaver.navigation.KategoriSkrin
import com.oneSaver.navigation.LoanScreen
import com.oneSaver.navigation.navigation
import com.oneSaver.navigation.screenScopedViewModel
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.components.BufferBattery
import com.oneSaver.legacy.legacyOld.ui.theme.modal.BufferModalData
import com.oneSaver.allStatus.userInterface.theme.wallet.AmountCurrencyB1
import com.oneSaver.home.HomeVM
import com.oneSaver.navigation.ScheduledPaymntsSkrin
import com.oneSaver.navigation.SeekSkrin
import com.oneSaver.allStatus.userInterface.theme.Gray
import com.oneSaver.allStatus.userInterface.theme.MediumBlack
import com.oneSaver.allStatus.userInterface.theme.MediumWhite
import com.oneSaver.allStatus.userInterface.theme.components.mysaveIcon
import com.oneSaver.allStatus.userInterface.theme.findContrastTextColor
import com.oneSaver.allStatus.userInterface.theme.isDarkColor
import com.oneSaver.legacy.legacyOld.ui.theme.modal.BufferModal
import com.oneSaver.userInterface.rememberScrollPositionListState


@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.AllAccountTabPage(activity: Activity) {
    val viewModel: HomeVM = screenScopedViewModel()
    val uiState = viewModel.uiState()

    AllAccountScreenUI(
        uiState,
        viewModel::onEvent,
        activity = activity)
}

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.AllAccountScreenUI(
    uiState: HomeState,
    onEvent: (HomeEvent) -> Unit,
    activity: Activity
) {
    val nav = navigation()
    val context = LocalContext.current
    val baseCurrency = uiState.baseData.baseCurrency
    var bufferModalData: BufferModalData? by remember { mutableStateOf(null) }
    val listState = rememberScrollPositionListState(key = "reportStatements")
    val mySaveAdsManager = remember { MySaveAdsManager.getInstance() }

    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            state = listState
        ) {

            item {
                Text(
                    modifier = Modifier.padding(
                        start = 32.dp
                    ),
                    text = stringResource(R.string.all_accounts),
                    style = UI.typo.h2.style(
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(Modifier.height(24.dp))

                SearchButton {
                    if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                        val adCallback = MySaveAdsManager.OnAdsCallback {
                            nav.navigateTo(
                                screen = SeekSkrin
                            )
                        }
                        mySaveAdsManager.displayAds(activity, adCallback)
                    }
                }

                Spacer(Modifier.height(24.dp))

                BufferLeft(
                    balance = uiState.balance.toDouble(),
                    currency = baseCurrency,
                    buffer = uiState.buffer.amount.toDouble(),
                    onBufferClick = {
                        if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                            val adCallback = MySaveAdsManager.OnAdsCallback {
                                bufferModalData = BufferModalData(
                                    balance = uiState.balance.toDouble(),
                                    currency = baseCurrency,
                                    buffer = uiState.buffer.amount.toDouble()
                                )
                            }
                            mySaveAdsManager.displayAds(activity, adCallback)
                        }
                    }
                )

                Spacer(Modifier.height(24.dp))

                AllAccountsCards(
                    hasAddButtons = false,
                    itemColor = UI.colors.pure,
                    accountsHeaderCardClicked = {
                        if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                            val adCallback = MySaveAdsManager.OnAdsCallback {
                                nav.navigateTo(AkauntiTabSkrin)
                            }
                            mySaveAdsManager.displayAds(activity, adCallback)
                        }
                    },
                    categoriesHeaderCardClicked = {
                        if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                            val adCallback = MySaveAdsManager.OnAdsCallback {
                                nav.navigateTo(KategoriSkrin)
                            }
                            mySaveAdsManager.displayAds(activity, adCallback)

                        }
                    }
                )
                Spacer(Modifier.height(34.dp))

                AllAccountsCards2(
                    hasAddButtons = false,
                    itemColor = UI.colors.pure,
                    loansHeaderCardClicked = {
                        if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                            val adCallback = MySaveAdsManager.OnAdsCallback {
                                nav.navigateTo(LoanScreen)
                            }
                            mySaveAdsManager.displayAds(activity, adCallback)
                        }
                    },
                    budgetsHeaderCardClicked = {
                        if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                            val adCallback = MySaveAdsManager.OnAdsCallback {
                                nav.navigateTo(BudgetScreen)
                            }
                            mySaveAdsManager.displayAds(activity, adCallback)

                        }
                    }
                )
                Spacer(Modifier.height(34.dp))
                PlannedPaymentsCard(
                    onClick = {
                        if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                            val adCallback = MySaveAdsManager.OnAdsCallback {
                                nav.navigateTo(ScheduledPaymntsSkrin)
                            }
                            mySaveAdsManager.displayAds(activity, adCallback)
                        } },
                    itemColor = UI.colors.pure,
                )
            }

        }
    }
    BufferModal(
        modal = bufferModalData,
        dismiss = {
            bufferModalData = null
        },
        onBufferChanged = forward<Double>() then2 {
            HomeEvent.SetBuffer(it)
        } then2 onEvent
    )
}

@Composable
private fun BufferLeft(
    buffer: Double,
    currency: String,
    balance: Double,
    onBufferClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableNoIndication(rememberInteractionSource()) {
                onBufferClick()
            }
            .testTag("savings_goal_row"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        Text(
            text = stringResource(R.string.savings_goal),
            style = UI.typo.b1.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.weight(1f))

        AmountCurrencyB1(
            amount = buffer,
            currency = currency,
            amountFontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.width(32.dp))
    }

    Spacer(Modifier.height(12.dp))

    BufferBattery(
        modifier = Modifier.padding(horizontal = 16.dp),
        buffer = buffer,
        currency = currency,
        balance = balance,
    ) {
        onBufferClick()
    }
}

@Composable
private fun PlannedPaymentsCard(
    onClick: () -> Unit,
    itemColor: Color
) {
    val backgroundColor = if (isDarkColor(itemColor)) {
        MediumBlack.copy(alpha = 0.9f)
    } else {
        MediumWhite.copy(alpha = 0.9f)
    }

    val contrastColor = findContrastTextColor(backgroundColor)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() }
            .background(Color.White, shape = RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                stringRes(R.string.planned_payments),
                style = UI.typo.c.style(
                    color = contrastColor,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                //text = "These are payments that are recurrent or funds due to be paid.",
                stringRes(R.string.no_planned_payments_description),
                style = UI.typo.c.style(
                    color = contrastColor,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

//searching transfers
@Composable
private fun SearchButton(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.rFull)
            .background(UI.colors.pure)
            .border(1.dp, Gray, UI.shapes.rFull)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        mysaveIcon(icon = R.drawable.ic_search)

        Spacer(Modifier.width(12.dp))

        Text(
            modifier = Modifier.padding(
                vertical = 12.dp,
            ),
            text = stringResource(R.string.search_transactions),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.SemiBold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.width(16.dp))
    }
}
