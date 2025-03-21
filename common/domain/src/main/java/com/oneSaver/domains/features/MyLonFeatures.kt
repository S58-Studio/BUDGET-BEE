package com.oneSaver.domains.features

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyLonFeatures @Inject constructor() : Features {
    override val sortCategoriesAlphabetically = BoolFeature(
        key = "sort_categories_alphabetically",
        name = "Sort Categories Alphabetically",
        description = "Sort income and expenses" +
        " categories alphabetically"
    )

    override val allFeatures: List<BoolFeature>
        get() = listOf(
            sortCategoriesAlphabetically
        )
}
