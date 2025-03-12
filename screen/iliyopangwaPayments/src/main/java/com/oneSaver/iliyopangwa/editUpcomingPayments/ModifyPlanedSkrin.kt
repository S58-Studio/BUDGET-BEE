package com.oneSaver.iliyopangwa.editUpcomingPayments

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.IntervalType
import com.oneSaver.legacy.MySavePreview
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.utils.onScreenStart
import com.oneSaver.navigation.EditPlannedScreen
import com.oneSaver.navigation.screenScopedViewModel
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.edit.core.Category
import com.oneSaver.legacy.ui.component.edit.core.Description
import com.oneSaver.allStatus.userInterface.edit.core.EditBottomSheet
import com.oneSaver.allStatus.userInterface.edit.core.Title
import com.oneSaver.allStatus.userInterface.edit.core.Toolbar
import com.oneSaver.allStatus.userInterface.theme.Orange
import com.oneSaver.allStatus.userInterface.theme.components.ChangeTransactionTypeModal
import com.oneSaver.allStatus.userInterface.theme.modal.DeleteModal
import com.oneSaver.legacy.legacyOld.ui.theme.modal.ModalSet
import com.oneSaver.allStatus.userInterface.theme.modal.RecurringRuleModal
import com.oneSaver.allStatus.userInterface.theme.modal.RecurringRuleModalData
import com.oneSaver.allStatus.userInterface.theme.modal.edit.AccountModal
import com.oneSaver.allStatus.userInterface.theme.modal.edit.AccountModalData
import com.oneSaver.allStatus.userInterface.theme.modal.edit.CategoryModal
import com.oneSaver.allStatus.userInterface.theme.modal.edit.CategoryModalData
import com.oneSaver.allStatus.userInterface.theme.modal.edit.ChooseCategoryModal
import com.oneSaver.allStatus.userInterface.theme.modal.edit.DescriptionModal
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDateTime

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ModifyPlanedSkrin(screen: EditPlannedScreen) {
    val viewModel: EditPlannedViewModel = screenScopedViewModel()
    val uiState = viewModel.uiState()
    LaunchedEffect(Unit) {
        viewModel.start(screen)
    }

    UI(
        screen = screen,
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

/**
 * Flow Empty: Type -> Amount -> Category -> Recurring Rule -> Title
 * Flow Amount + Category: Recurring Rule -> Title
 */
@Suppress("LongMethod")
@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: EditPlannedScreen,
    state: EditPlannedScreenState,
    onEvent: (EditPlannedScreenEvents) -> Unit,
) {
    var titleTextFieldValue by remember(state.initialTitle) {
        mutableStateOf(
            TextFieldValue(
                state.initialTitle.orEmpty()
            )
        )
    }
    val titleFocus = FocusRequester()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(16.dp))

        Toolbar(
            type = state.transactionType,
            initialTransactionId = screen.plannedPaymentRuleId,
            onDeleteTrnModal = {
                onEvent(EditPlannedScreenEvents.OnDeleteTransactionModalVisible(true))
            },
            onChangeTransactionTypeModal = {
                onEvent(EditPlannedScreenEvents.OnTransactionTypeModalVisible(true))
            },
            showDuplicateButton = false,
            onDuplicate = {}
        )

        Spacer(Modifier.height(32.dp))

        Title(
            type = state.transactionType,
            titleFocus = titleFocus,
            initialTransactionId = screen.plannedPaymentRuleId,

            titleTextFieldValue = titleTextFieldValue,
            setTitleTextFieldValue = {
                titleTextFieldValue = it
            },
            suggestions = emptySet(), // DO NOT display title suggestions for "Planned Payments"

            onTitleChanged = { onEvent(EditPlannedScreenEvents.OnTitleChanged(it)) },
            onNext = {
                when {
                    shouldFocusRecurring(
                        state.startDate,
                        state.intervalN,
                        state.intervalType,
                        state.oneTime
                    ) -> {
                        onEvent(
                            EditPlannedScreenEvents.OnRecurringRuleModalDataChanged(
                                RecurringRuleModalData(
                                    initialStartDate = state.startDate,
                                    initialIntervalN = state.intervalN,
                                    initialIntervalType = state.intervalType,
                                    initialOneTime = state.oneTime
                                )
                            )
                        )
                    }

                    else -> {
                        onEvent(EditPlannedScreenEvents.OnSave())
                    }
                }
            }
        )

        if (state.transactionType != TransactionType.TRANSFER) {
            Spacer(Modifier.height(32.dp))

            Category(
                category = state.category,
                onChooseCategory = {
                    onEvent(EditPlannedScreenEvents.OnCategoryModalVisible(true))
                }
            )
        }

        Spacer(Modifier.height(32.dp))

        RecurringRule(
            startDate = state.startDate,
            intervalN = state.intervalN,
            intervalType = state.intervalType,
            oneTime = state.oneTime,
            onShowRecurringRuleModal = {
                onEvent(
                    EditPlannedScreenEvents.OnRecurringRuleModalDataChanged(
                        RecurringRuleModalData(
                            initialStartDate = state.startDate,
                            initialIntervalN = state.intervalN,
                            initialIntervalType = state.intervalType,
                            initialOneTime = state.oneTime
                        )
                    )
                )
            }
        )

        Spacer(Modifier.height(12.dp))

        Description(
            description = state.description,
            onAddDescription = { onEvent(EditPlannedScreenEvents.OnDescriptionModalVisible(true)) },
            onEditDescription = { onEvent(EditPlannedScreenEvents.OnDescriptionModalVisible(true)) }
        )

        Spacer(Modifier.height(600.dp)) // scroll hack
    }

    onScreenStart {
        if (screen.plannedPaymentRuleId == null) {
            // Create mode
            if (screen.mandatoryFilled()) {
                // Flow Convert (Amount, Account, Category)
                onEvent(
                    EditPlannedScreenEvents.OnRecurringRuleModalDataChanged(
                        RecurringRuleModalData(
                            initialStartDate = state.startDate,
                            initialIntervalN = state.intervalN,
                            initialIntervalType = state.intervalType,
                            initialOneTime = state.oneTime
                        )
                    )
                )
            } else {
                // Flow Empty
                onEvent(EditPlannedScreenEvents.OnTransactionTypeModalVisible(true))
            }
        }
    }

    EditBottomSheet(
        initialTransactionId = screen.plannedPaymentRuleId,
        type = state.transactionType,
        accounts = state.accounts,
        selectedAccount = state.account,
        toAccount = null,
        amount = state.amount,
        currency = state.currency,

        ActionButton = {
            ModalSet(
                modifier = Modifier.testTag("editPlannedScreen_set")
            ) {
                onEvent(EditPlannedScreenEvents.OnSave())
            }
        },

        amountModalShown = state.amountModalVisible,
        setAmountModalShown = {
            onEvent(EditPlannedScreenEvents.OnAmountModalVisible(it))
        },

        onAmountChanged = {
            onEvent(EditPlannedScreenEvents.OnAmountChanged(it))
            when {
                shouldFocusCategory(state.category, state.transactionType) -> {
                    onEvent(EditPlannedScreenEvents.OnCategoryModalVisible(true))
                }

                shouldFocusRecurring(
                    state.startDate,
                    state.intervalN,
                    state.intervalType,
                    state.oneTime
                ) -> {
                    onEvent(
                        EditPlannedScreenEvents.OnRecurringRuleModalDataChanged(
                            RecurringRuleModalData(
                                initialStartDate = state.startDate,
                                initialIntervalN = state.intervalN,
                                initialIntervalType = state.intervalType,
                                initialOneTime = state.oneTime
                            )
                        )
                    )
                }

                shouldFocusTitle(titleTextFieldValue, state.transactionType) -> {
                    titleFocus.requestFocus()
                }
            }
        },
        onSelectedAccountChanged = { onEvent(EditPlannedScreenEvents.OnAccountChanged(it)) },
        onToAccountChanged = { },
        onAddNewAccount = {
            onEvent(
                EditPlannedScreenEvents.OnAccountModalDataChanged(
                    AccountModalData(
                        account = null,
                        baseCurrency = state.currency,
                        balance = 0.0
                    )
                )
            )
        }
    )

    // Modals
    ChooseCategoryModal(
        visible = state.categoryModalVisible,
        initialCategory = state.category,
        categories = state.categories,
        showCategoryModal = {
            onEvent(
                EditPlannedScreenEvents.OnCategoryModalDataChanged(
                    CategoryModalData(it)
                )
            )
        },
        onCategoryChanged = {
            onEvent(EditPlannedScreenEvents.OnCategoryChanged(it))
            onEvent(
                EditPlannedScreenEvents.OnRecurringRuleModalDataChanged(
                    RecurringRuleModalData(
                        initialStartDate = state.startDate,
                        initialIntervalN = state.intervalN,
                        initialIntervalType = state.intervalType,
                        initialOneTime = state.oneTime
                    )
                )
            )
        },
        dismiss = {
            onEvent(EditPlannedScreenEvents.OnCategoryModalVisible(false))
        }
    )

    CategoryModal(
        modal = state.categoryModalData,
        onCreateCategory = { onEvent(EditPlannedScreenEvents.OnCreateCategory(it)) },
        onEditCategory = {
            onEvent(EditPlannedScreenEvents.OnEditCategory(it))
        },
        dismiss = {
            onEvent(EditPlannedScreenEvents.OnCategoryModalDataChanged(null))
        }
    )

    AccountModal(
        modal = state.accountModalData,
        onCreateAccount = { onEvent(EditPlannedScreenEvents.OnCreateAccount(it)) },
        onEditAccount = { _, _ -> },
        dismiss = {
            onEvent(EditPlannedScreenEvents.OnAccountModalDataChanged(null))
        }
    )

    DescriptionModal(
        visible = state.descriptionModalVisible,
        description = state.description,
        onDescriptionChanged = { onEvent(EditPlannedScreenEvents.OnDescriptionChanged(it)) },
        dismiss = {
            onEvent(EditPlannedScreenEvents.OnDescriptionModalVisible(false))
        }
    )

    DeleteModal(
        visible = state.deleteTransactionModalVisible,
        title = stringResource(R.string.deletion_confirmation),
        description = stringResource(R.string.planned_payment_confirm_deletion_description),
        dismiss = { onEvent(EditPlannedScreenEvents.OnDeleteTransactionModalVisible(false)) }
    ) {
        onEvent(EditPlannedScreenEvents.OnDelete)
    }

    ChangeTransactionTypeModal(
        title = stringResource(R.string.set_payment_type),
        visible = state.transactionTypeModalVisible,
        includeTransferType = false,
        initialType = state.transactionType,
        dismiss = {
            onEvent(EditPlannedScreenEvents.OnTransactionTypeModalVisible(false))
        }
    ) {
        onEvent(EditPlannedScreenEvents.OnSetTransactionType(it))
        if (shouldFocusAmount(state.amount)) {
            onEvent(EditPlannedScreenEvents.OnAmountModalVisible(true))
        }
    }

    RecurringRuleModal(
        modal = state.recurringRuleModalData,
        onRuleChanged = { newStartDate, newOneTime, newIntervalN, newIntervalType ->
            onEvent(
                EditPlannedScreenEvents.OnRuleChanged(
                    newStartDate,
                    newOneTime,
                    newIntervalN,
                    newIntervalType
                )
            )

            when {
                shouldFocusCategory(state.category, state.transactionType) -> {
                    onEvent(EditPlannedScreenEvents.OnCategoryModalVisible(true))
                }

                shouldFocusTitle(titleTextFieldValue, state.transactionType) -> {
                    titleFocus.requestFocus()
                }
            }
        },
        dismiss = {
            onEvent(EditPlannedScreenEvents.OnRecurringRuleModalDataChanged(null))
        }
    )
}

private fun shouldFocusCategory(
    category: Category?,
    type: TransactionType,
): Boolean = category == null && type != TransactionType.TRANSFER

private fun shouldFocusTitle(
    titleTextFieldValue: TextFieldValue,
    type: TransactionType,
): Boolean = titleTextFieldValue.text.isBlank() && type != TransactionType.TRANSFER

private fun shouldFocusRecurring(
    startDate: LocalDateTime?,
    intervalN: Int?,
    intervalType: IntervalType?,
    oneTime: Boolean,
): Boolean {
    return !hasRecurringRule(
        startDate = startDate,
        intervalN = intervalN,
        intervalType = intervalType,
        oneTime = oneTime
    )
}

private fun shouldFocusAmount(amount: Double) = amount == 0.0

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    MySavePreview {
        UI(
            screen = EditPlannedScreen(null, TransactionType.EXPENSE),
            EditPlannedScreenState(
                oneTime = false,
                startDate = null,
                intervalN = null,
                intervalType = null,
                initialTitle = "",
                currency = "BGN",
                description = null,
                category = null,
                account = Account(name = "phyre", Orange.toArgb()),
                amount = 0.0,
                transactionType = TransactionType.INCOME,
                categories = persistentListOf(),
                accounts = persistentListOf(),
                categoryModalVisible = false,
                categoryModalData = null,
                accountModalData = null,
                descriptionModalVisible = false,
                deleteTransactionModalVisible = false,
                recurringRuleModalData = null,
                transactionTypeModalVisible = false,
                amountModalVisible = false
            )
        ) {}
    }
}
