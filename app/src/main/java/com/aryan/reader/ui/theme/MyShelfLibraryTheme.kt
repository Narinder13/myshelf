package com.aryan.reader.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

/** Android bookshelf surfaces. Reader colors and the user's accent/font stay in AppTheme. */
internal object MyShelfLayout {
    val pagePadding = 20.dp
    val bookSpacing = 16.dp
    val compactBookWidth = 128.dp
    val largeBookWidth = 152.dp
    val maxBookWidth = 180.dp
    val railBreakpoint = 600.dp
    val contentMaxWidth = 1200.dp
}

private object LibrarySurfaces {
    val paper = Color(0xFFFAF7F0)
    val paperLow = Color(0xFFF3EFE6)
    val paperContainer = Color(0xFFEDE9DF)
    val paperHigh = Color(0xFFE7E2D7)
    val paperHighest = Color(0xFFDFDACF)
    val ink = Color(0xFF292D29)
    val secondaryInk = Color(0xFF596058)
    val paperOutline = Color(0xFFCBC8BC)
    val charcoal = Color(0xFF191D1B)
    val charcoalLow = Color(0xFF202522)
    val charcoalContainer = Color(0xFF282E2A)
    val charcoalHigh = Color(0xFF303732)
    val charcoalHighest = Color(0xFF39413B)
    val lightInk = Color(0xFFE6EAE2)
    val secondaryLightInk = Color(0xFFB9C2B8)
    val charcoalOutline = Color(0xFF414A43)
}

@Composable
internal fun MyShelfLibraryTheme(content: @Composable () -> Unit) {
    val base = MaterialTheme.colorScheme
    val scheme = remember(base) {
        if (base.background.luminance() < 0.5f) {
            base.copy(
                background = LibrarySurfaces.charcoal,
                surface = LibrarySurfaces.charcoal,
                surfaceDim = LibrarySurfaces.charcoal,
                surfaceBright = LibrarySurfaces.charcoalHigh,
                surfaceContainerLowest = LibrarySurfaces.charcoal,
                surfaceContainerLow = LibrarySurfaces.charcoalLow,
                surfaceContainer = LibrarySurfaces.charcoalContainer,
                surfaceContainerHigh = LibrarySurfaces.charcoalHigh,
                surfaceContainerHighest = LibrarySurfaces.charcoalHighest,
                onBackground = LibrarySurfaces.lightInk,
                onSurface = LibrarySurfaces.lightInk,
                onSurfaceVariant = LibrarySurfaces.secondaryLightInk,
                outlineVariant = LibrarySurfaces.charcoalOutline,
            )
        } else {
            base.copy(
                background = LibrarySurfaces.paper,
                surface = LibrarySurfaces.paper,
                surfaceDim = LibrarySurfaces.paperContainer,
                surfaceBright = LibrarySurfaces.paper,
                surfaceContainerLowest = LibrarySurfaces.paper,
                surfaceContainerLow = LibrarySurfaces.paperLow,
                surfaceContainer = LibrarySurfaces.paperContainer,
                surfaceContainerHigh = LibrarySurfaces.paperHigh,
                surfaceContainerHighest = LibrarySurfaces.paperHighest,
                onBackground = LibrarySurfaces.ink,
                onSurface = LibrarySurfaces.ink,
                onSurfaceVariant = LibrarySurfaces.secondaryInk,
                outlineVariant = LibrarySurfaces.paperOutline,
            )
        }
    }
    MaterialTheme(colorScheme = scheme, typography = MaterialTheme.typography, content = content)
}
