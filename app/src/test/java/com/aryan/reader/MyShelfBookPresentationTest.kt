package com.aryan.reader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MyShelfBookPresentationTest {
    @Test
    fun unknownAndInvalidProgressDoNotProduceAnIndicator() {
        listOf(null, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY).forEach {
            assertNull(knownReadingProgress(it))
        }
    }

    @Test
    fun knownProgressPreservesZeroAndClampsToTheReadableRange() {
        assertEquals(0f, knownReadingProgress(0f)!!, 0f)
        assertEquals(42.75f, knownReadingProgress(42.75f)!!, 0f)
        assertEquals(0f, knownReadingProgress(-5f)!!, 0f)
        assertEquals(100f, knownReadingProgress(105f)!!, 0f)
    }

    @Test
    fun absentAuthorMetadataDoesNotInventAnAuthor() {
        listOf(null, "", "  ", "Unknown", " unknown ").forEach {
            assertNull(knownCardAuthor(it))
        }
        assertEquals("Ada Lovelace", knownCardAuthor(" Ada Lovelace "))
    }

}
