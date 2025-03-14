package com.oneSaver.allStatus.domain.deprecated.logic

import com.oneSaver.base.legacy.Transaction
import com.oneSaver.data.database.dao.read.TransactionDao
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import com.oneSaver.legacy.utils.capitalizeWords
import com.oneSaver.legacy.utils.isNotNullOrBlank
import java.util.*
import javax.inject.Inject

const val SUGGESTIONS_LIMIT = 10

@Deprecated("Use FP style, look into `domain.fp` package")
class SmartTitleSuggestionsLogic @Inject constructor(
    private val transactionDao: TransactionDao
) {

    /**
     * Suggests titles based on:
     * - title match
     * - most used titles for categories
     * - if suggestions.size < SUGGESTIONS_LIMIT most used titles for accounts
     */
    suspend fun suggest(
        title: String?,
        categoryId: UUID?,
        accountId: UUID?
    ): Set<String> {
        val suggestions = mutableSetOf<String>()

        if (title != null && title.isNotEmpty()) {
            // suggest by title
            val suggestionsByTitle = transactionDao.findAllByTitleMatchingPattern("$title%")
                .map { it.toLegacyDomain() }
                .extractUniqueTitles()
                .sortedByMostUsedFirst {
                    transactionDao.countByTitleMatchingPattern("$it%")
                }

            suggestions.addAll(suggestionsByTitle)
        }

        if (categoryId != null) {
            // suggest by category
            // all titles used for the specific category
            // ordered by N times used

            val suggestionsByCategory = transactionDao
                .findAllByCategory(
                    categoryId = categoryId
                )
                .map { it.toLegacyDomain() }
                // exclude already suggested suggestions so they're ordered by priority at the end
                .extractUniqueTitles(excludeSuggestions = suggestions)
                .sortedByMostUsedFirst {
                    transactionDao.countByTitleMatchingPatternAndCategoryId(
                        pattern = it,
                        categoryId = categoryId
                    )
                }

            suggestions.addAll(suggestionsByCategory)
        }

        if (suggestions.size < SUGGESTIONS_LIMIT && accountId != null) {
            // last resort, suggest by account
            // all titles used for the specific account
            // ordered by N times used

            val suggestionsByAccount = transactionDao
                .findAllByAccount(
                    accountId = accountId
                )
                .map { it.toLegacyDomain() }
                // exclude already suggested suggestions so they're ordered by priority at the end
                .extractUniqueTitles(excludeSuggestions = suggestions)
                .sortedByMostUsedFirst {
                    transactionDao.countByTitleMatchingPatternAndAccountId(
                        pattern = it,
                        accountId = accountId
                    )
                }

            suggestions.addAll(suggestionsByAccount)
        }

        return suggestions
            .filter { it != title }
            .toSet()
    }
}

@Deprecated("Use FP style, look into `domain.fp` package")
private fun List<Transaction>.extractUniqueTitles(
    excludeSuggestions: Set<String>? = null
): Set<String> {
    return this
        .filter { it.title.isNotNullOrBlank() }
        .map { it.title!!.trim().capitalizeWords() }
        .filter { excludeSuggestions == null || !excludeSuggestions.contains(it) }
        .toSet()
}

@Deprecated("Use FP style, look into `domain.fp` package")
private suspend fun Set<String>.sortedByMostUsedFirst(countUses: suspend (String) -> Long): Set<String> {
    val titleCountMap = this
        .map {
            it to countUses(it)
        }
        .toMap()

    val sortedSuggestions = this
        .sortedByDescending {
            titleCountMap.getOrDefault(it, 0)
        }
        .toSet()

    return sortedSuggestions
}
