package com.oneSaver.main

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.data.model.MainTab
import com.oneSaver.legacy.mySaveCtx
import com.oneSaver.legacy.utils.clickableNoIndication
import com.oneSaver.legacy.utils.densityScope
import com.oneSaver.legacy.utils.navigationBarInset
import com.oneSaver.legacy.utils.rememberInteractionSource
import com.oneSaver.legacy.utils.springBounceFast
import com.oneSaver.legacy.utils.toDensityPx
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.GradientMysave
import com.oneSaver.allStatus.userInterface.theme.Green
import com.oneSaver.allStatus.userInterface.theme.Ivy
import com.oneSaver.allStatus.userInterface.theme.White
import com.oneSaver.legacy.legacyOld.ui.theme.components.MysaveCircleButton
import com.oneSaver.allStatus.userInterface.theme.components.mysaveIcon
import com.oneSaver.allStatus.userInterface.theme.modal.AddModalBackHandling
import com.oneSaver.allStatus.userInterface.theme.pureBlur
import java.util.UUID

val TRN_BUTTON_CLICK_AREA_HEIGHT = 150.dp
val FAB_BUTTON_SIZE = 56.dp

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun BoxWithConstraintsScope.BottomBar(
    tab: MainTab,
    selectTab: (MainTab) -> Unit,

    showAddAccountModal: () -> Unit,
) {
    val ivyContext = mySaveCtx()

    var expanded by remember { mutableStateOf(false) }

    val modalId = remember { UUID.randomUUID() }

    AddModalBackHandling(
        modalId = modalId,
        visible = expanded
    ) {
        expanded = false
    }

    val screenHeightDp = densityScope { ivyContext.screenHeight.toDp() }
    val expandedBackgroundOffset by animateDpAsState(
        targetValue = if (expanded) 0.dp else screenHeightDp,
        animationSpec = springBounceFast()
    )

    val fabRotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = springBounceFast()
    )

    val buttonsShownPercent by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = springBounceFast()
    )

    Row(
        modifier = Modifier

            .align(Alignment.BottomCenter)
            .background(pureBlur())
            .alpha(1f - buttonsShownPercent)
            .navigationBarsPadding()
            .clickableNoIndication(rememberInteractionSource()) {
                // consume click
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Tab(
            icon = R.drawable.ic_home,
            name = stringResource(R.string.home),
            selected = tab == MainTab.HOME,
            selectedColor = Ivy
        ) {
            selectTab(MainTab.HOME)
        }

        Tab(
            icon = R.drawable.ic_reports_charts,
            name = stringResource(R.string.insights),
            selected = tab == MainTab.INSIGHTS,
            selectedColor = Ivy
        ) {
            selectTab(MainTab.INSIGHTS)
        }
//        Spacer(Modifier.width(FAB_BUTTON_SIZE))

        Tab(
            icon = R.drawable.ic_accounts,
            name = stringResource(R.string.all_accounts),
            selected = tab == MainTab.ACCOUNTS,
            selectedColor = Green
        ) {
            selectTab(MainTab.ACCOUNTS)
        }
//        Spacer(Modifier.width(FAB_BUTTON_SIZE))

        Tab(
            icon = R.drawable.ic_profile_me,
            name = stringResource(R.string.profile_me),
            selected = tab == MainTab.PROFILE,
            selectedColor = Green
        ) {
            selectTab(MainTab.PROFILE)
        }
    }

    if (expandedBackgroundOffset < screenHeightDp) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = expandedBackgroundOffset)
                .background(UI.colors.pure.copy(alpha = 0.95f))
                .clickableNoIndication(rememberInteractionSource()) {
                    // consume click, do nothing
                }
                .zIndex(100f)
        )
    }

    // ------------------------------------ BUTTONS--------------------------------------------------
    val fabStartX = ivyContext.screenWidth / 2 - FAB_BUTTON_SIZE.toDensityPx() / 2
    val fabStartY = ivyContext.screenHeight - navigationBarInset() -
            30.dp.toDensityPx() - FAB_BUTTON_SIZE.toDensityPx()


    var dragOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    // + & x button
    if (tab == MainTab.ACCOUNTS) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 20.dp, top = 28.dp) // Add padding to space from edges
        ) {
            MysaveCircleButton(
                modifier = Modifier
                    .size(FAB_BUTTON_SIZE)
                    .align(Alignment.TopEnd) // Align to the bottom end
                    .rotate(fabRotation)
                    .zIndex(200f) // Ensure it's on top
                    .testTag("fab_add"),
                backgroundPadding = 8.dp,
                icon = R.drawable.ic_add,
                backgroundGradient = GradientMysave,
                hasShadow = !expanded,
                tint = White
            ) {
                showAddAccountModal()
            }
        }
    }
}


@Composable
private fun RowScope.Tab(
    @DrawableRes icon: Int,
    name: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
) {
    // Use a Box to wrap the Row and control the alignment
    Box(
        modifier = Modifier
            .weight(1f)  // Ensure each tab takes an equal amount of space
            .clip(UI.shapes.rFull)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)  // Adjust padding as needed
            .testTag(name.lowercase())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()  // Ensure Row takes full width of the Box
                .align(Alignment.Center),  // Adjust padding as needed
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            mysaveIcon(
                icon = icon,
                tint = if (selected) selectedColor else UI.colors.pureInverse,
                modifier = Modifier.size(34.dp)  // Adjust icon size as needed
            )

            Spacer(modifier = Modifier.width(2.dp))

            Text(
                text = name,
                style = UI.typo.nC.style(
                    fontWeight = FontWeight.Bold,
                    color = selectedColor
                )
            )
        }
    }
}




