package com.aryan.reader

/** Unknown progress stays unknown; malformed metadata must never reach a progress indicator. */
internal fun knownReadingProgress(progressPercentage: Float?): Float? =
    progressPercentage?.takeIf { it.isFinite() }?.coerceIn(0f, 100f)

internal fun knownCardAuthor(author: String?): String? =
    author?.trim()?.takeIf { it.isNotEmpty() && !it.equals("Unknown", ignoreCase = true) }
