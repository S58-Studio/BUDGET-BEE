package com.oneSaver.allStatus

import android.content.Context
import android.content.Intent
import com.oneSaver.base.model.TransactionType
import com.oneSaver.domains.AppStarter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MylonAppStarter @Inject constructor(
    @ApplicationContext
    private val context: Context
) : AppStarter {

    override fun getRootIntent(): Intent {
        return Intent(context, MySaveRootActivty::class.java)
    }

    override fun defaultStart() {
        context.startActivity(
            getRootIntent().apply {
                applyWidgetStartFlags()
            }
        )
    }

    override fun addTransactionStart(type: TransactionType) {
        context.startActivity(
            getRootIntent().apply {
                putExtra(RootVM.EXTRA_ADD_TRANSACTION_TYPE, type)
                applyWidgetStartFlags()
            }
        )
    }

    private fun Intent.applyWidgetStartFlags() {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}
