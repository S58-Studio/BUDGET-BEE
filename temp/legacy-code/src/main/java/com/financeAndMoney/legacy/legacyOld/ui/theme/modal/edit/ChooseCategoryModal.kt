package com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.data.model.CategoryId
import com.financeAndMoney.data.model.primitive.ColorInt
import com.financeAndMoney.data.model.primitive.NotBlankTrimmedString
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.design.utils.thenIf
import com.financeAndMoney.legacy.MySavePreview
import com.financeAndMoney.legacy.utils.drawColoredShadow
import com.financeAndMoney.legacy.utils.hideKeyboard
import com.financeAndMoney.legacy.utils.onScreenStart
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Gradient
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Ivy
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Orange
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Red
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.ItemIconSDefaultIcon
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.MysaveBorderButton
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.MysaveCircleButton
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.WrapContentRow
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.findContrastTextColor
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.MysaveModal
import com.financeAndMoney.legacy.legacyOld.ui.theme.modal.ModalSkip
import com.financeAndMoney.legacy.legacyOld.ui.theme.modal.ModalTitle
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.toComposeColor
import java.util.UUID

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Suppress("ParameterNaming")
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ChooseCategoryModal(
    id: UUID = UUID.randomUUID(),
    visible: Boolean,
    initialCategory: Category?,
    categories: List<Category>,

    showCategoryModal: (Category?) -> Unit,
    onCategoryChanged: (Category?) -> Unit,
    dismiss: () -> Unit
) {
    var selectedCategory by remember(initialCategory) {
        mutableStateOf(initialCategory)
    }

    MysaveModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSkip {
                save(
                    category = selectedCategory,
                    onCategoryChanged = onCategoryChanged,
                    dismiss = dismiss
                )
            }
        }
    ) {
        val view = LocalView.current
        onScreenStart {
            hideKeyboard(view)
        }

        Spacer(Modifier.height(32.dp))

        ModalTitle(
            text = stringResource(R.string.choose_category)
        )

        Spacer(Modifier.height(24.dp))

        CategoryPicker(
            categories = categories,
            selectedCategory = selectedCategory,
            showCategoryModal = showCategoryModal,
            onEditCategory = {
                showCategoryModal(it)
            }
        ) {
            selectedCategory = it
            save(
                shouldDismissModal = it != null,
                category = it,
                onCategoryChanged = onCategoryChanged,
                dismiss = dismiss
            )
        }

        Spacer(Modifier.height(56.dp))
    }
}

private fun save(
    shouldDismissModal: Boolean = true,

    category: Category?,
    onCategoryChanged: (Category?) -> Unit,
    dismiss: () -> Unit
) {
    onCategoryChanged(category)
    if (shouldDismissModal) {
        dismiss()
    }
}

@ExperimentalFoundationApi
@Suppress("ParameterNaming")
@Composable
private fun CategoryPicker(
    categories: List<Category>,
    selectedCategory: Category?,
    showCategoryModal: (Category?) -> Unit,
    onEditCategory: (Category) -> Unit,
    onSelected: (Category?) -> Unit,
) {
    val data = mutableListOf<Any>()
    data.addAll(categories)
    data.add(AddNewCategory())

    WrapContentRow(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        horizontalMarginBetweenItems = 12.dp,
        verticalMarginBetweenRows = 12.dp,
        items = data
    ) {
        when (it) {
            is Category -> {
                CategoryButton(
                    category = it,
                    selected = it == selectedCategory,
                    onClick = {
                        onSelected(it)
                    },
                    onLongClick = {
                        onEditCategory(it)
                    },
                    onDeselect = {
                        onSelected(null)
                    }
                )
            }

            is AddNewCategory -> {
                AddNewButton {
                    showCategoryModal(null)
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun CategoryButton(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDeselect: () -> Unit,
) {
    val categoryColor = category.color.value.toComposeColor()

    val rFull = UI.shapes.rFull

    Row(
        modifier = Modifier
            .thenIf(selected) {
                drawColoredShadow(categoryColor)
            }
            .clip(UI.shapes.rFull)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .border(
                width = 2.dp,
                color = if (selected) UI.colors.pureInverse else UI.colors.medium,
                shape = UI.shapes.rFull
            )
            .thenIf(selected) {
                background(categoryColor, rFull)
            }
            .testTag("choose_category_button"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(if (selected) 12.dp else 8.dp))

        ItemIconSDefaultIcon(
            modifier = Modifier
                .background(categoryColor, CircleShape),
            iconName = category.icon?.id,
            defaultIcon = R.drawable.ic_custom_category_s,
            tint = findContrastTextColor(categoryColor)
        )

        Text(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .padding(
                    start = if (selected) 12.dp else 12.dp,
                    end = if (selected) 20.dp else 24.dp
                ),
            text = category.name.value,
            style = UI.typo.b2.style(
                color = if (selected) {
                    findContrastTextColor(categoryColor)
                } else {
                    UI.colors.pureInverse
                },
                fontWeight = FontWeight.SemiBold
            )
        )

        if (selected) {
            val deselectBtnBackground = findContrastTextColor(categoryColor)
            MysaveCircleButton(
                modifier = Modifier
                    .size(32.dp),
                icon = R.drawable.ic_remove,
                backgroundGradient = Gradient.solid(deselectBtnBackground),
                tint = findContrastTextColor(deselectBtnBackground)
            ) {
                onDeselect()
            }

            Spacer(Modifier.width(8.dp))
        }
    }
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun AddNewButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    MysaveBorderButton(
        modifier = modifier,
        text = stringResource(R.string.add_new),
        backgroundGradient = Gradient.solid(UI.colors.mediumInverse),
        iconStart = R.drawable.ic_plus,
        textStyle = UI.typo.b2.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.Bold
        ),
        iconTint = UI.colors.pureInverse,
        padding = 10.dp,
        onClick = onClick
    )
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
private class AddNewCategory

@ExperimentalFoundationApi
@Preview
@Composable
private fun PreviewChooseCategoryModal() {
    MySavePreview {
        val categories = mutableListOf(
            Category(
                name = NotBlankTrimmedString.unsafe("Test"),
                color = ColorInt(Ivy.toArgb()),
                icon = null,
                id = CategoryId(UUID.randomUUID()),
                orderNum = 0.0,
            ),
            Category(
                name = NotBlankTrimmedString.unsafe("Second"),
                color = ColorInt(Orange.toArgb()),
                icon = null,
                id = CategoryId(UUID.randomUUID()),
                orderNum = 0.0,
            ),
            Category(
                name = NotBlankTrimmedString.unsafe("Third"),
                color = ColorInt(Red.toArgb()),
                icon = null,
                id = CategoryId(UUID.randomUUID()),
                orderNum = 0.0,
            ),
        )

        ChooseCategoryModal(
            visible = true,
            initialCategory = categories.first(),
            categories = categories,
            showCategoryModal = { },
            onCategoryChanged = { }
        ) {
        }
    }
}
