package com.financeAndMoney.home.clientJourney

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.financeAndMoney.design.l0_system.Gradient
import com.financeAndMoney.domains.RootScreen
import com.financeAndMoney.legacy.MySaveCtx
import com.financeAndMoney.navigation.Navigation

@Immutable
data class ClientJourneyCardModel(
    val id: String,
    val condition: (trnCount: Long, plannedPaymentsCount: Long, ivyContext: MySaveCtx) -> Boolean,

    val title: String,
    val description: String,
    val cta: String?,
    @DrawableRes val ctaIcon: Int,

    val hasDismiss: Boolean = true,

    val background: Gradient,
    val onAction: (Navigation, MySaveCtx, RootScreen) -> Unit
)
