package com.financeAndMoney.categories

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.base.utils.MySaveAdsManager
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.data.model.CategoryId
import com.financeAndMoney.data.model.primitive.ColorInt
import com.financeAndMoney.data.model.primitive.IconAsset
import com.financeAndMoney.data.model.primitive.NotBlankTrimmedString
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.navigation.KategoriSkrin
import com.financeAndMoney.navigation.TransactScrin
import com.financeAndMoney.navigation.navigation
import com.financeAndMoney.navigation.screenScopedViewModel
import com.financeAndMoney.core.userInterface.R

import com.financeAndMoney.expenseAndBudgetPlanner.domain.data.SortOrder
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Gradient
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.GradientGreen
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Green
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.GreenDark
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.GreenLight
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.IvyDark
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Orange
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.White
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.BalanceRow
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.CircleButtonFilled
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.ItemIconSDefaultIcon
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.mysaveIcon
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.ReorderButton
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.ReorderModalSingleType
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.findContrastTextColor
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.MysaveModal
import com.financeAndMoney.legacy.legacyOld.ui.theme.modal.ModalSet
import com.financeAndMoney.legacy.legacyOld.ui.theme.modal.ModalTitle
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit.CategoryModal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit.CategoryModalData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.toComposeColor
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.wallet.AmountCurrencyB1
import com.financeAndMoney.userInterface.rememberScrollPositionListState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

@Composable
fun BoxWithConstraintsScope.CategoriesScreen(screen: KategoriSkrin, activity: Activity) {
    val viewModel: CategoriesViewModel = screenScopedViewModel()
    val state = viewModel.uiState()

    UI(
        activity = activity,
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    activity: Activity,
    state: KategoriSkriniState = KategoriSkriniState(),
    onEvent: (KategoriSkriniEventi) -> Unit = {}
) {
    val nav = navigation()
    val mySaveAdsManager = remember { MySaveAdsManager.getInstance() }
    val mysaveContext = com.financeAndMoney.legacy.mySaveCtx()
    var listState = rememberLazyListState()
    if (!state.categories.isEmpty()) {
        listState = rememberScrollPositionListState(
            key = "categories_lazy_column",
            initialFirstVisibleItemIndex = mysaveContext.categoriesListState?.firstVisibleItemIndex
                ?: 0,
            initialFirstVisibleItemScrollOffset = mysaveContext.categoriesListState?.firstVisibleItemScrollOffset
                ?: 0
        )
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        state = listState
    ) {
        item {
            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(24.dp))

                Text(
                    text = stringResource(R.string.categories),
                    style = UI.typo.h2.style(
                        color = UI.colors.pureInverse,
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(Modifier.weight(1f))

                CircleButtonFilled(
                    icon = R.drawable.ic_sort_by_alpha_24,
                    onClick = {
                        onEvent(KategoriSkriniEventi.OnSortOrderModalVisible(visible = true))
                    },
                    clickAreaPadding = 12.dp
                )

                Spacer(modifier = Modifier.width(16.dp))

                ReorderButton {
                    onEvent(KategoriSkriniEventi.OnReorderModalVisible(true))
                }

                Spacer(Modifier.width(24.dp))
            }

            Spacer(Modifier.height(16.dp))
        }

        items(state.categories, key = { it.category.id.value }) { categoryData ->
            Spacer(Modifier.height(16.dp))
            CategoryCard(
                currency = state.baseCurrency,
                kategoriData = categoryData,
                onLongClick = {
                    onEvent(KategoriSkriniEventi.OnReorderModalVisible(true))
                }
            ) {
                if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                    val adCallback = MySaveAdsManager.OnAdsCallback {
                        nav.navigateTo(
                            TransactScrin(
                                accountId = null,
                                categoryId = categoryData.category.id.value
                            )
                        )
                    }
                    mySaveAdsManager.displayAds(activity, adCallback)
                }

            }
        }

        item {
            Spacer(Modifier.height(150.dp)) // scroll hack
        }
    }
    CategoriesBottomBar(
        onAddCategory = {
            if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                val adCallback = MySaveAdsManager.OnAdsCallback {
                    onEvent(
                        KategoriSkriniEventi.OnCategoryModalVisible(
                            CategoryModalData(category = null)
                        )
                    )
                }
                mySaveAdsManager.displayAds(activity, adCallback)

            }
        },
        onClose = {
            if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                val adCallback = MySaveAdsManager.OnAdsCallback {
                    nav.back()
                }
                mySaveAdsManager.displayAds(activity, adCallback)

            }
        },
    )

    ReorderModalSingleType(
        visible = state.reorderModalVisible,
        initialItems = state.categories,
        dismiss = {
            onEvent(KategoriSkriniEventi.OnReorderModalVisible(false))
        },
        onReordered = {
            onEvent(KategoriSkriniEventi.OnReorder(it))
        }
    ) { _, item ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp)
                .padding(vertical = 8.dp),
            text = item.category.name.value,
            style = UI.typo.b1.style(
                color = item.category.color.value.toComposeColor(),
                fontWeight = FontWeight.Bold
            )
        )
    }

    CategoryModal(
        modal = state.categoryModalData,
        onCreateCategory = {
            onEvent(KategoriSkriniEventi.OnCreateCategory(it))
        },
        onEditCategory = { },
        dismiss = {
            onEvent(KategoriSkriniEventi.OnCategoryModalVisible(null))
        }
    )

    SortModal(
        initialType = state.sortOrder,
        items = state.sortOrderItems,
        visible = state.sortModalVisible,
        dismiss = {
            onEvent(KategoriSkriniEventi.OnSortOrderModalVisible(visible = false))
        },
        onSortOrderChange = {
            onEvent(KategoriSkriniEventi.OnReorder(state.categories, it))
        }
    )
}

@Composable
private fun CategoryCard(
    currency: String,
    kategoriData: KategoriData,
    onLongClick: () -> Unit,
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
        CategoryHeader(
            kategoriData = kategoriData,
            currency = currency,
            contrastColor = findContrastTextColor(kategoriData.category.color.value.toComposeColor())
        )

        Spacer(Modifier.height(12.dp))

        // Emitting content
        AddedSpent(
            currency = currency,
            monthlyIncome = kategoriData.monthlyIncome,
            monthlyExpenses = kategoriData.monthlyExpenses
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun AddedSpent(
    monthlyIncome: Double,
    monthlyExpenses: Double,
    currency: String,
    modifier: Modifier = Modifier,
    textColor: Color = UI.colors.pureInverse,
    dividerColor: Color = UI.colors.medium,
    center: Boolean = true,
    dividerSpacer: Dp? = null,

    ) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (center) {
            Spacer(Modifier.weight(1f))
        }

        LabelAmount(
            textColor = textColor,
            label = stringResource(R.string.expenses_monthly),
            amount = monthlyExpenses,
            currency = currency,
            center = center
        )

        if (center) {
            Spacer(Modifier.weight(1f))
        }

        if (dividerSpacer != null) {
            Spacer(modifier = Modifier.width(dividerSpacer))
        }

        // Divider
        Spacer(
            modifier = Modifier
                .width(2.dp)
                .height(48.dp)
                .background(dividerColor, UI.shapes.rFull)
        )

        if (center) {
            Spacer(Modifier.weight(1f))
        }

        if (dividerSpacer != null) {
            Spacer(modifier = Modifier.width(dividerSpacer))
        }

        LabelAmount(
            textColor = textColor,
            label = stringResource(R.string.income_monthly),
            amount = monthlyIncome,
            currency = currency,
            center = center
        )

        if (center) {
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun LabelAmount(
    label: String,
    amount: Double,
    currency: String,
    textColor: Color,
    center: Boolean
) {
    Column(
        horizontalAlignment = if (center) Alignment.CenterHorizontally else Alignment.Start
    ) {
        Text(
            text = label,
            style = UI.typo.c.style(
                color = textColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AmountCurrencyB1(
                textColor = textColor,
                amount = amount,
                currency = currency
            )
        }
    }
}

@Composable
private fun CategoryHeader(
    kategoriData: KategoriData,
    currency: String,
    contrastColor: Color,
) {
    val category = kategoriData.category

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(category.color.value.toComposeColor(), UI.shapes.r4Top)
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(20.dp))

            ItemIconSDefaultIcon(
                iconName = category.icon?.id,
                defaultIcon = R.drawable.ic_custom_category_s,
                tint = contrastColor
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = category.name.value,
                style = UI.typo.b1.style(
                    color = contrastColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.height(4.dp))

        BalanceRow(
            modifier = Modifier.align(Alignment.CenterHorizontally),

            textColor = contrastColor,
            currency = currency,
            balance = kategoriData.monthlyBalance,

            balanceFontSize = 30.sp,
            currencyFontSize = 30.sp,

            currencyUpfront = false,
            balanceAmountPrefix = com.financeAndMoney.legacy.utils.balancePrefix(
                income = kategoriData.monthlyIncome,
                expenses = kategoriData.monthlyExpenses
            )
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Suppress("UnusedParameter")
@Composable
fun BoxWithConstraintsScope.SortModal(
    items: ImmutableList<SortOrder>,
    visible: Boolean,
    initialType: SortOrder,
    dismiss: () -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.sort_by),
    id: UUID = UUID.randomUUID()
) {
    var sortOrder by remember(initialType) {
        mutableStateOf(initialType)
    }

    val applyChange = {
        onSortOrderChange(sortOrder)
        dismiss()
    }

    MysaveModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSet {
                applyChange()
            }
        },
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(text = title)

        Spacer(Modifier.height(32.dp))

        items.forEach {
            SelectTypeButton(
                text = it.displayName,
                icon = when (it) {
                    SortOrder.DEFAULT -> R.drawable.ic_custom_star_s
                    SortOrder.BALANCE_AMOUNT -> R.drawable.ic_mysave_money_coins
                    SortOrder.EXPENSES -> R.drawable.ic_expense
                    SortOrder.ALPHABETICAL -> R.drawable.ic_sort_by_alpha_24
                },
                selected = it == sortOrder
            ) {
                sortOrder = it
                applyChange()
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SelectTypeButton(
    text: String,
    @DrawableRes icon: Int,
    selected: Boolean,
    selectedGradient: Gradient = GradientGreen,
    textSelectedColor: Color = White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(64.dp)
            .clip(UI.shapes.r4)
            .background(
                brush = if (selected) selectedGradient.asHorizontalBrush() else SolidColor(UI.colors.medium),
                shape = UI.shapes.r4
            )
            .clickable {
                onClick()
            }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        val textColor = if (selected) textSelectedColor else UI.colors.pureInverse

        mysaveIcon(
            icon = icon,
            tint = textColor,
            modifier = Modifier.fillMaxHeight()
        )

        Spacer(Modifier.width(12.dp))

        Text(
            modifier = Modifier.wrapContentHeight(),
            text = text,
            style = UI.typo.b1.style(
                color = textColor
            ),
            textAlign = TextAlign.Center,
        )

        if (selected) {
            Spacer(Modifier.weight(1f))

            mysaveIcon(
                icon = R.drawable.ic_check,
                tint = textSelectedColor
            )

            Text(
                text = stringResource(R.string.selected_text),
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.SemiBold,
                    color = textSelectedColor
                )
            )

            Spacer(Modifier.width(24.dp))
        }
    }
}

@Preview
@Composable
private fun Preview(theme: Theme = Theme.LIGHT) {
    com.financeAndMoney.legacy.MySavePreview(theme) {
        val state = KategoriSkriniState(
            baseCurrency = "BGN",
            categories = persistentListOf(
                KategoriData(
                    category = Category(
                        id = CategoryId(UUID.randomUUID()),
                        name = NotBlankTrimmedString.unsafe("Groceries"),
                        color = ColorInt(Green.toArgb()),
                        icon = IconAsset.unsafe("groceries"),
                        orderNum = 0.0,
                    ),
                    monthlyBalance = 2125.0,
                    monthlyExpenses = 920.0,
                    monthlyIncome = 3045.0
                ),
                KategoriData(
                    category = Category(
                        id = CategoryId(UUID.randomUUID()),
                        name = NotBlankTrimmedString.unsafe("Fun"),
                        color = ColorInt(Orange.toArgb()),
                        icon = IconAsset.unsafe("game"),
                        orderNum = 0.0,
                    ),
                    monthlyBalance = 1200.0,
                    monthlyExpenses = 750.0,
                    monthlyIncome = 0.0
                ),
                KategoriData(
                    category = Category(
                        id = CategoryId(UUID.randomUUID()),
                        name = NotBlankTrimmedString.unsafe("Ivy"),
                        color = ColorInt(IvyDark.toArgb()),
                        icon = IconAsset.unsafe("star"),
                        orderNum = 0.0,
                    ),
                    monthlyBalance = 1200.0,
                    monthlyExpenses = 0.0,
                    monthlyIncome = 5000.0
                ),
                KategoriData(
                    category = Category(
                        id = CategoryId(UUID.randomUUID()),
                        name = NotBlankTrimmedString.unsafe("Food"),
                        color = ColorInt(GreenLight.toArgb()),
                        icon = IconAsset.unsafe("atom"),
                        orderNum = 0.0,
                    ),
                    monthlyBalance = 12125.21,
                    monthlyExpenses = 1350.50,
                    monthlyIncome = 8000.48
                ),
                KategoriData(
                    category = Category(
                        id = CategoryId(UUID.randomUUID()),
                        name = NotBlankTrimmedString.unsafe("Shisha"),
                        color = ColorInt(GreenDark.toArgb()),
                        icon = IconAsset.unsafe("drink"),
                        orderNum = 0.0,
                    ),
                    monthlyBalance = 820.0,
                    monthlyExpenses = 340.0,
                    monthlyIncome = 400.0
                ),

                )
        )
        UI(
            activity = FakeActivity(),
            state = state)
    }
}

/** For screenshot testing */
@Composable
fun CategoriesScreenUiTest(isDark: Boolean) {
    val theme = when (isDark) {
        true -> Theme.DARK
        false -> Theme.LIGHT
    }
    Preview(theme)
}
class FakeActivity: Activity()