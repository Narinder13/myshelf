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

    @Test
    fun statusCountsFollowLibraryProgressBoundariesWithoutInventingProgress() {
        val books = listOf(
            book("unknown", 1L),
            book("first-page", 1L).copy(lastPage = 0),
            book("zero", 1L).copy(progressPercentage = 0f),
            book("negative", 1L).copy(progressPercentage = -1f),
            book("below-reading-boundary", 1L).copy(progressPercentage = .001f),
            book("reading-boundary", 1L).copy(progressPercentage = .01f),
            book("reading", 1L).copy(progressPercentage = 99.99f),
            book("complete", 1L).copy(progressPercentage = 100f),
            book("past-complete", 1L).copy(progressPercentage = 101f),
            book("invalid", 1L).copy(progressPercentage = Float.NaN),
            book("deleted", 1L).copy(progressPercentage = 50f, isDeleted = true),
        )

        assertEquals(
            mapOf(UnifiedLibraryFilter.READING to 2, UnifiedLibraryFilter.UNREAD to 4, UnifiedLibraryFilter.FINISHED to 2),
            myShelfHomeStatusCounts(books),
        )
    }

    @Test
    fun emptyLibraryStillHasZeroCountsForEachShortcut() {
        assertEquals(
            mapOf(UnifiedLibraryFilter.READING to 0, UnifiedLibraryFilter.UNREAD to 0, UnifiedLibraryFilter.FINISHED to 0),
            myShelfHomeStatusCounts(emptyList()),
        )
    }

    @Test
    fun pinnedBooksIgnoreStalePinsAndDeletedRowsAndKeepRecentFirst() {
        val old = book("old", added = 1L, opened = 2L)
        val recent = book("recent", added = 1L, opened = 20L)
        val deleted = book("deleted", added = 30L).copy(isDeleted = true)
        val unpinned = book("unpinned", added = 40L)

        assertEquals(
            listOf(recent, old),
            pinnedMyShelfBooks(listOf(old, unpinned, deleted, recent), setOf("old", "recent", "deleted", "missing")),
        )
        assertEquals(emptyList<RecentFileItem>(), pinnedMyShelfBooks(listOf(old), emptySet()))
    }
}
