package com.financeAndMoney.design.api

import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.design.MysaveContext
import com.financeAndMoney.design.l0_system.IvyColors
import com.financeAndMoney.design.l0_system.IvyShapes
import com.financeAndMoney.design.l0_system.IvyTypography

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
interface IvyDesign {
    @Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
    fun context(): MysaveContext

    @Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
    fun typography(): IvyTypography

    @Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
    fun colors(theme: Theme, isDarkModeEnabled: Boolean): IvyColors

    @Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
    fun shapes(): IvyShapes
}
