package com.oneSaver.allStatus.userInterface.theme.modal.edit

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import arrow.core.raise.either
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.IconAsset
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.domains.legacy.ui.IvyColorPicker
import com.oneSaver.legacy.MySavePreview
import com.oneSaver.legacy.utils.hideKeyboard
import com.oneSaver.legacy.utils.isNotNullOrBlank
import com.oneSaver.legacy.utils.onScreenStart
import com.oneSaver.legacy.utils.selectEndTextFieldValue
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateCategoryData
import com.oneSaver.allStatus.userInterface.theme.Ivy
import com.oneSaver.allStatus.userInterface.theme.components.ItemIconMDefaultIcon
import com.oneSaver.allStatus.userInterface.theme.components.IvyNameTextField
import com.oneSaver.allStatus.userInterface.theme.dynamicContrast
import com.oneSaver.allStatus.userInterface.theme.modal.ChooseIconModal
import com.oneSaver.allStatus.userInterface.theme.modal.MysaveModal
import com.oneSaver.legacy.legacyOld.ui.theme.modal.ModalAddSave
import com.oneSaver.legacy.legacyOld.ui.theme.modal.ModalTitle
import java.util.UUID

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
data class CategoryModalData(
    val category: Category?,
    val id: UUID = UUID.randomUUID(),
    val autoFocusKeyboard: Boolean = true,
)

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun BoxWithConstraintsScope.CategoryModal(
    modal: CategoryModalData?,
    onCreateCategory: (CreateCategoryData) -> Unit,
    onEditCategory: (Category) -> Unit,
    dismiss: () -> Unit,
) {
    val initialCategory = modal?.category
    var nameTextFieldValue by remember(modal) {
        mutableStateOf(selectEndTextFieldValue(initialCategory?.name?.value))
    }
    var color by remember(modal) {
        mutableStateOf(initialCategory?.color?.let { Color(it.value) } ?: Ivy)
    }
    var icon by remember(modal) {
        mutableStateOf(initialCategory?.icon)
    }

    var chooseIconModalVisible by remember(modal) {
        mutableStateOf(false)
    }

    MysaveModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        PrimaryAction = {
            ModalAddSave(
                item = modal?.category,
                enabled = nameTextFieldValue.text.isNotNullOrBlank()
            ) {
                if (initialCategory != null) {
                    val updatedCategory = either {
                        initialCategory.copy(
                            name = NotBlankTrimmedString.from(nameTextFieldValue.text.trim()).bind(),
                            color = ColorInt(color.toArgb()),
                            icon = icon
                        )
                    }.getOrNull()

                    if (updatedCategory != null) {
                        onEditCategory(updatedCategory)
                    }
                } else {
                    onCreateCategory(
                        CreateCategoryData(
                            name = nameTextFieldValue.text.trim(),
                            color = color,
                            icon = icon?.id
                        )
                    )
                }

                dismiss()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(
            text = if (modal?.category != null) {
                stringResource(R.string.edit_category)
            } else {
                stringResource(
                    R.string.create_category
                )
            }
        )

        Spacer(Modifier.height(24.dp))

        IconNameRow(
            hint = stringResource(R.string.category_name),
            defaultIcon = R.drawable.ic_custom_category_m,
            color = color,
            icon = icon?.id,

            autoFocusKeyboard = modal?.autoFocusKeyboard ?: true,

            nameTextFieldValue = nameTextFieldValue,
            setNameTextFieldValue = { nameTextFieldValue = it },
            showChooseIconModal = {
                chooseIconModalVisible = true
            }
        )

        Spacer(Modifier.height(40.dp))

        IvyColorPicker(
            selectedColor = color,
            onColorSelected = { color = it }
        )

        Spacer(Modifier.height(48.dp))
    }

    ChooseIconModal(
        visible = chooseIconModalVisible,
        initialIcon = icon?.id ?: "category",
        color = color,
        dismiss = { chooseIconModalVisible = false }
    ) {
        icon = it?.let { iconId -> IconAsset.unsafe(iconId) }
    }
}

@Composable
fun IconNameRow(
    hint: String,
    @DrawableRes defaultIcon: Int,
    color: Color,
    icon: String?,

    autoFocusKeyboard: Boolean,

    nameTextFieldValue: TextFieldValue,
    setNameTextFieldValue: (TextFieldValue) -> Unit,

    showChooseIconModal: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val nameFocus = FocusRequester()

        onScreenStart {
            if (autoFocusKeyboard) {
                nameFocus.requestFocus()
            }
        }

        Spacer(Modifier.width(24.dp))

        ItemIconMDefaultIcon(
            modifier = Modifier
                .clip(CircleShape)
                .background(color, CircleShape)
                .clickable {
                    showChooseIconModal()
                }
                .testTag("modal_item_icon"),
            iconName = icon,
            tint = color.dynamicContrast(),
            defaultIcon = defaultIcon
        )

        val view = LocalView.current
        IvyNameTextField(
            modifier = Modifier
                .padding(start = 28.dp, end = 36.dp)
                .focusRequester(nameFocus),
            underlineModifier = Modifier.padding(start = 24.dp, end = 32.dp),
            value = nameTextFieldValue,
            hint = hint,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
                autoCorrect = true
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    hideKeyboard(view)
                }
            ),
        ) { newValue ->
            setNameTextFieldValue(newValue)
        }
    }
}

@Preview
@Composable
private fun PreviewCategoryModal() {
    MySavePreview {
        CategoryModal(
            modal = CategoryModalData(null),
            onCreateCategory = { },
            onEditCategory = { }
        ) {
        }
    }
}
