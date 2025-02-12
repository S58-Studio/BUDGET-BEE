package com.oneSaver.legacy

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.oneSaver.base.legacy.Theme
import com.oneSaver.base.legacy.appContext
import com.oneSaver.design.MysaveContext
import com.oneSaver.design.api.MysaveDesign
import com.oneSaver.design.api.ivyContext
import com.oneSaver.design.api.systems.MySaveDesign
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.utils.IvyPreview
import com.oneSaver.domains.RootScreen
import com.oneSaver.navigation.Navigation
import com.oneSaver.navigation.NavigationRoot

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun mySaveCtx(): MySaveCtx = ivyContext() as MySaveCtx

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun rootView(): View = LocalView.current

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun rootActivity(): AppCompatActivity = LocalContext.current as AppCompatActivity

@Composable
fun rootScreen(): RootScreen = LocalContext.current as RootScreen

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun MySaveComponentPreview(
    theme: Theme = Theme.LIGHT,
    Content: @Composable BoxScope.() -> Unit
) {
    MySavePreview(
        theme = theme
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.pure),
            contentAlignment = Alignment.Center
        ) {
            Content()
        }
    }
}

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun MySavePreview(
    theme: Theme = Theme.LIGHT,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    appContext = rootView().context
    IvyPreview(
        theme = theme,
        design = appDesign(MySaveCtx()),
    ) {
        NavigationRoot(navigation = Navigation()) {
            content()
        }
    }
}

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
fun appDesign(context: MySaveCtx): MysaveDesign = object : MySaveDesign() {
    override fun context(): MysaveContext = context
}
