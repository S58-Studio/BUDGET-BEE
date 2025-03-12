package com.oneSaver.piechart

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oneSaver.base.model.TransactionType
import com.oneSaver.base.utils.MySaveAdsManager
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.CategoryId
import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.IconAsset
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.design.utils.thenIf
import com.oneSaver.legacy.utils.drawColoredShadow
import com.oneSaver.legacy.utils.format
import com.oneSaver.legacy.utils.horizontalSwipeListener
import com.oneSaver.legacy.utils.rememberSwipeListenerState
import com.oneSaver.navigation.ModifyTransactionSkrin
import com.oneSaver.navigation.FinPieChartStatisticSkrin
import com.oneSaver.navigation.TransactionsScreen
import com.oneSaver.navigation.navigation
import com.oneSaver.navigation.screenScopedViewModel
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.GradientGreen
import com.oneSaver.allStatus.userInterface.theme.Gray
import com.oneSaver.allStatus.userInterface.theme.Green
import com.oneSaver.allStatus.userInterface.theme.IvyDark
import com.oneSaver.allStatus.userInterface.theme.IvyLight
import com.oneSaver.allStatus.userInterface.theme.Orange
import com.oneSaver.allStatus.userInterface.theme.Red
import com.oneSaver.allStatus.userInterface.theme.RedLight
import com.oneSaver.allStatus.userInterface.theme.White
import com.oneSaver.allStatus.userInterface.theme.components.BalanceRow
import com.oneSaver.allStatus.userInterface.theme.components.BalanceRowMini
import com.oneSaver.legacy.legacyOld.ui.theme.components.CircleButtonFilledGradient
import com.oneSaver.legacy.legacyOld.ui.theme.components.CloseButton
import com.oneSaver.legacy.utils.lerp
import com.oneSaver.allStatus.userInterface.theme.components.ItemIconM
import com.oneSaver.allStatus.userInterface.theme.components.ItemIconMDefaultIcon
import com.oneSaver.allStatus.userInterface.theme.components.MysaveOutlinedButton
import com.oneSaver.allStatus.userInterface.theme.findContrastTextColor
import com.oneSaver.allStatus.userInterface.theme.gradientExpenses
import com.oneSaver.allStatus.userInterface.theme.modal.ChoosePeriodModal
import com.oneSaver.allStatus.userInterface.theme.pureBlur
import com.oneSaver.allStatus.userInterface.theme.toComposeColor
import com.oneSaver.allStatus.userInterface.theme.wallet.AmountCurrencyB1Row
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.FinPieChartStatisticSkrin(
    screen: FinPieChartStatisticSkrin,
    activity: Activity
) {
    val viewModel: FinPieChartStatisticVM = screenScopedViewModel()
    val uiState = viewModel.uiState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(FinPieChartStatisticEventi.OnStart(screen))
    }

    UI(
        state = uiState,
        onEvent = viewModel::onEvent,
        activity = activity
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    state: FinPieChartStatisticState,
    activity: Activity,
    onEvent: (FinPieChartStatisticEventi) -> Unit = {}
) {
    val nav = navigation()
    val lazyState = rememberLazyListState()
    val expanded = lazyState.firstVisibleItemIndex < 1
    val percentExpanded by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = com.oneSaver.legacy.utils.springBounce(),
        label = "percent expanded"
    )
    val mySaveAdsManager = remember { MySaveAdsManager.getInstance() } //am not sure here,comeback later

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        state = lazyState
    ) {
        stickyHeader {
            Header(
                transactionType = state.transactionType,
                period = state.period,
                percentExpanded = percentExpanded,
                currency = state.baseCurrency,
                amount = state.totalAmount,
                onShowMonthModal = {
                    onEvent(FinPieChartStatisticEventi.OnShowMonthModal(state.period))
                },
                onSelectNextMonth = {
                    onEvent(FinPieChartStatisticEventi.OnSelectNextMonth)
                },
                onSelectPreviousMonth = {
                    onEvent(FinPieChartStatisticEventi.OnSelectPreviousMonth)
                },
                showCloseButtonOnly = state.showCloseButtonOnly,
                onClose = {
                    if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                        val adCallback = MySaveAdsManager.OnAdsCallback {
                            nav.back()
                        }
                        mySaveAdsManager.displayAds(activity, adCallback)
                    }
                },
                onAdd = { trnType ->
                    if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                        val adCallback = MySaveAdsManager.OnAdsCallback {
                            nav.navigateTo(
                                ModifyTransactionSkrin(
                                    initialTransactionId = null,
                                    type = trnType
                                )
                            )
                        }
                        mySaveAdsManager.displayAds(activity, adCallback)
                    }
                }
            )
        }

        item {
            Spacer(Modifier.height(20.dp))

            Text(
                modifier = Modifier
                    .padding(start = 32.dp)
                    .testTag("piechart_title"),
                text = if (state.transactionType == TransactionType.EXPENSE) {
                    stringResource(R.string.expenses)
                } else {
                    stringResource(R.string.income)
                },
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            BalanceRow(
                modifier = Modifier
                    .padding(start = 32.dp, end = 16.dp)
                    .testTag("piechart_total_amount")
                    .alpha(percentExpanded),
                currency = state.baseCurrency,
                balance = state.totalAmount,
                currencyUpfront = false,
                currencyFontSize = 30.sp
            )
        }

        item {
            Spacer(Modifier.height(40.dp))

            FinPieCharting(
                type = state.transactionType,
                kategoriAmounts = state.kategoriAmounts,
                selectedKategori = state.selectedKategori,
                onCategoryClick = { clickedCategory ->
                    onEvent(FinPieChartStatisticEventi.OnCategoryClicked(clickedCategory))
                }
            )

            Spacer(Modifier.height(48.dp))
        }

        itemsIndexed(
            items = state.kategoriAmounts
        ) { index, item ->
            if (item.amount != 0.0) {
                if (index != 0) {
                    Spacer(Modifier.height(16.dp))
                }

                CategoryAmountCard(
                    kategoriAmount = item,
                    currency = state.baseCurrency,
                    totalAmount = state.totalAmount,
                    selectedKategori = state.selectedKategori
                ) {
                    if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                        val adCallback = MySaveAdsManager.OnAdsCallback {
                            nav.navigateTo(
                                TransactionsScreen(
                                    categoryId = item.category?.id?.value,
                                    unspecifiedCategory = item.isCategoryUnspecified,
                                    accountIdFilterList = state.accountIdFilterList,
                                    transactions = item.associatedTransactions
                                )
                            )
                        }
                        mySaveAdsManager.displayAds(activity, adCallback)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(160.dp)) // scroll hack
        }
    }

    ChoosePeriodModal(
        modal = state.choosePeriodModal,
        dismiss = {
            onEvent(FinPieChartStatisticEventi.OnShowMonthModal(null))
        }
    ) {
        onEvent(FinPieChartStatisticEventi.OnSetPeriod(it))
    }
}

@Composable
private fun Header(
    transactionType: TransactionType,
    period: com.oneSaver.legacy.data.model.TimePeriod,
    percentExpanded: Float,

    currency: String,
    amount: Double,

    onShowMonthModal: () -> Unit,
    onSelectNextMonth: () -> Unit,
    onSelectPreviousMonth: () -> Unit,

    onClose: () -> Unit,
    onAdd: (TransactionType) -> Unit,
    showCloseButtonOnly: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(pureBlur())
            .statusBarsPadding()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        CloseButton {
            onClose()
        }

        // Balance mini row
        if (percentExpanded < 1f) {
            Spacer(Modifier.width(12.dp))

            BalanceRowMini(
                modifier = Modifier
                    .alpha(1f - percentExpanded),
                currency = currency,
                balance = amount,
            )
        }

        if (!showCloseButtonOnly) {
            Spacer(Modifier.weight(1f))

            MysaveOutlinedButton(
                modifier = Modifier.horizontalSwipeListener(
                    sensitivity = 75,
                    state = rememberSwipeListenerState(),
                    onSwipeLeft = {
                        onSelectNextMonth()
                    },
                    onSwipeRight = {
                        onSelectPreviousMonth()
                    }
                ),
                iconStart = R.drawable.ic_calendar,
                text = period.toDisplayShort(com.oneSaver.legacy.mySaveCtx().startDayOfMonth),
            ) {
                onShowMonthModal()
            }

            if (percentExpanded > 0f) {
                Spacer(Modifier.width(12.dp))

                val backgroundGradient = if (transactionType == TransactionType.EXPENSE) {
                    gradientExpenses()
                } else {
                    GradientGreen
                }
                CircleButtonFilledGradient(
                    modifier = Modifier
                        .thenIf(percentExpanded == 1f) {
                            drawColoredShadow(backgroundGradient.startColor)
                        }
                        .alpha(percentExpanded)
                        .size(lerp(1, 40, percentExpanded).dp),
                    iconPadding = 4.dp,
                    icon = R.drawable.ic_plus,
                    backgroundGradient = backgroundGradient,
                    tint = if (transactionType == TransactionType.EXPENSE) {
                        UI.colors.pure
                    } else {
                        White
                    }
                ) {
                    onAdd(transactionType)
                }
            }

            Spacer(Modifier.width(20.dp))
        }
    }
}

@Composable
private fun CategoryAmountCard(
    kategoriAmount: KategoriAmount,
    currency: String,
    totalAmount: Double,

    selectedKategori: SelectedKategori?,

    onClick: () -> Unit
) {
    val category = kategoriAmount.category
    val amount = kategoriAmount.amount

    val categoryColor =
        category?.color?.value?.toComposeColor() ?: Gray // Unspecified category = Gray
    val selectedState = when {
        selectedKategori == null -> {
            // no selectedKategori
            false
        }

        kategoriAmount.category == selectedKategori.category -> {
            // selectedKategori && we're selected
            true
        }

        else -> false
    }
    val backgroundColor = if (selectedState) categoryColor else UI.colors.medium

    val textColor = findContrastTextColor(
        backgroundColor = backgroundColor
    )

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .thenIf(selectedState) {
                drawColoredShadow(backgroundColor)
            }
            .clip(UI.shapes.r3)
            .background(backgroundColor, UI.shapes.r3)
            .clickable {
                onClick()
            }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        ItemIconM(
            modifier = Modifier.background(categoryColor, CircleShape),
            iconName = category?.icon?.id,
            tint = findContrastTextColor(categoryColor),
            iconContentScale = ContentScale.None,
            Default = {
                ItemIconMDefaultIcon(
                    modifier = Modifier.background(categoryColor, CircleShape),
                    iconName = category?.icon?.id,
                    defaultIcon = R.drawable.ic_custom_category_m,
                    tint = findContrastTextColor(categoryColor)
                )
            }
        )

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                    text = category?.name?.value ?: stringResource(R.string.unspecified),
                    style = UI.typo.b2.style(
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    )
                )

                PercentText(
                    amount = amount,
                    totalAmount = totalAmount,
                    selectedState = selectedState,
                    contrastColor = textColor
                )

                Spacer(Modifier.width(24.dp))
            }

            Spacer(Modifier.height(4.dp))

            AmountCurrencyB1Row(
                amount = amount,
                currency = currency,
                textColor = textColor,
                amountFontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun PercentText(
    amount: Double,
    totalAmount: Double,
    selectedState: Boolean,
    contrastColor: Color
) {
    Text(
        text = if (totalAmount != 0.0) {
            stringResource(R.string.percent, ((amount / totalAmount) * 100).format(2))
        } else {
            stringResource(R.string.percent, "0")
        },
        style = UI.typo.nB2.style(
            color = if (selectedState) contrastColor else UI.colors.pureInverse,
            fontWeight = FontWeight.Normal
        )
    )
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_Expense() {
    com.oneSaver.legacy.MySavePreview {
        val state = FinPieChartStatisticState(
            transactionType = TransactionType.EXPENSE,
            period = com.oneSaver.legacy.data.model.TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), // preview
            baseCurrency = "BGN",
            totalAmount = 1828.0,
            kategoriAmounts = persistentListOf(
                KategoriAmount(
                    category = Category(
                        name = NotBlankTrimmedString.unsafe("Bills"),
                        color = ColorInt(Green.toArgb()),
                        icon = IconAsset.unsafe("bills"),
                        id = CategoryId(UUID.randomUUID()),
                        orderNum = 0.0,
                    ),
                    amount = 791.0
                ),
                KategoriAmount(
                    category = null,
                    amount = 497.0,
                    isCategoryUnspecified = true
                ),
                KategoriAmount(
                    category = Category(
                        name = NotBlankTrimmedString.unsafe("Shisha"),
                        color = ColorInt(Orange.toArgb()),
                        icon = IconAsset.unsafe("trees"),
                        id = CategoryId(UUID.randomUUID()),
                        orderNum = 0.0,
                    ),
                    amount = 411.93
                ),
                KategoriAmount(
                    category = Category(
                        name = NotBlankTrimmedString.unsafe("Food & Drink"),
                        color = ColorInt(IvyDark.toArgb()),
                        icon = null,
                        id = CategoryId(UUID.randomUUID()),
                        orderNum = 0.0,
                    ),
                    amount = 260.03
                ),
                KategoriAmount(
                    category = Category(
                        name = NotBlankTrimmedString.unsafe("Gifts"),
                        color = ColorInt(RedLight.toArgb()),
                        icon = null,
                        id = CategoryId(UUID.randomUUID()),
                        orderNum = 0.0,
                    ),
                    amount = 160.0
                ),
                KategoriAmount(
                    category = Category(
                        name = NotBlankTrimmedString.unsafe("Clothes & Jewelery Fancy"),
                        color = ColorInt(Red.toArgb()),
                        icon = null,
                        id = CategoryId(UUID.randomUUID()),
                        orderNum = 0.0,
                    ),
                    amount = 2.0
                ),
                KategoriAmount(
                    category = Category(
                        name = NotBlankTrimmedString.unsafe("Finances, Burocracy & Governance"),
                        color = ColorInt(IvyLight.toArgb()),
                        icon = IconAsset.unsafe("work"),
                        id = CategoryId(UUID.randomUUID()),
                        orderNum = 0.0,
                    ),
                    amount = 2.0
                ),
            ),
            selectedKategori = null,
            accountIdFilterList = persistentListOf(),
            choosePeriodModal = null,
            filterExcluded = false,
            showCloseButtonOnly = false,
            transactions = persistentListOf()
        )

        UI(state = state, activity = FakeActivity())
    }
}
class FakeActivity : Activity()
@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_Income() {
    com.oneSaver.legacy.MySavePreview {
        val state = FinPieChartStatisticState(
            transactionType = TransactionType.INCOME,
            period = com.oneSaver.legacy.data.model.TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), // preview
            baseCurrency = "BGN",
            totalAmount = 1828.0,
            kategoriAmounts = persistentListOf(
                KategoriAmount(
                    category = Category(
                        name = NotBlankTrimmedString.unsafe("Bills"),
                        color = ColorInt(Green.toArgb()),
                        icon = IconAsset.unsafe("bills"),
                        id = CategoryId(UUID.randomUUID()),
                        orderNum = 0.0,
                    ),
                    amount = 791.0
                ),
                KategoriAmount(
                    category = null,
                    amount = 497.0,
                    isCategoryUnspecified = true
                ),
                KategoriAmount(
                    category = Category(
                        name = NotBlankTrimmedString.unsafe("Shisha"),
                        color = ColorInt(Orange.toArgb()),
                        icon = IconAsset.unsafe("trees"),
                        id = CategoryId(UUID.randomUUID()),
                        orderNum = 0.0,
                    ),
                    amount = 411.93
                ),
                KategoriAmount(
                    category = Category(
                        name = NotBlankTrimmedString.unsafe("Food & Drink"),
                        color = ColorInt(IvyDark.toArgb()),
                        icon = null,
                        id = CategoryId(UUID.randomUUID()),
                        orderNum = 0.0,
                    ),
                    amount = 260.03
                ),
                KategoriAmount(
                    category = Category(
                        name = NotBlankTrimmedString.unsafe("Gifts"),
                        color = ColorInt(RedLight.toArgb()),
                        icon = null,
                        id = CategoryId(UUID.randomUUID()),
                        orderNum = 0.0,
                    ),
                    amount = 160.0
                ),
                KategoriAmount(
                    category = Category(
                        name = NotBlankTrimmedString.unsafe("Clothes & Jewelery Fancy"),
                        color = ColorInt(Red.toArgb()),
                        icon = null,
                        id = CategoryId(UUID.randomUUID()),
                        orderNum = 0.0,
                    ),
                    amount = 2.0
                ),
                KategoriAmount(
                    category = Category(
                        name = NotBlankTrimmedString.unsafe("Finances, Burocracy & Governance"),
                        color = ColorInt(IvyLight.toArgb()),
                        icon = IconAsset.unsafe("work"),
                        id = CategoryId(UUID.randomUUID()),
                        orderNum = 0.0,
                    ),
                    amount = 2.0
                ),
            ),
            selectedKategori = null,
            accountIdFilterList = persistentListOf(),
            choosePeriodModal = null,
            filterExcluded = false,
            showCloseButtonOnly = false,
            transactions = persistentListOf()
        )

        UI(state = state, activity = FakeActivity())
    }
}
