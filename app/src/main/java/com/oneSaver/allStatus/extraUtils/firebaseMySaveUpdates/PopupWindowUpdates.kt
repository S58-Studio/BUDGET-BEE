package com.oneSaver.allStatus.extraUtils.firebaseMySaveUpdates


import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.oneSaver.allStatus.R

class PopupWindowUpdates : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Create AlertDialog.Builder Instance
        val builder = Dialog(requireContext())

        // Creating the custom layout using Inflater Class
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.popup_window_update, null)
        builder.setContentView(view)

        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dataSource = AppUpdatesDataSource(requireContext())
        val appUpdates = dataSource.getAppUpdates()
        if (appUpdates != null) {
            val appVersion: TextView = view.findViewById(R.id.app_version)
            val newUpdates: TextView = view.findViewById(R.id.new_updates)
            val updateNowBtn: AppCompatButton = view.findViewById(R.id.updateNowBtn)

            appVersion.text = String.format(
                "${getString(R.string.app_name)} ${getString(R.string.app_version)}",
                appUpdates.version
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                newUpdates.text = Html.fromHtml(appUpdates.updates, Html.FROM_HTML_MODE_COMPACT)
            } else {
                newUpdates.text = Html.fromHtml(appUpdates.updates)
            }

            updateNowBtn.setOnClickListener {
                val uri: Uri = if (appUpdates.url.startsWith("http://play.google.com")) {
                    Uri.parse("market://details?id=${requireContext().packageName}")
                } else {
                    Uri.parse(appUpdates.url)
                }

                val intent = Intent(Intent.ACTION_VIEW, uri)
                var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                if (Build.VERSION.SDK_INT >= 21) {
                    flags = flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                } else {
                    flags = flags or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
                }

                intent.addFlags(flags)

                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
            }
        }

        return builder
    }
}

