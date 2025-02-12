package com.oneSaver.onboarding.steps

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.base.utils.MySaveAdsManager
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.Constants
import com.oneSaver.legacy.MySaveCtx
import com.oneSaver.legacy.MySavePreview
import com.oneSaver.legacy.mySaveCtx
import com.oneSaver.legacy.utils.clickableNoIndication
import com.oneSaver.legacy.utils.drawColoredShadow
import com.oneSaver.legacy.utils.lerp
import com.oneSaver.legacy.utils.springBounceSlow
import com.oneSaver.design.utils.thenIf
import com.oneSaver.legacy.utils.rememberInteractionSource
import com.oneSaver.legacy.utils.toDensityDp
import com.oneSaver.legacy.utils.toDensityPx
import com.oneSaver.onboarding.OnboardingState
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.Gradient
import com.oneSaver.allStatus.userInterface.theme.Gray
import com.oneSaver.allStatus.userInterface.theme.Green
import com.oneSaver.allStatus.userInterface.theme.components.mysaveIcon
import kotlin.math.roundToInt

@Composable
fun BoxWithConstraintsScope.OnboardingSplashLogin(
    onboardingState: OnboardingState,
    onSkip: () -> Unit,
    activity: Activity
) {
    var internalSwitch by remember { mutableStateOf(true) }

    val transition = updateTransition(
        targetState = if (!internalSwitch) OnboardingState.LOGIN else onboardingState,
        label = "Splash"
    )

    val logoWidth by transition.animateDp(
        transitionSpec = {
            springBounceSlow()
        },
        label = "logoWidth"
    ) {
        when (it) {
            OnboardingState.SPLASH -> 113.dp
            else -> 76.dp
        }
    }

    val logoHeight by transition.animateDp(
        transitionSpec = {
            springBounceSlow()
        },
        label = "logoHeight"
    ) {
        when (it) {
            OnboardingState.SPLASH -> 113.dp
            else -> 76.dp
        }
    }

    val ivyContext = mySaveCtx()

    val spacerTop by transition.animateDp(
        transitionSpec = {
            springBounceSlow()
        },
        label = "spacerTop"
    ) {
        when (it) {
            OnboardingState.SPLASH -> {
                (ivyContext.screenHeight / 2f - logoHeight.toDensityPx() / 2f).toDensityDp()
            }

            else -> 56.dp
        }
    }

    val percentTransition by transition.animateFloat(
        transitionSpec = {
            springBounceSlow()
        },
        label = "percentTransition"
    ) {
        when (it) {
            OnboardingState.SPLASH -> 0f
            else -> 1f
        }
    }

    val marginTextTop by transition.animateDp(
        transitionSpec = {
            springBounceSlow()
        },
        label = "marginTextTop"
    ) {
        when (it) {
            OnboardingState.SPLASH -> 64.dp
            else -> 40.dp
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UI.colors.pure)
            .systemBarsPadding()
            .navigationBarsPadding()
    ) {
        Spacer(Modifier.height(spacerTop))

        Image(
            modifier = Modifier
                .size(
                    width = logoWidth,
                    height = logoHeight
                )
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)

                    val xSplash = ivyContext.screenWidth / 2f - placeable.width / 2
                    val xLogin = 24.dp.toPx()

                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(
                            x = lerp(xSplash, xLogin, percentTransition).roundToInt(),
                            y = 0,
                        )
                    }
                }
                .clickableNoIndication(rememberInteractionSource()) {
                    internalSwitch = !internalSwitch
                },
            painter = painterResource(id = R.drawable.ic_mysave_logo),
            contentScale = ContentScale.FillBounds,
            contentDescription = "MySpace logo"
        )

        Spacer(Modifier.height(marginTextTop))

        Text(
            modifier = Modifier.animateXCenterToLeft(
                ivyContext = ivyContext,
                percentTransition = percentTransition
            ),
            text = stringResource(R.string.app_name),
            style = UI.typo.h2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.animateXCenterToLeft(
                ivyContext = ivyContext,
                percentTransition = percentTransition
            ),
            text = stringResource(R.string.your_personal_money_manager),
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.SemiBold
            )
        )

        LoginSection(
            percentTransition = percentTransition,
            onSkip = onSkip,
            activity = activity
        )
    }
}

private fun Modifier.animateXCenterToLeft(
    ivyContext: MySaveCtx,
    percentTransition: Float
): Modifier {
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            val xSplash = ivyContext.screenWidth / 2f - placeable.width / 2
            val xLogin = 32.dp.toPx()

            placeable.placeRelative(
                x = lerp(xSplash, xLogin, percentTransition).roundToInt(),
                y = 0
            )
        }
    }
}

@Composable
private fun LoginSection(
    percentTransition: Float,
    onSkip: () -> Unit,
    activity: Activity
) {
    val mySaveAdsManager = remember { MySaveAdsManager.getInstance() }

    if (percentTransition > 0.01f) {
        Column(
            modifier = Modifier
                .alpha(percentTransition),
        ) {
            Spacer(Modifier.height(16.dp))
            Spacer(Modifier.weight(1f))

            LocalAccountExplanation()

            Spacer(Modifier.height(16.dp))

            LoginButton(
                icon = R.drawable.ic_local_account,
                text = stringResource(R.string.let_get_started),
                textColor = UI.colors.pureInverse,
                backgroundGradient = Gradient.solid(UI.colors.medium),
                hasShadow = false
            ) {
                if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                    val adCallback = MySaveAdsManager.OnAdsCallback {
                        onSkip()
                    }
                    mySaveAdsManager.displayAds(activity, adCallback)
                }
            }

            Spacer(Modifier.weight(3f))
            Spacer(Modifier.height(16.dp))

            PrivacyPolicyAndTC()

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LocalAccountExplanation() {
    Text(
        modifier = Modifier.padding(start = 32.dp),
        text = stringResource(R.string.enter_with_offline_account),
        style = UI.typo.c.style(
            color = Gray,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(4.dp))

    Text(
        modifier = Modifier.padding(start = 32.dp, end = 32.dp),
        text = stringResource(R.string.your_data_will_be_saved_only_locally),
        style = UI.typo.c.style(
            color = Gray,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun PrivacyPolicyAndTC() {
    val terms = stringResource(R.string.terms_conditions)
    val privacy = stringResource(R.string.privacy_policy)
    val text = stringResource(R.string.by_signing_in, terms, privacy)

    val tcStart = text.indexOf(terms)
    val tcEnd = tcStart + terms.length

    val privacyStart = text.indexOf(privacy)
    val privacyEnd = privacyStart + privacy.length

    val annotatedString = buildAnnotatedString {
        append(text)

        addStringAnnotation(
            tag = "URL",
            annotation = Constants.URL_TC,
            start = tcStart,
            end = tcEnd
        )

        addStringAnnotation(
            tag = "URL",
            annotation = Constants.URL_PRIVACY_POLICY,
            start = privacyStart,
            end = privacyEnd
        )

        addStyle(
            style = SpanStyle(
                color = Green,
                textDecoration = TextDecoration.Underline
            ),
            start = tcStart,
            end = tcEnd
        )

        addStyle(
            style = SpanStyle(
                color = Green,
                textDecoration = TextDecoration.Underline
            ),
            start = privacyStart,
            end = privacyEnd
        )
    }

    val uriHandler = LocalUriHandler.current
    ClickableText(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        text = annotatedString,
        style = UI.typo.c.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        ),
        onClick = {
            annotatedString
                .getStringAnnotations("URL", it, it)
                .forEach { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                }
        }
    )
}

@Composable
private fun LoginButton(
    @DrawableRes icon: Int,
    text: String,
    textColor: Color,
    backgroundGradient: Gradient,
    hasShadow: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .thenIf(hasShadow) {
                drawColoredShadow(backgroundGradient.startColor)
            }
            .clip(UI.shapes.r4)
            .background(backgroundGradient.asHorizontalBrush(), UI.shapes.r4)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        mysaveIcon(
            icon = icon,
            tint = textColor
        )

        Spacer(Modifier.width(16.dp))

        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            text = text,
            style = UI.typo.b2.style(
                color = textColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MySavePreview {
        OnboardingSplashLogin(
            onboardingState = OnboardingState.SPLASH,
            onSkip = {},
            activity = FakeActivity() // Use a placeholder or mock activity
        )
    }
}

// Create a fake or mock activity class for preview purposes
class FakeActivity : Activity()
