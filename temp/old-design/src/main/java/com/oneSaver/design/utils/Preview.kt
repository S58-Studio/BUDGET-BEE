package com.oneSaver.design.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.oneSaver.base.legacy.Theme
import com.oneSaver.base.resource.AndroidResourceProvider
import com.oneSaver.base.time.impl.DeviceTimeProvider
import com.oneSaver.base.time.impl.StandardTimeConverter
import com.oneSaver.design.MysaveContext
import com.oneSaver.design.api.MysaveDesign
import com.oneSaver.design.api.MysaveUI
import com.oneSaver.design.api.systems.MySaveDesign
import com.oneSaver.design.l0_system.UI
import com.oneSaver.userInterface.time.impl.AndroidDevicePreferences
import com.oneSaver.userInterface.time.impl.MysaveTimeFormatter

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun IvyComponentPreview(
    modifier: Modifier = Modifier,
    design: MysaveDesign = defaultDesign(),
    theme: Theme = Theme.LIGHT,
    content: @Composable BoxScope.() -> Unit
) {
    IvyPreview(
        design = design,
        theme = theme
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.pure),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun IvyPreview(
    design: MysaveDesign,
    theme: Theme = Theme.LIGHT,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    design.context().switchTheme(theme = theme)
    val timeProvider = DeviceTimeProvider()
    val timeConverter = StandardTimeConverter(timeProvider)
    MysaveUI(
        design = design,
        content = content,
        timeConverter = timeConverter,
        timeProvider = timeProvider,
        timeFormatter = MysaveTimeFormatter(
            resourceProvider = AndroidResourceProvider(LocalContext.current),
            timeProvider = timeProvider,
            converter = timeConverter,
            devicePreferences = AndroidDevicePreferences(LocalContext.current)
        )
    )
}

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
fun defaultDesign(): MysaveDesign = object : MySaveDesign() {
    override fun context(): MysaveContext = object : MysaveContext() {
    }
}
