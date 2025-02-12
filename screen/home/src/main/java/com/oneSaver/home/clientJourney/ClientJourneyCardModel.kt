package com.oneSaver.home.clientJourney

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.oneSaver.design.l0_system.Gradient
import com.oneSaver.domains.RootScreen
import com.oneSaver.legacy.MySaveCtx
import com.oneSaver.navigation.Navigation

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
