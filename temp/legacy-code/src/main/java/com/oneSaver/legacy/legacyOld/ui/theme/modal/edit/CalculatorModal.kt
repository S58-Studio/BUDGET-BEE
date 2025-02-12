package com.oneSaver.allStatus.userInterface.theme.modal.edit

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.MySavePreview
import com.oneSaver.legacy.utils.amountToDoubleOrNull
import com.oneSaver.legacy.utils.format
import com.oneSaver.legacy.utils.formatInputAmount
import com.oneSaver.legacy.utils.localDecimalSeparator
import com.oneSaver.legacy.utils.normalizeExpression
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.Gray
import com.oneSaver.allStatus.userInterface.theme.Red
import com.oneSaver.allStatus.userInterface.theme.modal.MysaveModal
import com.oneSaver.legacy.legacyOld.ui.theme.modal.ModalSet
import com.oneSaver.legacy.legacyOld.ui.theme.modal.ModalTitle
import com.notkamui.keval.Keval
import java.util.UUID

@SuppressLint("ComposeModifierMissing")
@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun BoxWithConstraintsScope.CalculatorModal(
      initialAmount: Double?,
      visible: Boolean,
      currency: String,
      dismiss: () -> Unit,
      id: UUID = UUID.randomUUID(),
      onCalculation: (Double) -> Unit
) {
    var expression by remember(id, initialAmount) {
        mutableStateOf(initialAmount?.format(currency) ?: "")
    }

    MysaveModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSet(
                modifier = Modifier.testTag("calc_set")
            ) {
                val result = calculate(expression)
                if (result != null) {
                    onCalculation(result)
                    dismiss()
                }
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(text = stringResource(R.string.app_calculator))

        Spacer(Modifier.height(32.dp))

        val isEmpty = expression.isBlank()
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            text = if (isEmpty) stringResource(R.string.calculator_empty_expression) else expression,
            style = UI.typo.nH2.style(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (isEmpty) Gray else UI.colors.pureInverse
            )
        )

        Spacer(Modifier.height(32.dp))

        AmountKeyboard(
            forCalculator = true,
            ZeroRow = {
                KeypadCircleButton(
                    text = "C",
                    textColor = Red,
                    testTag = "key_C"
                ) {
                    expression = ""
                }

                KeypadCircleButton(
                    text = "(",
                    testTag = "key_("
                ) {
                    expression += "("
                }

                KeypadCircleButton(
                    text = ")",
                    testTag = "key_)"
                ) {
                    expression += ")"
                }

                KeypadCircleButton(
                    text = "÷",
                    testTag = "key_/"
                ) {
                    expression += "÷"
                }
            },
            FirstRowExtra = {
                KeypadCircleButton(
                    text = "×",
                    testTag = "key_*"
                ) {
                    expression += "×"
                }
            },
            SecondRowExtra = {
                KeypadCircleButton(
                    text = "−",
                    testTag = "key_-"
                ) {
                    expression += "−"
                }
            },
            ThirdRowExtra = {
                KeypadCircleButton(
                    text = "+",
                    testTag = "key_+"
                ) {
                    expression += "+"
                }
            },
            FourthRowExtra = {
                KeypadCircleButton(
                    text = "=",
                    testTag = "key_="
                ) {
                    val result = calculate(expression)
                    if (result != null) {
                        expression = result.format(currency)
                    }
                }
            },

            onNumberPressed = {
                expression = formatExpression(
                    expression = expression + it,
                    currency = currency
                )
            },
            onDecimalPoint = {
                expression = formatExpression(
                    expression = expression + localDecimalSeparator(),
                    currency = currency
                )
            },
            onBackspace = {
                if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
                }
            }
        )

        Spacer(Modifier.height(24.dp))
    }
}

private fun formatExpression(expression: String, currency: String): String {
    var formattedExpression = expression

    expression
        .split("(", ")", "÷", "×", "−", "+")
        .ifEmpty {
            // handle only number expression formatting
            listOf(expression)
        }
        .forEach { part ->
            val numberPart = part.amountToDoubleOrNull()
            if (numberPart != null) {
                val formattedPart = formatInputAmount(
                    currency = currency,
                    amount = part,
                    newSymbol = ""
                )

                if (formattedPart != null) {
                    formattedExpression = formattedExpression.replace(part, formattedPart)
                }
            }
        }

    return formattedExpression
}

private fun calculate(expression: String): Double? {
    return try {
        // Keval doesn't support negative numbers, so we add a zero in front of the expression
        val expression = buildString {
            for (char in expression) {
                when (char) {
                    '÷' -> this.append('/')
                    '×' -> this.append('*')
                    '−' -> this.append('-')
                    else -> this.append(char)
                }
            }
        }
        val modifiedExpression = if (expression.startsWith("-")) "0$expression" else expression
        Keval.eval(modifiedExpression.normalizeExpression())
    } catch (e: Exception) {
        null
    }
}

@Preview
@Composable
private fun Preview() {
    MySavePreview {
        CalculatorModal(
            visible = true,
            initialAmount = 50.23,
            currency = "BGN",
            dismiss = { },
            onCalculation = {}
        )
    }
}
