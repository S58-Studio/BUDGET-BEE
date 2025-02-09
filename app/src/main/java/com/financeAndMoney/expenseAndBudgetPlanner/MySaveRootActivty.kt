package com.financeAndMoney.expenseAndBudgetPlanner

import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.messaging.FirebaseMessaging
import com.financeAndMoney.MysaveNavGraph
import com.financeAndMoney.design.api.MysaveUI
import com.financeAndMoney.domains.RootScreen
import com.financeAndMoney.home.clientJourney.ClientJourneyCardsProvider
import com.financeAndMoney.legacy.Constants
import com.financeAndMoney.legacy.MySaveCtx
import com.financeAndMoney.legacy.appDesign
import com.financeAndMoney.legacy.utils.activityForResultLauncher
import com.financeAndMoney.legacy.utils.convertLocalToUTC
import com.financeAndMoney.legacy.utils.sendToCrashlytics
import com.financeAndMoney.legacy.utils.simpleActivityForResultLauncher
import com.financeAndMoney.legacy.utils.timeNowLocal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.applocked.AppLockedScreen
import com.financeAndMoney.expenseAndBudgetPlanner.extraUtils.firebaseMySaveUpdates.ApiInterface
import com.financeAndMoney.expenseAndBudgetPlanner.extraUtils.firebaseMySaveUpdates.AppUpdates
import com.financeAndMoney.expenseAndBudgetPlanner.extraUtils.firebaseMySaveUpdates.AppUpdatesActivity
import com.financeAndMoney.expenseAndBudgetPlanner.extraUtils.firebaseMySaveUpdates.AppUpdatesDataSource
import com.financeAndMoney.expenseAndBudgetPlanner.extraUtils.firebaseMySaveUpdates.FirebaseApiClient
import com.financeAndMoney.navigation.Navigation
import com.financeAndMoney.navigation.NavigationRoot
import com.financeAndMoney.widget.mulaBalanc.WalletBalanceWidgetReceiver
import com.financeAndMoney.widget.walleTransaction.AddTransactionWidget
import com.financeAndMoney.widget.walleTransaction.AddTransactionWidgetCompact
import dagger.hilt.android.AndroidEntryPoint
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal
import com.financeAndMoney.core.userInterface.R
import android.Manifest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
@Suppress("TooManyFunctions")
class MySaveRootActivty : AppCompatActivity(), RootScreen {
    @Inject
    lateinit var mySaveContext: MySaveCtx

    @Inject
    lateinit var navigation: Navigation

    @Inject
    lateinit var customerJourneyLogic: ClientJourneyCardsProvider

    private lateinit var createFileLauncher: ActivityResultLauncher<String>
    private lateinit var onFileCreated: (fileUri: Uri) -> Unit

    private lateinit var openFileLauncher: ActivityResultLauncher<Unit>
    private lateinit var onFileOpened: (fileUri: Uri) -> Unit
    private lateinit var noInternetDialog: NoInternetDialogSignal

    private val viewModel: RootVM by viewModels()

    //Cloud notification
    private val channelId = "Mysave_Notifications_Channel"
    // Request code for POST_NOTIFICATIONS permission
    private val POST_NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

    @OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalFoundationApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setupActivityForResultLaunchers()
        //FCM
        createNotificationChannel()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Handle error
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            // Log and send the token to your backend
            Log.d("FCM", "FCM Token: $token")
        }
        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        // Make the app drawing area fullscreen (draw behind status and nav bars)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setupDatePicker()
        setupTimePicker()
        checkForAppUpdates()

        AddTransactionWidget.updateBroadcast(this)
        AddTransactionWidgetCompact.updateBroadcast(this)
        WalletBalanceWidgetReceiver.updateBroadcast(this)

        setContent {
            val viewModel: RootVM = viewModel()
            val isSystemInDarkTheme = isSystemInDarkTheme()

            LaunchedEffect(isSystemInDarkTheme) {
                viewModel.start(isSystemInDarkTheme, intent)
            }

            val appLocked by viewModel.appLocked.collectAsState()
            when (appLocked) {
                null -> {
                    // display nothing
                }

                true -> {
                    MysaveUI(
                        design = appDesign(mySaveContext)
                    ) {
                        AppLockedScreen(
                            onShowOSBiometricsModal = {
                                authenticateWithOSBiometricsModal(
                                    biometricPromptCallback = viewModel.handleBiometricAuthenticationResult()
                                )
                            },
                            onContinueWithoutAuthentication = {
                                viewModel.unlockApp()
                            }
                        )
                    }
                }

                false -> {
                    NavigationRoot(navigation = navigation) { screen ->
                        MysaveUI(
                            design = appDesign(mySaveContext),
                            includeSurface = screen?.isLegacy ?: true
                        ) {
                            MysaveNavGraph(screen,this@MySaveRootActivty)
                        }
                    }
                }
            }
        }
        // Initialize NoInternetDialogSignal
        noInternetDialog = NoInternetDialogSignal.Builder(this, lifecycle).build()

    }

    private companion object {
        private const val MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000
    }

    private fun setupDatePicker() {
        mySaveContext.onShowDatePicker = { minDate,
                                           maxDate,
                                           initialDate,
                                           onDatePicked ->
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setSelection(
                        if (initialDate != null) {
                            initialDate.toEpochDay() * MILLISECONDS_IN_DAY
                        } else {
                            MaterialDatePicker.todayInUtcMilliseconds()
                        }
                    )
                    .build()
            datePicker.show(supportFragmentManager, "datePicker")
            datePicker.addOnPositiveButtonClickListener {
                onDatePicked(LocalDate.ofEpochDay(it / MILLISECONDS_IN_DAY))
            }

            if (minDate != null) {
                datePicker.addOnCancelListener {
                    onDatePicked(minDate)
                }
            }

            if (maxDate != null) {
                datePicker.addOnCancelListener {
                    onDatePicked(maxDate)
                }
            }

            if (initialDate != null) {
                datePicker.addOnCancelListener {
                    onDatePicked(initialDate)
                }
            }
        }
    }

    private fun setupTimePicker() {
        mySaveContext.onShowTimePicker = { onTimePicked ->
            val nowLocal = timeNowLocal()
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(nowLocal.hour)
                    .setMinute(nowLocal.minute)
                    .build()
            picker.show(supportFragmentManager, "timePicker")
            picker.addOnPositiveButtonClickListener {
                onTimePicked(
                    LocalTime.of(picker.hour, picker.minute).convertLocalToUTC().withSecond(0)
                )
            }
        }
    }

    private fun setupActivityForResultLaunchers() {
        createFileLauncher()

        openFileLauncher()
    }

    private fun createFileLauncher() {
        createFileLauncher = activityForResultLauncher(
            createIntent = { _, fileName ->
                Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/csv"
                    putExtra(Intent.EXTRA_TITLE, fileName)

                    // Optionally, specify a URI for the directory that should be opened in
                    // the system file picker before your app creates the document.
                    putExtra(
                        DocumentsContract.EXTRA_INITIAL_URI,
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .toURI()
                    )
                }
            }
        ) { _, intent ->
            intent?.data?.also {
                onFileCreated(it)
            }
        }

        mySaveContext.createNewFile = { fileName, onFileCreatedCallback ->
            onFileCreated = onFileCreatedCallback

            createFileLauncher.launch(fileName)
        }
    }

    private fun openFileLauncher() {
        openFileLauncher = simpleActivityForResultLauncher(
            intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
        ) { _, intent ->
            intent?.data?.also {
                onFileOpened(it)
            }
        }

        mySaveContext.openFile = { onFileOpenedCallback ->
            onFileOpened = onFileOpenedCallback

            openFileLauncher.launch(Unit)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (viewModel.isAppLockEnabled() && !hasFocus) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    private fun authenticateWithOSBiometricsModal(
        biometricPromptCallback: BiometricPrompt.AuthenticationCallback
    ) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            biometricPromptCallback
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(
                getString(R.string.authentication_required)
            )
            .setSubtitle(
                getString(R.string.authentication_required_description)
            )
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .setConfirmationRequired(false)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    @Suppress("TooGenericExceptionCaught", "PrintStackTrace")
    override fun openUrlInBrowser(url: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(url)
            startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            e.sendToCrashlytics("Cannot open URL in browser, intent not supported.")
            Toast.makeText(
                this,
                "No browser app found. Visit manually: $url",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun shareMySave() {
        val share = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, Constants.URL_MY_SAVE_GOOGLE_PLAY)
                type = "text/plain"
            },
            null
        )
        startActivity(share)
    }

    @Suppress("SwallowedException")
    override fun openGooglePlayAppPage(appId: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appId")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appId")
                )
            )
        }
    }

    override fun shareCSVFile(fileUri: Uri) {
        val intent = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "text/csv"
            },
            null
        )
        startActivity(intent)
    }

    override fun shareZipFile(fileUri: Uri) {
        val intent = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "application/zip"
            },
            null
        )
        startActivity(intent)
    }

    override val isDebug: Boolean
        get() = BuildConfig.DEBUG
    override val buildVersionName: String
        get() = BuildConfig.VERSION_NAME
    override val buildVersionCode: Int
        get() = BuildConfig.VERSION_CODE

    override fun reviewMySave(dismissReviewCard: Boolean) {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                reviewInfo.let { review ->
                    val flow = manager.launchReviewFlow(this, review!!)
                    flow.addOnCompleteListener {
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                        if (dismissReviewCard) {
                            customerJourneyLogic.dismissCard(ClientJourneyCardsProvider.rateUsCard())
                        }

                        openGooglePlayAppPage(packageName)
                    }
                }
            } else {
                openGooglePlayAppPage(packageName)
            }
        }
    }

    override fun <T> pinWidget(widget: Class<T>) {
        val appWidgetManager: AppWidgetManager = this.getSystemService(AppWidgetManager::class.java)
        val addTransactionWidget = ComponentName(this, widget)
        appWidgetManager.requestPinAppWidget(addTransactionWidget, null, null)
    }

    // Function to check for app updates in the background
    private fun checkForAppUpdates() {
        Log.d("DISPACTCHERIO", "inside checkForUpdates")
        val dataSource = AppUpdatesDataSource(this)
        val apiInterface = FirebaseApiClient().getAppUpdates().create(ApiInterface::class.java)
        apiInterface.getAppUpdates().enqueue(object : Callback<AppUpdates> {
            override fun onResponse(call: Call<AppUpdates>, response: Response<AppUpdates>) {
                if(response.isSuccessful) {
                    val appUpdates = response.body()
                    try {
                        val pkgInfo = packageManager.getPackageInfo(packageName, 0)
                        if(appUpdates != null && appUpdates.version != pkgInfo.versionName) {
                            // Checking and making sure we don't overwrite the same data again and again
                            val previousUpdate = dataSource.getAppUpdates()
                            if(previousUpdate == null) {
                                dataSource.addAppUpdates(appUpdates)
                            }
                            if(previousUpdate != null && appUpdates.version != previousUpdate.version) {
                                dataSource.clearAppUpdates()
                                dataSource.addAppUpdates(appUpdates)
                            }
                            Log.d("DISPACTCHERIO", "inside new updates available")
                            intent = Intent(this@MySaveRootActivty, AppUpdatesActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    catch (e: PackageManager.NameNotFoundException) { }
                }
            }
            override fun onFailure(call: Call<AppUpdates>, t: Throwable) { }
        })
    }

    //FCM
    private fun createNotificationChannel() {
        val name = "Mysave Notifications"
        val descriptionText = "Notifications for Mysave app"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    // Method to request POST_NOTIFICATION permission for Android 13+
    private fun requestNotificationPermission() {
        // Register the permission launcher
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, proceed with notifications
                Log.d("Notification", "Notification Permission Granted")
            } else {
                // Permission denied, handle accordingly
                Log.d("Notification", "Notification Permission Denied")
            }
        }

        // Check if we should show a rationale for the permission
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            // Show a custom dialog explaining why the permission is needed
            showPermissionExplanationDialog {
                // Launch the permission request after showing the rationale
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Directly request the permission if no rationale is needed
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Show custom dialog to explain POST_NOTIFICATION permission
    private fun showPermissionExplanationDialog(onContinue: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Notification Permission Needed")
            .setMessage("Mysave needs permission to send you important notifications like account " +
                    "updates, transaction alerts, and savings goals. Please grant the notification permission.")
            .setPositiveButton("OK") { dialog, _ ->
                onContinue()
                dialog.dismiss()
            }
            .setNegativeButton("No Thanks") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    override fun onResume() {
        super.onResume()
        if (viewModel.isAppLockEnabled()) {
            viewModel.checkUserInactiveTimeStatus()
        }
        noInternetDialog.destroy()
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.isAppLockEnabled()) {
            viewModel.startUserInactiveTimeCounter()
        }
        noInternetDialog.destroy()
    }

    override fun onBackPressed() {
        noInternetDialog.destroy()
        if (viewModel.isAppLocked()) {
            super.onBackPressed()
        } else {
            if (!navigation.onBackPressed()) {
                super.onBackPressed()
            }
        }
    }
}
