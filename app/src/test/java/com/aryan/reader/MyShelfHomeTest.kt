package com.aryan.reader

import com.aryan.reader.data.RecentFileItem
import org.junit.Assert.assertEquals
import org.junit.Test

class MyShelfHomeTest {
    private fun book(id: String, added: Long, opened: Long = added) = RecentFileItem(
        bookId = id,
        uriString = null,
        type = FileType.PDF,
        displayName = id,
        timestamp = opened,
        dateAddedTimestamp = added,
    )

    @Test
    fun recentlyAddedUsesImportTimeAndDoesNotMoveBooksWhenOpened() {
        val oldImport = book("old", added = 1L, opened = 100L)
        val newImport = book("new", added = 2L, opened = 2L)

        assertEquals(listOf(newImport, oldImport), recentlyAddedMyShelfBooks(listOf(oldImport, newImport)))
    }

    @Test
    fun recentlyAddedExcludesDeletedBooksAndLimitsTheCarousel() {
        val newestDeleted = book("deleted", 10L).copy(isDeleted = true)
        val first = book("first", 3L)
        val second = book("second", 2L)

        assertEquals(listOf(first), recentlyAddedMyShelfBooks(listOf(newestDeleted, second, first), limit = 1))
        assertEquals(emptyList<RecentFileItem>(), recentlyAddedMyShelfBooks(listOf(first), limit = 0))
    }
}
