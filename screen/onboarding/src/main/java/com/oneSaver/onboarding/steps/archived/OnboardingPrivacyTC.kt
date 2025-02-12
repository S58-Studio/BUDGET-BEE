package com.oneSaver.onboarding.steps.archived

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.Constants
import com.oneSaver.legacy.MySavePreview
import com.oneSaver.legacy.mySaveCtx
import com.oneSaver.legacy.rootScreen
import com.oneSaver.legacy.utils.drawColoredShadow
import com.oneSaver.legacy.utils.toDensityDp
import com.oneSaver.legacy.utils.toDensityPx
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.GradientGreen
import com.oneSaver.allStatus.userInterface.theme.Gray
import com.oneSaver.allStatus.userInterface.theme.Green
import com.oneSaver.allStatus.userInterface.theme.White
import com.oneSaver.allStatus.userInterface.theme.components.mysaveIcon
import timber.log.Timber

@Composable
fun OnboardingPrivacyTC(
    onAccept: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Spacer(Modifier.height(40.dp))

        Image(
            modifier = Modifier
                .padding(start = 32.dp)
                .size(
                    width = 56.dp,
                    height = 56.dp
                ),
            painter = painterResource(id = R.drawable.ic_mysave_logo),
            contentScale = ContentScale.FillBounds,
            contentDescription = "Mysave Wallet logo"
        )

        Spacer(Modifier.height(40.dp))

        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = stringResource(R.string.privacy_and_data_collection),
            style = UI.typo.h2.style(
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(20.dp))

        URLsRow()

        Spacer(Modifier.height(44.dp))

        LongText()

        Spacer(Modifier.weight(1f))

        var tcAccepted by remember { mutableStateOf(false) }
        var privacyAccepted by remember { mutableStateOf(false) }

        SwipeToAgree(
            swipeToAgreeText = stringResource(R.string.swipe_to_agree_terms_conditions),
            agreedText = stringResource(R.string.agreed_terms_conditions)
        ) {
            tcAccepted = it
        }

        Spacer(Modifier.height(24.dp))

        SwipeToAgree(
            swipeToAgreeText = stringResource(R.string.swipe_to_agree_privacy_policy),
            agreedText = stringResource(R.string.agreed_privacy_policy)
        ) {
            privacyAccepted = it
        }

        if (tcAccepted && privacyAccepted) {
            onAccept()
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun URLsRow() {
    Row {
        Spacer(Modifier.width(32.dp))

        TextLink(
            text = stringResource(R.string.terms_and_conditions),
            url = Constants.URL_TC
        )

        Spacer(Modifier.width(36.dp))

        TextLink(
            text = stringResource(R.string.privacy_policy),
            url = Constants.URL_PRIVACY_POLICY
        )

        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun LongText() {
    Text(
        modifier = Modifier.padding(
            start = 32.dp,
            end = 48.dp
        ),
        text = stringResource(R.string.mysave_description),
        style = UI.typo.b2.style(
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun TextLink(
    text: String,
    url: String,
) {
    val context = LocalContext.current
    val ivy1 = UI.colors.primary1

    val rootScreen = rootScreen()
    Text(
        modifier = Modifier
            .drawBehind {
                drawRoundRect(
                    color = ivy1,
                    topLeft = Offset(
                        x = 0f,
                        y = this.size.height + 4.dp.toPx()
                    ),
                    size = this.size.copy(
                        height = 2.dp.toPx()
                    ),
                    cornerRadius = CornerRadius(x = 1.dp.toPx()),
                )
            }
            .clickable {
                rootScreen.openUrlInBrowser(url)
            },
        text = text,
        style = UI.typo.b2.style(
            color = ivy1,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun SwipeToAgree(
    swipeToAgreeText: String,
    agreedText: String,
    onAgree: (Boolean) -> Unit
) {
    val ivyContext = mySaveCtx()

    val maxOffsetX = ivyContext.screenWidth - 80.dp.toDensityPx() - 72.dp.toDensityPx()
    var offsetX by remember { mutableFloatStateOf(0f) }

    val percentSwiped = offsetX / maxOffsetX
    val agreed = percentSwiped > 0.5f

    if (percentSwiped > 0.99f || percentSwiped < 0.01f) {
        onAgree(agreed)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 32.dp
            )
            .border(
                width = 2.dp,
                color = if (agreed) Green else UI.colors.medium,
                shape = UI.shapes.r4
            )
            .padding(
                vertical = 8.dp
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        if (agreed) {
            Text(
                modifier = Modifier
                    .padding(start = 32.dp) // 24+8=32.dp
                    .width(164.dp),
                text = agreedText,
                style = UI.typo.c.style(
                    color = Green,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        if (!agreed) {
            Text(
                modifier = Modifier
                    .padding(start = 100.dp)
                    .width(164.dp),
                text = swipeToAgreeText,
                style = UI.typo.c.style(
                    color = Gray,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        mysaveIcon(
            modifier = Modifier
                .padding(start = 8.dp)
                .offset(x = offsetX.toDensityDp())
                .drawColoredShadow(color = Green)
                .background(GradientGreen.asHorizontalBrush(), UI.shapes.r4)
                .pointerInput(onAgree) {
                    detectHorizontalDragGestures(
                        onDragCancel = {
                            if (percentSwiped < 0.9f) {
                                offsetX = 0f
                            }
                        },
                        onDragEnd = {
                            if (percentSwiped < 0.9f) {
                                offsetX = 0f
                            }
                        }
                    ) { _, dragAmount ->
                        val dragAmountScaled = dragAmount * 10
                        val newOffsetX = offsetX + dragAmountScaled
                        Timber.i(
                            "dragAmount=$dragAmount, offsetX=$offsetX, newOffsetX=$newOffsetX, maxOffset=$maxOffsetX"
                        )
                        offsetX = newOffsetX.coerceIn(
                            minimumValue = 0f,
                            maximumValue = maxOffsetX
                        )
                    }
                }
                .padding(horizontal = 20.dp, vertical = 8.dp),
            icon = if (agreed) R.drawable.ic_agreed else R.drawable.ic_swipe_horizontal,
            tint = White
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MySavePreview {
        OnboardingPrivacyTC(
            onAccept = {
            }
        )
    }
}
