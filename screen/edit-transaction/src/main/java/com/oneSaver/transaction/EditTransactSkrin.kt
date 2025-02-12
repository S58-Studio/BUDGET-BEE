package com.oneSaver.transaction

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.base.legacy.Theme
import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.Tag
import com.oneSaver.data.model.TagId
import com.oneSaver.design.l0_system.Orange
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.design.utils.hideKeyboard
import com.oneSaver.legacy.MySavePreview
import com.oneSaver.legacy.data.EditTransactionDisplayLoan
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.rootView
import com.oneSaver.legacy.ui.component.edit.TransactionDateTime
import com.oneSaver.legacy.ui.component.tags.AddTagButton
import com.oneSaver.legacy.ui.component.tags.ShowTagModal
import com.oneSaver.legacy.utils.convertUTCtoLocal
import com.oneSaver.legacy.utils.onScreenStart
import com.oneSaver.navigation.ModifyScheduledSkrin
import com.oneSaver.navigation.ModifyTransactionSkrin
import com.oneSaver.navigation.MylonPreview
import com.oneSaver.navigation.navigation
import com.oneSaver.navigation.screenScopedViewModel
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.domain.data.CustomExchangeRateState
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateAccountData
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateCategoryData
import com.oneSaver.allStatus.userInterface.edit.core.Category
import com.oneSaver.legacy.ui.component.edit.core.Description
import com.oneSaver.legacy.domain.data.MysaveCurrency
import com.oneSaver.legacy.mySaveCtx
import com.oneSaver.allStatus.userInterface.edit.core.DueDate
import com.oneSaver.allStatus.userInterface.edit.core.EditBottomSheet
import com.oneSaver.allStatus.userInterface.edit.core.Title
import com.oneSaver.allStatus.userInterface.edit.core.Toolbar
import com.oneSaver.allStatus.userInterface.theme.components.AddPrimaryAttributeButton
import com.oneSaver.allStatus.userInterface.theme.components.ChangeTransactionTypeModal
import com.oneSaver.allStatus.userInterface.theme.components.CustomExchangeRateCard
import com.oneSaver.allStatus.userInterface.theme.modal.DeleteModal
import com.oneSaver.legacy.legacyOld.ui.theme.modal.ModalAdd
import com.oneSaver.legacy.legacyOld.ui.theme.modal.ModalCheck
import com.oneSaver.legacy.legacyOld.ui.theme.modal.ModalSave
import com.oneSaver.allStatus.userInterface.theme.modal.ProgressModal
import com.oneSaver.allStatus.userInterface.theme.modal.edit.AccountModal
import com.oneSaver.allStatus.userInterface.theme.modal.edit.AccountModalData
import com.oneSaver.allStatus.userInterface.theme.modal.edit.AmountModal
import com.oneSaver.allStatus.userInterface.theme.modal.edit.CategoryModal
import com.oneSaver.allStatus.userInterface.theme.modal.edit.CategoryModalData
import com.oneSaver.allStatus.userInterface.theme.modal.edit.ChooseCategoryModal
import com.oneSaver.allStatus.userInterface.theme.modal.edit.DescriptionModal
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import kotlin.math.roundToInt

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.EditTransactionScreen(screen: ModifyTransactionSkrin) {
    val viewModel: EditTransactsVM = screenScopedViewModel()
    val uiState = viewModel.uiState()

    LaunchedEffect(Unit) {
        viewModel.start(screen)
    }

    val view = rootView()

    UI(
        screen = screen,
        transactionType = uiState.transactionType,
        baseCurrency = uiState.currency,
        initialTitle = uiState.initialTitle,
        titleSuggestions = uiState.titleSuggestions,
        description = uiState.description,
        dateTime = uiState.dateTime,
        category = uiState.category,
        account = uiState.account,
        toAccount = uiState.toAccount,
        dueDate = uiState.dueDate,
        amount = uiState.amount,
        loanData = uiState.displayLoanHelper,
        backgroundProcessing = uiState.backgroundProcessingStarted,
        customExchangeRateState = uiState.customExchangeRateState,

        categories = uiState.categories,
        accounts = uiState.accounts,
        tags = uiState.tags,
        transactionAssociatedTags = uiState.transactionAssociatedTags,
        hasChanges = uiState.hasChanges,
        onSetDate = {
            viewModel.onEvent(EditTransactEventi.OnSetDate(it))
        },
        onSetTime = {
            viewModel.onEvent(EditTransactEventi.OnSetTime(it))
        },
        onTitleChange = {
            viewModel.onEvent(EditTransactEventi.OnTitleChanged(it))
        },
        onDescriptionChange = {
            viewModel.onEvent(EditTransactEventi.OnDescriptionChanged(it))
        },
        onAmountChange = {
            viewModel.onEvent(EditTransactEventi.OnAmountChanged(it))
        },
        onCategoryChange = {
            viewModel.onEvent(EditTransactEventi.OnCategoryChanged(it))
        },
        onAccountChange = {
            viewModel.onEvent(EditTransactEventi.OnAccountChanged(it))
        },
        onToAccountChange = {
            viewModel.onEvent(EditTransactEventi.OnToAccountChanged(it))
        },
        onDueDateChange = {
            viewModel.onEvent(EditTransactEventi.OnDueDateChanged(it))
        },
        onSetTransactionType = {
            viewModel.onEvent(EditTransactEventi.OnSetTransactionType(it))
        },
        onCreateCategory = {
            viewModel.onEvent(EditTransactEventi.CreateCategory(it))
        },
        onEditCategory = {
            viewModel.onEvent(EditTransactEventi.EditCategory(it))
        },
        onPayPlannedPayment = {
            viewModel.onEvent(EditTransactEventi.OnPayPlannedPayment)
        },
        onSave = {
            view.hideKeyboard()
            viewModel.onEvent(EditTransactEventi.Save(it))
        },
        onSetHasChanges = {
            viewModel.onEvent(EditTransactEventi.SetHasChanges(it))
        },
        onDelete = {
            viewModel.onEvent(EditTransactEventi.Delete)
        },
        onDuplicate = {
            viewModel.onEvent(EditTransactEventi.Duplicate)
        },
        onCreateAccount = {
            viewModel.onEvent(EditTransactEventi.CreateAccount(it))
        },
        onExchangeRateChange = {
            viewModel.onEvent(EditTransactEventi.UpdateExchangeRate(it))
        },
        onTagOperation = {
            viewModel.onEvent(it)
        }
    )
}

@Suppress("LongParameterList", "LongMethod", "CyclomaticComplexMethod")
@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: ModifyTransactionSkrin,
    transactionType: TransactionType,
    baseCurrency: String,
    initialTitle: String?,
    titleSuggestions: ImmutableSet<String>,
    description: String?,
    category: Category?,
    dateTime: LocalDateTime?,
    account: Account?,
    toAccount: Account?,
    dueDate: LocalDateTime?,
    amount: Double,

    customExchangeRateState: CustomExchangeRateState,
    categories: ImmutableList<Category>,
    accounts: ImmutableList<Account>,
    tags: ImmutableList<Tag>,
    transactionAssociatedTags: ImmutableList<TagId>,
    onTitleChange: (String?) -> Unit,
    onDescriptionChange: (String?) -> Unit,
    onAmountChange: (Double) -> Unit,
    onCategoryChange: (Category?) -> Unit,
    onAccountChange: (Account) -> Unit,
    onToAccountChange: (Account) -> Unit,
    onDueDateChange: (LocalDateTime?) -> Unit,
    onSetDate: (LocalDate) -> Unit,
    onSetTime: (LocalTime) -> Unit,
    onSetTransactionType: (TransactionType) -> Unit,

    onCreateCategory: (CreateCategoryData) -> Unit,
    onEditCategory: (Category) -> Unit,
    onPayPlannedPayment: () -> Unit,
    onSave: (closeScreen: Boolean) -> Unit,
    onSetHasChanges: (hasChanges: Boolean) -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onCreateAccount: (CreateAccountData) -> Unit,
    onExchangeRateChange: (Double?) -> Unit = { },
    onTagOperation: (EditTransactEventi.TagEvent) -> Unit = {},
    loanData: EditTransactionDisplayLoan = EditTransactionDisplayLoan(),
    backgroundProcessing: Boolean = false,
    hasChanges: Boolean = false,

    ) {
    var chooseCategoryModalVisible by remember { mutableStateOf(false) }
    var tagModelVisible by remember { mutableStateOf(false) }
    var categoryModalData: CategoryModalData? by remember { mutableStateOf(null) }
    var accountModalData: AccountModalData? by remember { mutableStateOf(null) }
    var descriptionModalVisible by remember { mutableStateOf(false) }
    var deleteTrnModalVisible by remember { mutableStateOf(false) }
    var changeTransactionTypeModalVisible by remember { mutableStateOf(false) }
    var amountModalShown by remember { mutableStateOf(false) }
    var exchangeRateAmountModalShown by remember { mutableStateOf(false) }
    var accountChangeModal by remember { mutableStateOf(false) }
    val waitModalVisible by remember(backgroundProcessing) {
        mutableStateOf(backgroundProcessing)
    }
    var selectedAcc by remember(account) {
        mutableStateOf(account)
    }

    val amountModalId =
        remember(screen.initialTransactionId, customExchangeRateState.exchangeRate) {
            UUID.randomUUID()
        }

    var titleTextFieldValue by remember(initialTitle) {
        mutableStateOf(
            TextFieldValue(
                initialTitle ?: ""
            )
        )
    }
    val titleFocus = FocusRequester()
    val scrollState = rememberScrollState()

    // This is to scroll the column to the customExchangeCard composable when it is shown
    var customExchangeRatePosition by remember { mutableFloatStateOf(0F) }
    LaunchedEffect(key1 = customExchangeRateState.showCard) {
        val scrollInt =
            if (customExchangeRateState.showCard) customExchangeRatePosition.roundToInt() else 0
        scrollState.animateScrollTo(scrollInt)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
    ) {
        Spacer(Modifier.height(16.dp))

        Toolbar(
            // Setting the transaction type to TransactionType.TRANSFER for transfers associated
            // with loan record to hide the ChangeTransactionType Button
            type = if (loanData.isLoanRecord) TransactionType.TRANSFER else transactionType,
            initialTransactionId = screen.initialTransactionId,
            onDeleteTrnModal = {
                deleteTrnModalVisible = true
            },
            onChangeTransactionTypeModal = {
                changeTransactionTypeModalVisible = true
            },
            showDuplicateButton = true,
            onDuplicate = onDuplicate
        )

        Spacer(Modifier.height(32.dp))

        Title(
            type = transactionType,
            titleFocus = titleFocus,
            initialTransactionId = screen.initialTransactionId,

            titleTextFieldValue = titleTextFieldValue,
            setTitleTextFieldValue = {
                titleTextFieldValue = it
            },
            suggestions = titleSuggestions,
            scrollState = scrollState,

            onTitleChanged = onTitleChange,
            onNext = {
                when {
                    shouldFocusAmount(amount = amount) -> {
                        amountModalShown = true
                    }

                    else -> {
                        onSave(true)
                    }
                }
            }
        )

        if (loanData.loanCaption != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = loanData.loanCaption!!,
                style = UI.typo.nB2.style(
                    color = UI.colors.mediumInverse,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        Spacer(Modifier.height(32.dp))

        Category(category = category, onChooseCategory = {
            chooseCategoryModalVisible = true
        })

        Spacer(Modifier.height(16.dp))

        AddTagButton(transactionAssociatedTags = transactionAssociatedTags, onClick = {
            tagModelVisible = true
        })

        Spacer(Modifier.height(32.dp))

        val mysaveContext = mySaveCtx()

        if (dueDate != null) {
            DueDate(dueDate = dueDate) {
                mysaveContext.datePicker(
                    initialDate = dueDate.toLocalDate()
                ) {
                    onDueDateChange(it.atTime(12, 0))
                }
            }

            Spacer(Modifier.height(12.dp))
        }

        Description(
            description = description,
            onAddDescription = { descriptionModalVisible = true },
            onEditDescription = { descriptionModalVisible = true }
        )

        TransactionDateTime(
            dateTime = dateTime,
            dueDateTime = dueDate,
            onEditDate = {
                mysaveContext.datePicker(
                    initialDate = dateTime?.convertUTCtoLocal()?.toLocalDate()
                ) { date ->
                    onSetDate((date))
                }
            },
            onEditTime = {
                mysaveContext.timePicker { time ->
                    onSetTime(time)
                }
            }
        )

        if (transactionType == TransactionType.TRANSFER && customExchangeRateState.showCard) {
            Spacer(Modifier.height(12.dp))
            CustomExchangeRateCard(
                fromCurrencyCode = baseCurrency,
                toCurrencyCode = customExchangeRateState.toCurrencyCode ?: baseCurrency,
                exchangeRate = customExchangeRateState.exchangeRate,
                onRefresh = {
                    // Set exchangeRate to null to reset
                    onExchangeRateChange(null)
                },
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    customExchangeRatePosition = coordinates.positionInParent().y * 0.3f
                }
            ) {
                exchangeRateAmountModalShown = true
            }
        }

        if (dueDate == null && transactionType != TransactionType.TRANSFER && dateTime == null) {
            Spacer(Modifier.height(12.dp))

            val nav = navigation()
            AddPrimaryAttributeButton(
                icon = R.drawable.ic_planned_payments,
                text = stringResource(R.string.add_planned_date_payment),
                onClick = {
                    nav.back()
                    nav.navigateTo(
                        ModifyScheduledSkrin(
                            plannedPaymentRuleId = null,
                            type = transactionType,
                            amount = amount,
                            accountId = account?.id,
                            categoryId = category?.id?.value,
                            title = titleTextFieldValue.text,
                            description = description,
                        )
                    )
                }
            )
        }

        Spacer(Modifier.height(600.dp)) // scroll hack
    }

    onScreenStart {
        if (screen.initialTransactionId == null) {
            amountModalShown = true
        }
    }

    EditBottomSheet(
        initialTransactionId = screen.initialTransactionId,
        type = transactionType,
        accounts = accounts,
        selectedAccount = account,
        toAccount = toAccount,
        amount = amount,
        currency = baseCurrency,
        convertedAmount = customExchangeRateState.convertedAmount,
        convertedAmountCurrencyCode = customExchangeRateState.toCurrencyCode,

        ActionButton = {
            if (screen.initialTransactionId != null) {
                // Edit mode
                if (dueDate != null) {
                    // due date stuff
                    if (hasChanges) {
                        // has changes
                        ModalSave {
                            onSave(false)
                            onSetHasChanges(false)
                        }
                    } else {
                        // no changes, pay
                        ModalCheck(
                            label = if (transactionType == TransactionType.EXPENSE) {
                                stringResource(
                                    R.string.pay
                                )
                            } else {
                                stringResource(R.string.get)
                            }
                        ) {
                            onPayPlannedPayment()
                        }
                    }
                } else {
                    // normal transaction
                    ModalSave {
                        onSave(true)
                    }
                }
            } else {
                // create new mode
                ModalAdd {
                    onSave(true)
                }
            }
        },

        amountModalShown = amountModalShown,
        setAmountModalShown = {
            amountModalShown = it
        },

        onAmountChanged = {
            onAmountChange(it)
            if (shouldFocusCategory(category)) {
                chooseCategoryModalVisible = true
            } else if (shouldFocusTitle(titleTextFieldValue, transactionType)) {
                titleFocus.requestFocus()
            }
        },
        onSelectedAccountChanged = {
            if (loanData.isLoan && account?.currency != it.currency) {
                selectedAcc = it
                accountChangeModal = true
            } else {
                onAccountChange(it)
            }
        },
        onToAccountChanged = onToAccountChange,
        onAddNewAccount = {
            accountModalData = AccountModalData(
                account = null, baseCurrency = baseCurrency, balance = 0.0
            )
        }
    )

    // Modals
    ChooseCategoryModal(
        visible = chooseCategoryModalVisible,
        initialCategory = category,
        categories = categories,
        showCategoryModal = { categoryModalData = CategoryModalData(it) },
        onCategoryChanged = {
            onCategoryChange(it)
            if (shouldFocusTitle(titleTextFieldValue, transactionType)) {
                titleFocus.requestFocus()
            } else if (shouldFocusAmount(amount = amount)) {
                amountModalShown = true
            }
        },
        dismiss = {
            chooseCategoryModalVisible = false
        }
    )

    CategoryModal(modal = categoryModalData, onCreateCategory = { createData ->
        onCreateCategory(createData)
        chooseCategoryModalVisible = false
    }, onEditCategory = onEditCategory, dismiss = {
        categoryModalData = null
    })

    AccountModal(
        modal = accountModalData,
        onCreateAccount = onCreateAccount,
        onEditAccount = { _, _ -> },
        dismiss = {
            accountModalData = null
        }
    )

    DescriptionModal(
        visible = descriptionModalVisible,
        description = description,
        onDescriptionChanged = onDescriptionChange,
        dismiss = {
            descriptionModalVisible = false
        }
    )

    DeleteModal(
        visible = deleteTrnModalVisible,
        title = stringResource(R.string.deletion_confirmation),
        description = stringResource(R.string.transaction_confirm_deletion_description),
        dismiss = { deleteTrnModalVisible = false }
    ) {
        onDelete()
    }

    ChangeTransactionTypeModal(
        visible = changeTransactionTypeModalVisible,
        includeTransferType = true,
        initialType = transactionType,
        dismiss = {
            changeTransactionTypeModalVisible = false
        }
    ) {
        onSetTransactionType(it)
    }

    DeleteModal(
        visible = accountChangeModal,
        title = stringResource(R.string.confirm_account_change),
        description = stringResource(R.string.confirm_account_change_description),
        buttonText = stringResource(R.string.confirm),
        iconStart = R.drawable.ic_agreed,
        dismiss = {
            accountChangeModal = false
        }
    ) {
        selectedAcc?.let { onAccountChange(it) }
        accountChangeModal = false
    }

    ProgressModal(
        title = stringResource(R.string.confirm_account_change),
        description = stringResource(R.string.confirm_account_loan_change),
        visible = waitModalVisible
    )

    AmountModal(
        id = amountModalId,
        visible = exchangeRateAmountModalShown,
        currency = "",
        initialAmount = customExchangeRateState.exchangeRate,
        dismiss = { exchangeRateAmountModalShown = false },
        decimalCountMax = MysaveCurrency.getDecimalPlaces(
            customExchangeRateState.toCurrencyCode ?: baseCurrency
        ),
        onAmountChanged = {
            onExchangeRateChange(it)
        }
    )

    ShowTagModal(
        visible = tagModelVisible,
        onDismiss = {
            tagModelVisible = false
            // Reset TagList, avoids showing incorrect tag list when user has searched for a tag
            onTagOperation(EditTransactEventi.TagEvent.OnTagSearch(""))
        },
        allTagList = tags,
        selectedTagList = transactionAssociatedTags,
        onTagAdd = {
            onTagOperation(EditTransactEventi.TagEvent.SaveTag(name = it))
        },
        onTagEdit = { oldTag, newTag ->
            onTagOperation(EditTransactEventi.TagEvent.OnTagEdit(oldTag, newTag))
        },
        onTagDelete = {
            onTagOperation(EditTransactEventi.TagEvent.OnTagDelete(it))
        },
        onTagSelected = {
            onTagOperation(EditTransactEventi.TagEvent.OnTagSelect(it))
        },
        onTagDeSelected = {
            onTagOperation(EditTransactEventi.TagEvent.OnTagDeSelect(it))
        },
        onTagSearch = {
            onTagOperation(EditTransactEventi.TagEvent.OnTagSearch(it))
        }
    )
}

private fun shouldFocusCategory(
    category: Category?,
): Boolean = category == null

private fun shouldFocusTitle(
    titleTextFieldValue: TextFieldValue,
    type: TransactionType
): Boolean = titleTextFieldValue.text.isBlank() && type != TransactionType.TRANSFER

private fun shouldFocusAmount(amount: Double) = amount == 0.0

/** For Preview purpose **/
private val testDateTime = LocalDateTime.of(2023, 4, 27, 0, 35)

@ExperimentalFoundationApi
@Preview
@Composable
private fun BoxWithConstraintsScope.Preview(isDark: Boolean = false) {
    MylonPreview(isDark) {
        UI(
            screen = ModifyTransactionSkrin(null, TransactionType.EXPENSE),
            initialTitle = "",
            titleSuggestions = persistentSetOf(),
            tags = persistentListOf(),
            transactionAssociatedTags = persistentListOf(),
            baseCurrency = "BGN",
            dateTime = testDateTime,
            description = null,
            category = null,
            account = Account(name = "phyre", Orange.toArgb()),
            toAccount = null,
            amount = 0.0,
            dueDate = null,
            transactionType = TransactionType.INCOME,
            customExchangeRateState = CustomExchangeRateState(),

            categories = persistentListOf(),
            accounts = persistentListOf(),

            onDueDateChange = {},
            onCategoryChange = {},
            onAccountChange = {},
            onToAccountChange = {},
            onDescriptionChange = {},
            onTitleChange = {},
            onAmountChange = {},

            onCreateCategory = { },
            onEditCategory = {},
            onPayPlannedPayment = {},
            onSave = {},
            onSetHasChanges = {},
            onDelete = {},
            onDuplicate = {},
            onCreateAccount = { },
            onSetDate = {},
            onSetTime = {},
            onSetTransactionType = {}
        )
    }
}

/** For screenshot testing */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditTransactionScreenUiTest(isDark: Boolean) {
    val theme = when (isDark) {
        true -> Theme.DARK
        false -> Theme.LIGHT
    }
    MySavePreview(theme) {
        Preview(isDark)
    }
}
