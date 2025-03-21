package com.oneSaver.allStatus.userInterface.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.design.utils.thenIf
import com.oneSaver.legacy.MySaveComponentPreview
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.Green
import com.oneSaver.allStatus.userInterface.theme.Ivy
import com.oneSaver.allStatus.userInterface.theme.Orange
import com.oneSaver.allStatus.userInterface.theme.Red
import com.oneSaver.allStatus.userInterface.theme.White
import com.oneSaver.allStatus.userInterface.theme.wallet.AmountCurrencyB2Row
import kotlin.math.abs

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun BufferBattery(
    modifier: Modifier = Modifier,
    buffer: Double,
    balance: Double,
    currency: String,
    backgroundNotFilled: Color = UI.colors.pure,
    onClick: (() -> Unit)? = null,
) {
    val bufferExceeded = balance < buffer

    val leftToSpend = balance - buffer
    val bufferExceededPercent = if (balance != 0.0) {
        (balance - leftToSpend) / balance
    } else {
        1.0
    }

    val textColor = when {
        bufferExceededPercent <= 0.25 -> {
            UI.colors.pureInverse
        }
        bufferExceededPercent <= 0.50 -> {
            White
        }
        bufferExceededPercent <= 0.75 -> {
            White
        }
        else -> White
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .background(backgroundNotFilled)
            .drawBehind {
                drawRect(
                    color = when {
                        bufferExceededPercent <= 0.25 -> {
                            Green
                        }

                        bufferExceededPercent <= 0.50 -> {
                            Ivy
                        }

                        bufferExceededPercent <= 0.75 -> {
                            Orange
                        }

                        else -> Red
                    },
                    size = size.copy(
                        width = (size.width * bufferExceededPercent).toFloat()
                    )
                )
            }
            .thenIf(onClick != null) {
                clickable {
                    onClick?.invoke()
                }
            }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        mysaveIcon(
            icon = if (bufferExceeded) R.drawable.ic_buffer_exceeded else R.drawable.ic_buffer_ok,
            tint = textColor
        )

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = if (bufferExceeded) {
                    stringResource(R.string.buffer_exceeded_by)
                } else {
                    stringResource(
                        R.string.left_to_spend
                    )
                },
                style = UI.typo.c.style(
                    color = textColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(4.dp))

            AmountCurrencyB2Row(
                amount = abs(leftToSpend),
                currency = currency,
                textColor = textColor
            )
        }
    }
}

@Preview
@Composable
private fun Preview_buffer_0() {
    MySaveComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 0.0,
                balance = 100000.0,
                currency = "BGN"
            )
        }
    }
}

@Preview
@Composable
private fun Preview_buffer_balance_0() {
    MySaveComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 0.0,
                currency = "BGN"
            )
        }
    }
}

@Preview
@Composable
private fun Preview_buffer_very_low() {
    MySaveComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 100000.0,
                currency = "BGN"
            )
        }
    }
}

@Preview
@Composable
private fun Preview_buffer_25() {
    MySaveComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 20000.0,
                currency = "BGN"
            )
        }
    }
}

@Preview
@Composable
private fun Preview_buffer_50() {
    MySaveComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 10000.0,
                currency = "BGN"
            )
        }
    }
}

@Preview
@Composable
private fun Preview_buffer_75() {
    MySaveComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 7500.0,
                currency = "BGN"
            )
        }
    }
}

@Preview
@Composable
private fun Preview_buffer_90() {
    MySaveComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 5500.0,
                currency = "BGN"
            )
        }
    }
}

@Preview
@Composable
private fun Preview_buffer_100() {
    MySaveComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 5000.0,
                currency = "BGN"
            )
        }
    }
}

@Preview
@Composable
private fun Preview_buffer_125() {
    MySaveComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 2500.0,
                currency = "BGN"
            )
        }
    }
}
