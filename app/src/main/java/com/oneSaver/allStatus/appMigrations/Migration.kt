package com.oneSaver.allStatus.appMigrations

interface Migration {
    val key: String

    suspend fun migrate()
}
