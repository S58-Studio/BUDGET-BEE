package com.oneSaver.design.api

import com.oneSaver.base.legacy.Theme
import com.oneSaver.design.MysaveContext
import com.oneSaver.design.l0_system.IvyColors
import com.oneSaver.design.l0_system.IvyShapes
import com.oneSaver.design.l0_system.IvyTypography

@Deprecated("Old design system. Use `:ivy-design` and Material3")
interface MysaveDesign {
    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    fun context(): MysaveContext

    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    fun typography(): IvyTypography

    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    fun colors(theme: Theme, isDarkModeEnabled: Boolean): IvyColors

    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    fun shapes(): IvyShapes
}
