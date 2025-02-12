package com.oneSaver.iliyopangwa.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.CategoryId
import com.oneSaver.data.model.IntervalType
import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.MySavePreview
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.datamodel.PlannedPaymentRule
import com.oneSaver.legacy.forDisplay
import com.oneSaver.legacy.ui.component.transaction.TypeAmountCurrency
import com.oneSaver.legacy.utils.formatDateOnly
import com.oneSaver.legacy.utils.formatDateOnlyWithYear
import com.oneSaver.legacy.utils.isNotNullOrBlank
import com.oneSaver.legacy.utils.timeNowUTC
import com.oneSaver.legacy.utils.uppercaseLocal
import com.oneSaver.navigation.TransactScrin
import com.oneSaver.navigation.navigation
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.Blue
import com.oneSaver.allStatus.userInterface.theme.Gradient
import com.oneSaver.allStatus.userInterface.theme.Green
import com.oneSaver.allStatus.userInterface.theme.Orange
import com.oneSaver.legacy.legacyOld.ui.theme.components.MysaveButton
import com.oneSaver.allStatus.userInterface.theme.components.mysaveIcon
import com.oneSaver.allStatus.userInterface.theme.components.getCustomIconIdS
import com.oneSaver.allStatus.userInterface.theme.findContrastTextColor
import com.oneSaver.allStatus.userInterface.theme.toComposeColor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun LazyItemScope.ScheduledPaymntKard(
    baseCurrency: String,
    categories: ImmutableList<Category>,
    accounts: ImmutableList<Account>,
    plannedPayment: PlannedPaymentRule,
    onClick: (PlannedPaymentRule) -> Unit,
) {
    Spacer(Modifier.height(12.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.r4)
            .clickable {
                if (accounts.find { it.id == plannedPayment.accountId } != null) {
                    onClick(plannedPayment)
                }
            }
            .background(UI.colors.medium, UI.shapes.r4)
            .testTag("planned_payment_card")
    ) {
        val currency = accounts.find { it.id == plannedPayment.accountId }?.currency ?: baseCurrency

        Spacer(Modifier.height(20.dp))

        ScheduledPaymntHeaderRow(
            plannedPayment = plannedPayment,
            categories = categories,
            accounts = accounts
        )

        Spacer(Modifier.height(16.dp))

        RuleTextRow(
            oneTime = plannedPayment.oneTime,
            startDate = plannedPayment.startDate,
            intervalN = plannedPayment.intervalN,
            intervalType = plannedPayment.intervalType
        )

        if (plannedPayment.title.isNotNullOrBlank()) {
            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = plannedPayment.title!!,
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.pureInverse
                )
            )
        }

        Spacer(Modifier.height(20.dp))

        TypeAmountCurrency(
            transactionType = plannedPayment.type,
            dueDate = null,
            currency = currency,
            amount = plannedPayment.amount
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ScheduledPaymntHeaderRow(
    plannedPayment: PlannedPaymentRule,
    categories: ImmutableList<Category>,
    accounts: ImmutableList<Account>
) {
    val nav = navigation()

    if (plannedPayment.type != TransactionType.TRANSFER) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(20.dp))

            mysaveIcon(
                modifier = Modifier
                    .background(UI.colors.pure, CircleShape),
                icon = R.drawable.ic_planned_payments,
                tint = UI.colors.pureInverse
            )

            Spacer(Modifier.width(12.dp))

            val category =
                plannedPayment.categoryId?.let { targetId -> categories.find { it.id.value == targetId } }
            if (category != null) {
                MysaveButton(
                    iconTint = findContrastTextColor(category.color.value.toComposeColor()),
                    iconStart = getCustomIconIdS(
                        category.icon?.id,
                        R.drawable.ic_custom_category_s
                    ),
                    text = category.name.value,
                    backgroundGradient = Gradient.solid(category.color.value.toComposeColor()),
                    textStyle = UI.typo.c.style(
                        color = findContrastTextColor(category.color.value.toComposeColor()),
                        fontWeight = FontWeight.ExtraBold
                    ),
                    padding = 8.dp,
                    iconEdgePadding = 10.dp
                ) {
                    nav.navigateTo(
                        TransactScrin(
                            accountId = null,
                            categoryId = category.id.value
                        )
                    )
                }

                Spacer(Modifier.width(12.dp))
            }

            val account = accounts.find { it.id == plannedPayment.accountId }
            MysaveButton(
                backgroundGradient = Gradient.solid(UI.colors.pure),
                text = account?.name ?: stringResource(R.string.deleted),
                iconTint = UI.colors.pureInverse,
                iconStart = getCustomIconIdS(account?.icon, R.drawable.ic_custom_account_s),
                textStyle = UI.typo.c.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                ),
                padding = 8.dp,
                iconEdgePadding = 10.dp
            ) {
                account?.let {
                    nav.navigateTo(
                        TransactScrin(
                            accountId = account.id,
                            categoryId = null
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun RuleTextRow(
    oneTime: Boolean,
    startDate: LocalDateTime?,
    intervalN: Int?,
    intervalType: IntervalType?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        if (oneTime) {
            Text(
                text = stringResource(R.string.planned_for_uppercase),
                style = UI.typo.nC.style(
                    color = Orange,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                modifier = Modifier.padding(bottom = 1.dp),
                text = startDate?.toLocalDate()?.formatDateOnlyWithYear()?.uppercaseLocal()
                    ?: stringResource(R.string.null_text),
                style = UI.typo.nC.style(
                    color = Orange,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        } else {
            val startDateFormatted = startDate?.toLocalDate()?.formatDateOnly()?.uppercaseLocal()
            Text(
                text = stringResource(R.string.start_date, startDateFormatted ?: ""),
                style = UI.typo.nC.style(
                    color = Orange,
                    fontWeight = FontWeight.SemiBold
                )
            )
            val intervalTypeFormatted = intervalType?.forDisplay(intervalN ?: 0)?.uppercaseLocal()
            Text(
                modifier = Modifier.padding(bottom = 1.dp),
                text = stringResource(
                    R.string.repeats_every,
                    intervalN ?: 0,
                    intervalTypeFormatted ?: ""
                ),
                style = UI.typo.nC.style(
                    color = Orange,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.width(24.dp))
    }
}

@Preview
@Composable
private fun Preview_oneTime() {
    MySavePreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = Account(name = "Cash", Green.toArgb())
            val food = Category(
                name = NotBlankTrimmedString.unsafe("Food"),
                color = ColorInt(Blue.toArgb()),
                icon = null,
                id = CategoryId(UUID.randomUUID()),
                orderNum = 0.0,
            )

            item {
                Spacer(Modifier.height(68.dp))

                ScheduledPaymntKard(
                    baseCurrency = "BGN",
                    categories = persistentListOf(food),
                    accounts = persistentListOf(cash),
                    plannedPayment = PlannedPaymentRule(
                        accountId = cash.id,
                        title = "Lidl pazar",
                        categoryId = food.id.value,
                        amount = 250.75,
                        startDate = timeNowUTC().plusDays(5),
                        oneTime = true,
                        intervalType = null,
                        intervalN = null,
                        type = TransactionType.EXPENSE
                    )
                ) {}
            }
        }
    }
}

@Preview
@Composable
private fun Preview_recurring() {
    MySavePreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val account = Account(name = "Revolut", Blue.toArgb())
            val shisha = Category(
                name = NotBlankTrimmedString.unsafe("Shisha"),
                color = ColorInt(Orange.toArgb()),
                icon = null,
                id = CategoryId(UUID.randomUUID()),
                orderNum = 0.0,
            )

            item {
                Spacer(Modifier.height(68.dp))

                ScheduledPaymntKard(
                    baseCurrency = "BGN",
                    categories = persistentListOf(shisha),
                    accounts = persistentListOf(account),
                    plannedPayment = PlannedPaymentRule(
                        accountId = account.id,
                        title = "Tabu",
                        categoryId = shisha.id.value,
                        amount = 250.75,
                        startDate = timeNowUTC().plusDays(5),
                        oneTime = false,
                        intervalType = IntervalType.MONTH,
                        intervalN = 1,
                        type = TransactionType.EXPENSE
                    )
                ) {}
            }
        }
    }
}

@Preview
@Composable
private fun Preview_recurringError() {
    MySavePreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val account = Account(name = "Revolut", Blue.toArgb())
            val shisha = Category(
                name = NotBlankTrimmedString.unsafe("Shisha"),
                color = ColorInt(Orange.toArgb()),
                icon = null,
                id = CategoryId(UUID.randomUUID()),
                orderNum = 0.0,
            )

            item {
                Spacer(Modifier.height(68.dp))

                ScheduledPaymntKard(
                    baseCurrency = "BGN",
                    categories = persistentListOf(shisha),
                    accounts = persistentListOf(account),
                    plannedPayment = PlannedPaymentRule(
                        accountId = account.id,
                        title = "Tabu",
                        categoryId = shisha.id.value,
                        amount = 250.75,
                        startDate = timeNowUTC().plusDays(5),
                        oneTime = false,
                        intervalType = null,
                        intervalN = null,
                        type = TransactionType.EXPENSE
                    ),
                ) {}
            }
        }
    }
}
