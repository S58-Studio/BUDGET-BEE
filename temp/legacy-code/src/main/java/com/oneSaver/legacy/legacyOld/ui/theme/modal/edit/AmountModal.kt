package com.oneSaver.allStatus.userInterface.theme.modal.edit

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.MySavePreview
import com.oneSaver.legacy.utils.amountToDouble
import com.oneSaver.legacy.utils.amountToDoubleOrNull
import com.oneSaver.legacy.utils.format
import com.oneSaver.legacy.utils.formatInputAmount
import com.oneSaver.legacy.utils.formatInt
import com.oneSaver.legacy.utils.hideKeyboard
import com.oneSaver.legacy.utils.localDecimalSeparator
import com.oneSaver.legacy.utils.onScreenStart
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.Red
import com.oneSaver.allStatus.userInterface.theme.components.mysaveIcon
import com.oneSaver.allStatus.userInterface.theme.modal.MysaveModal
import com.oneSaver.legacy.legacyOld.ui.theme.modal.ModalPositiveButton
import com.oneSaver.allStatus.userInterface.theme.modal.modalPreviewActionRowHeight
import java.util.UUID
import kotlin.math.truncate

@SuppressLint("ComposeModifierMissing")
@Suppress("ParameterNaming")
@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun BoxWithConstraintsScope.AmountModal(
    id: UUID,
    visible: Boolean,
    currency: String,
    initialAmount: Double?,
    dismiss: () -> Unit,
    showPlusMinus: Boolean = false,
    decimalCountMax: Int = 2,
    Header: (@Composable () -> Unit)? = null,
    amountSpacerTop: Dp = 64.dp,
    onAmountChanged: (Double) -> Unit,
) {
    var amount by remember(id) {
        mutableStateOf(
            if (currency.isNotEmpty()) {
                initialAmount?.takeIf { it != 0.0 }?.format(currency)
                    ?: ""
            } else {
                initialAmount?.takeIf { it != 0.0 }?.format(decimalCountMax)
                    ?: ""
            }
        )
    }

    var calculatorModalVisible by remember(id) {
        mutableStateOf(false)
    }

    MysaveModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            mysaveIcon(
                modifier = circleButtonModifier(
                    size = 52.dp,
                    onClick = {
                        calculatorModalVisible = true
                    }
                )
                    .testTag("btn_calculator")
                    .padding(all = 4.dp),
                icon = R.drawable.ic_custom_calculator_m,
                tint = UI.colors.pureInverse
            )

            Spacer(Modifier.width(16.dp))

            ModalPositiveButton(
                text = stringResource(R.string.enter),
                iconStart = R.drawable.ic_check
            ) {
                try {
                    onAmountChanged(amount.amountToDouble())
                    dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        },
        SecondaryActions = {
            if (showPlusMinus) {
                Row {
                    Spacer(modifier = Modifier.width(24.dp))
                    KeypadCircleButton(
                        text = "+/-",
                        testTag = "plus_minus",
                        fontSize = 22.sp,
                        btnSize = 52.dp,
                        onClick = {
                            when {
                                amount.firstOrNull() == '-' -> {
                                    amount = amount.drop(1)
                                }

                                amount.isNotEmpty() -> {
                                    amount = "-$amount"
                                }
                            }
                        }
                    )
                }
            }
        }
    ) {
        Header?.invoke()

        Spacer(Modifier.height(amountSpacerTop))

        val rootView = LocalView.current
        onScreenStart {
            hideKeyboard(rootView)
        }

        AmountCurrency(
            amount = amount,
            currency = currency
        )

        Spacer(Modifier.height(10.dp))

        AmountInput(
            currency = currency,
            decimalCountMax = decimalCountMax,
            amount = amount
        ) {
            amount = it
        }

        Spacer(Modifier.height(24.dp))
    }

    CalculatorModal(
        visible = calculatorModalVisible,
        initialAmount = amount.amountToDoubleOrNull(),
        currency = currency,
        dismiss = {
            calculatorModalVisible = false
        },
        onCalculation = {
            amount = if (currency.isNotEmpty()) it.format(currency) else it.format(decimalCountMax)
        }
    )
}

@SuppressLint("ComposeModifierMissing")
@Composable
fun AmountCurrency(
    amount: String,
    currency: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.weight(1f))

        Text(
            text = amount.ifBlank { "0" },
            style = UI.typo.nH2.style(
                fontWeight = FontWeight.Bold,
                color = UI.colors.pureInverse
            )
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = currency,
            style = UI.typo.nH2.style(
                fontWeight = FontWeight.Normal,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun AmountInput(
    currency: String,
    amount: String,
    decimalCountMax: Int = 2,
    setAmount: (String) -> Unit,

    ) {
    var firstInput by remember { mutableStateOf(true) }

    AmountKeyboard(
        horizontalPadding = 40.dp,
        forCalculator = false,
        onNumberPressed = {
            if (firstInput) {
                setAmount(it)
                firstInput = false
            } else {
                val formattedAmount = formatInputAmount(
                    currency = currency,
                    amount = amount,
                    newSymbol = it,
                    decimalCountMax = decimalCountMax
                )
                if (formattedAmount != null) {
                    setAmount(formattedAmount)
                }
            }
        },
        onDecimalPoint = {
            if (firstInput) {
                setAmount("0${localDecimalSeparator()}")
                firstInput = false
            } else {
                val newlyEnteredString = if (amount.isEmpty()) {
                    "0${localDecimalSeparator()}"
                } else {
                    "$amount${localDecimalSeparator()}"
                }
                if (newlyEnteredString.amountToDoubleOrNull() != null) {
                    setAmount(newlyEnteredString)
                }
            }
        },
        onBackspace = {
            if (firstInput) {
                setAmount("")
                firstInput = false
            } else {
                if (amount.isNotEmpty()) {
                    val formattedNumber = formatNumber(amount.dropLast(1))
                    setAmount(formattedNumber ?: "")
                }
            }
        }
    )
}

private fun formatNumber(number: String): String? {
    val decimalPartString = number
        .split(localDecimalSeparator())
        .getOrNull(1)
    val newDecimalCount = decimalPartString?.length ?: 0

    val amountDouble = number.amountToDoubleOrNull()

    if (newDecimalCount <= 2 && amountDouble != null) {
        val intPart = truncate(amountDouble).toInt()
        val decimalFormatted = if (decimalPartString != null) {
            "${localDecimalSeparator()}$decimalPartString"
        } else {
            ""
        }

        return formatInt(intPart) + decimalFormatted
    }

    return null
}

@SuppressLint(
    "ComposeContentEmitterReturningValues",
    "ComposeMultipleContentEmitters",
    "ComposeModifierMissing",
)
@Suppress("ParameterNaming")
@Composable
fun AmountKeyboard(
    forCalculator: Boolean,
    onNumberPressed: (String) -> Unit,
    onDecimalPoint: () -> Unit,
    horizontalPadding: Dp = 0.dp,
    ZeroRow: (@Composable RowScope.() -> Unit)? = null,
    FirstRowExtra: (@Composable RowScope.() -> Unit)? = null,
    SecondRowExtra: (@Composable RowScope.() -> Unit)? = null,
    ThirdRowExtra: (@Composable RowScope.() -> Unit)? = null,
    FourthRowExtra: (@Composable RowScope.() -> Unit)? = null,
    onBackspace: () -> Unit,
) {
    if (ZeroRow != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ZeroRow.invoke(this)
        }

        Spacer(Modifier.height(8.dp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly

    ) {
        CircleNumberButton(
            forCalculator = forCalculator,
            value = "7",
            onNumberPressed = onNumberPressed
        )

        CircleNumberButton(
            forCalculator = forCalculator,
            value = "8",
            onNumberPressed = onNumberPressed
        )

        CircleNumberButton(
            forCalculator = forCalculator,
            value = "9",
            onNumberPressed = onNumberPressed
        )

        FirstRowExtra?.invoke(this)
    }

    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CircleNumberButton(
            forCalculator = forCalculator,
            value = "4",
            onNumberPressed = onNumberPressed
        )

        CircleNumberButton(
            forCalculator = forCalculator,
            value = "5",
            onNumberPressed = onNumberPressed
        )

        CircleNumberButton(
            forCalculator = forCalculator,
            value = "6",
            onNumberPressed = onNumberPressed
        )

        SecondRowExtra?.invoke(this)
    }

    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CircleNumberButton(
            forCalculator = forCalculator,
            value = "1",
            onNumberPressed = onNumberPressed
        )

        CircleNumberButton(
            forCalculator = forCalculator,
            value = "2",
            onNumberPressed = onNumberPressed
        )

        CircleNumberButton(
            forCalculator = forCalculator,
            value = "3",
            onNumberPressed = onNumberPressed
        )

        ThirdRowExtra?.invoke(this)
    }

    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        KeypadCircleButton(
            text = localDecimalSeparator(),
            testTag = if (forCalculator) {
                "calc_key_decimal_separator"
            } else {
                "key_decimal_separator"
            }
        ) {
            onDecimalPoint()
        }

        CircleNumberButton(
            forCalculator = forCalculator,
            value = "0",
            onNumberPressed = onNumberPressed
        )

        mysaveIcon(
            modifier = circleButtonModifier(onClick = onBackspace)
                .padding(all = 24.dp)
                .testTag("key_del"),
            icon = R.drawable.ic_backspace,
            tint = Red
        )

        FourthRowExtra?.invoke(this)
    }
}

@Composable
@Suppress("ParameterNaming")
fun CircleNumberButton(
    forCalculator: Boolean,
    value: String,
    onNumberPressed: (String) -> Unit,
) {
    KeypadCircleButton(
        text = value,
        testTag = if (forCalculator) {
            "calc_key_$value"
        } else {
            "key_$value"
        },
        onClick = {
            onNumberPressed(value)
        }
    )
}

@SuppressLint("ComposeModifierMissing")
@Composable
fun KeypadCircleButton(
    text: String,
    testTag: String,
    textColor: Color = UI.colors.pureInverse,
    fontSize: TextUnit = 32.sp,
    btnSize: Dp = 80.dp,
    onClick: () -> Unit,
) {
    Text(
        modifier = circleButtonModifier(size = btnSize, onClick = onClick)
            .testTag(testTag),
        text = text,
        fontSize = fontSize,
        style = UI.typo.nH2.style(
            color = textColor,
            fontWeight = FontWeight.Bold
        ).copy(
            textAlign = TextAlign.Center
        )
    )
}

@SuppressLint("ComposableModifierFactory", "ModifierFactoryExtensionFunction")
@Composable
private fun circleButtonModifier(
    size: Dp = 80.dp,
    onClick: () -> Unit,
): Modifier {
    return Modifier
        .size(size)
        .clip(CircleShape)
        .clickable(
            onClick = onClick
        )
        .background(UI.colors.pure, UI.shapes.rFull)
        .border(2.dp, UI.colors.medium, UI.shapes.rFull)
        .wrapContentHeight()
}

@Preview
@Composable
private fun Preview() {
    MySavePreview {
        BoxWithConstraints(
            modifier = Modifier.padding(bottom = modalPreviewActionRowHeight())
        ) {
            AmountModal(
                id = UUID.randomUUID(),
                visible = true,
                currency = "BGN",
                initialAmount = null,
                dismiss = { }
            ) {
            }
        }
    }
}
