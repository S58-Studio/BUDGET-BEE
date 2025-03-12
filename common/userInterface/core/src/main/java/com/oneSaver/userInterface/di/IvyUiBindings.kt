package com.oneSaver.userInterface.di

import com.oneSaver.userInterface.time.DevicePreferences
import com.oneSaver.userInterface.time.TimeFormatter
import com.oneSaver.userInterface.time.impl.AndroidDateTimePicker
import com.oneSaver.userInterface.time.impl.AndroidDevicePreferences
import com.oneSaver.userInterface.time.impl.DateTimePicker
import com.oneSaver.userInterface.time.impl.MysaveTimeFormatter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface IvyUiBindings {
    @Binds
    fun timeFormatter(impl: MysaveTimeFormatter): TimeFormatter

    @Binds
    fun deviceTimePreferences(impl: AndroidDevicePreferences): DevicePreferences

    @Binds
    fun dateTimePicker(impl: AndroidDateTimePicker): DateTimePicker
}