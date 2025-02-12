package com.oneSaver.legacy.legacyOld.ui.theme.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.MySaveComponentPreview
import com.oneSaver.legacy.utils.hideKeyboard
import com.oneSaver.legacy.utils.isNotNullOrBlank
import com.oneSaver.legacy.utils.selectEndTextFieldValue

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Suppress("ParameterNaming")
@Composable
fun MysaveBasicTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    textColor: Color = UI.colors.pureInverse,
    hint: String?,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        autoCorrect = true,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done,
        capitalization = KeyboardCapitalization.Sentences
    ),
    keyboardActions: KeyboardActions? = null,
    onValueChanged: (TextFieldValue) -> Unit
) {
    val isEmpty = value.text.isBlank()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        if (isEmpty && hint.isNotNullOrBlank()) {
            Text(
                modifier = Modifier,
                text = hint!!,
                style = UI.typo.b2.style(
                    color = UI.colors.gray,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start
                ),
            )
        }

        val view = LocalView.current
        BasicTextField(
            modifier = Modifier
                .testTag("base_input"),
            value = value,
            onValueChange = onValueChanged,
            textStyle = UI.typo.b2.style(
                fontWeight = FontWeight.SemiBold,
                color = UI.colors.pureInverse,
                textAlign = TextAlign.Start
            ),
            singleLine = false,
            cursorBrush = SolidColor(UI.colors.pureInverse),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions ?: KeyboardActions(
                onDone = {
                    hideKeyboard(view)
                }
            )
        )
    }
}

@Preview
@Composable
private fun Preview_Hint() {
    MySaveComponentPreview {
        MysaveBasicTextField(
            value = selectEndTextFieldValue(""),
            hint = "Search transfers",
            onValueChanged = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Filled() {
    MySaveComponentPreview {
        MysaveBasicTextField(
            value = selectEndTextFieldValue("sfds"),
            hint = "Okay",
            onValueChanged = {}
        )
    }
}
