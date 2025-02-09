package com.financeAndMoney.data.repository.fake

import com.financeAndMoney.base.TestDispatchersProvider
import com.financeAndMoney.data.DataObserver
import com.financeAndMoney.data.repository.RepositoryMemoFactory
import org.jetbrains.annotations.VisibleForTesting

@VisibleForTesting
fun fakeRepositoryMemoFactory(): RepositoryMemoFactory = RepositoryMemoFactory(
    dataObserver = DataObserver(),
    dispatchers = TestDispatchersProvider
)