package com.oneSaver.data.repository.fake

import com.oneSaver.base.TestDispatchersProvider
import com.oneSaver.data.DataObserver
import com.oneSaver.data.repository.RepositoryMemoFactory
import org.jetbrains.annotations.VisibleForTesting

@VisibleForTesting
fun fakeRepositoryMemoFactory(): RepositoryMemoFactory = RepositoryMemoFactory(
    dataObserver = DataObserver(),
    dispatchers = TestDispatchersProvider
)