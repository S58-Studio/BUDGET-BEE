package com.financeAndMoney.home.clientJourney

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.domains.RootScreen
import com.financeAndMoney.legacy.mySaveCtx
import com.financeAndMoney.legacy.rootScreen
import com.financeAndMoney.legacy.utils.drawColoredShadow
import com.financeAndMoney.navigation.MylonPreview
import com.financeAndMoney.navigation.navigation
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Gradient
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.MysaveButton
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.mysaveIcon
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.dynamicContrast
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.findContrastTextColor
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CustomerJourney(
    customerJourneyCards: ImmutableList<ClientJourneyCardModel>,
    modifier: Modifier = Modifier,
    onDismiss: (ClientJourneyCardModel) -> Unit,
) {
    val ivyContext = mySaveCtx()
    val nav = navigation()
    // Check is added for Paparazzi Test where context is different
    if (LocalContext.current is RootScreen) {
        val rootScreen = rootScreen()

        if (customerJourneyCards.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
        }

        for (card in customerJourneyCards) {
            Spacer(Modifier.height(12.dp))

            CustomerJourneyCard(
                modifier = modifier,
                cardData = card,
                onDismiss = {
                    onDismiss(card)
                }
            ) {
                card.onAction(nav, ivyContext, rootScreen)
            }
        }
    } else {
        Box(modifier)
    }
}

@Composable
fun CustomerJourneyCard(
    cardData: ClientJourneyCardModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onCTA: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .drawColoredShadow(cardData.background.startColor)
            .background(cardData.background.asHorizontalBrush(), UI.shapes.r3)
            .clip(UI.shapes.r3)
            .clickable {
                onCTA()
            }
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 24.dp, end = 16.dp),
                text = cardData.title,
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = findContrastTextColor(cardData.background.startColor)
                )
            )

            if (cardData.hasDismiss) {
                mysaveIcon(
                    modifier = Modifier
                        .clickable {
                            onDismiss()
                        }
                        .padding(8.dp), // enlarge click area
                    icon = R.drawable.ic_dismiss,
                    tint = cardData.background.startColor.dynamicContrast(),
                    contentDescription = "prompt_dismiss",
                )

                Spacer(Modifier.width(20.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 32.dp),
            text = cardData.description,
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Medium,
                color = findContrastTextColor(cardData.background.startColor)
            )
        )

        Spacer(Modifier.height(32.dp))

        if (cardData.cta != null) {
            MysaveButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 20.dp)
                    .testTag("cta_prompt_${cardData.id}"),
                text = cardData.cta,
                shadowAlpha = 0f,
                iconStart = cardData.ctaIcon,
                iconTint = cardData.background.startColor,
                textStyle = UI.typo.b2.style(
                    color = cardData.background.startColor,
                    fontWeight = FontWeight.Bold
                ),
                padding = 8.dp,
                backgroundGradient = Gradient.solid(findContrastTextColor(cardData.background.startColor))
            ) {
                onCTA()
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewCard() {
    MylonPreview {
        CustomerJourneyCard(
            cardData = ClientJourneyCardsProvider.adjustBalanceCard(),
            onCTA = { },
            onDismiss = {}
        )
    }
}
