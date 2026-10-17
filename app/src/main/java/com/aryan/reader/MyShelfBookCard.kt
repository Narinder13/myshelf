package com.aryan.reader

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aryan.reader.data.RecentFileItem
import kotlin.math.roundToInt

internal fun RecentFileItem.knownReadingProgress(): Float? =
    knownReadingProgress(progressPercentage)

internal fun RecentFileItem.knownCardAuthor(): String? =
    knownCardAuthor(author)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MyShelfBookCard(
    item: RecentFileItem,
    isSelected: Boolean,
    isPinned: Boolean,
    isDownloading: Boolean,
    usePdfFileNameAsDisplayName: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = item.knownReadingProgress()
    val progressDescription = progress?.let { stringResource(R.string.progress_complete, it.roundToInt()) }
    val state = bookCardStateDescription(item, isSelected, isDownloading, progressDescription)
    val coverShape = RoundedCornerShape(8.dp)
    Column(
        modifier = modifier
            .testTag("HomeRecentFileCard_${item.bookId}")
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                onLongClickLabel = stringResource(R.string.myshelf_book_actions),
            )
            .semantics(mergeDescendants = true) {
                selected = isSelected
                stateDescription = state
            }
            .padding(4.dp),
    ) {
        Box(
            Modifier.fillMaxWidth().aspectRatio(2f / 3f)
                .shadow(3.dp, coverShape)
                .clip(coverShape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    if (isSelected) 2.dp else 0.5.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    coverShape,
                ),
        ) {
            ThemedBookCover(item, Modifier.fillMaxSize(), contentDescription = null)
            if (item.sourceFolderUri != null || item.isOpdsStream() || isPinned) {
                FileStatusBadges(item, isPinned, Modifier.align(Alignment.TopStart).padding(6.dp), overlay = true)
            }
            if (!item.isAvailable) {
                Box(
                    Modifier.fillMaxSize().background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.48f)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            Icons.Default.Info,
                            stringResource(R.string.not_available_locally),
                            Modifier.size(32.dp),
                            tint = Color.White,
                        )
                    }
                }
            }
            if (isSelected) {
                SelectedBookIndicator(Modifier.align(Alignment.TopEnd).padding(6.dp))
            }
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 2.dp)) {
            Spacer(Modifier.height(10.dp))
            Text(
                item.cardTitle(usePdfFileNameAsDisplayName),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(3.dp))
            val author = item.knownCardAuthor()
            Text(
                author ?: " ",
                modifier = if (author == null) Modifier.clearAndSetSemantics {} else Modifier,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(7.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BookFormatLabel(item)
                Spacer(Modifier.weight(1f))
                progress?.let {
                    Text(
                        "${it.roundToInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            Box(Modifier.fillMaxWidth().height(3.dp)) {
                progress?.let { MyShelfReadingProgress(it, Modifier.fillMaxWidth()) }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

/** A wrapping text column avoids the old fixed-height row clipping at large font scales. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MyShelfLibraryListItem(
    item: RecentFileItem,
    isSelected: Boolean,
    isPinned: Boolean,
    isDownloading: Boolean,
    usePdfFileNameAsDisplayName: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val progress = item.knownReadingProgress()
    val progressDescription = progress?.let { stringResource(R.string.progress_complete, it.roundToInt()) }
    val state = bookCardStateDescription(item, isSelected, isDownloading, progressDescription)
    val shape = RoundedCornerShape(16.dp)
    Surface(
        shape = shape,
        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(
            if (isSelected) 2.dp else 0.5.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        modifier = Modifier.fillMaxWidth().testTag("LibraryBookItem_${item.bookId}")
            .clip(shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                onLongClickLabel = stringResource(R.string.myshelf_book_actions),
            )
            .semantics(mergeDescendants = true) {
                selected = isSelected
                stateDescription = state
            },
    ) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 136.dp).padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(Modifier.width(72.dp).aspectRatio(2f / 3f).clip(RoundedCornerShape(6.dp))) {
                ThemedBookCover(item, Modifier.fillMaxSize(), contentDescription = null)
                if (isSelected) SelectedBookIndicator(Modifier.align(Alignment.TopEnd).padding(4.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    item.cardTitle(usePdfFileNameAsDisplayName),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                item.knownCardAuthor()?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    BookFormatLabel(item)
                    if (item.sourceFolderUri != null || item.isOpdsStream() || isPinned) {
                        FileStatusBadges(item, isPinned)
                    }
                }
                if (item.tags.isNotEmpty()) {
                    BookTagChipsRow(item.tags, Modifier.fillMaxWidth(), compact = true)
                }
                if (!item.isAvailable) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (isDownloading) CircularProgressIndicator(Modifier.size(14.dp), strokeWidth = 2.dp)
                        else Icon(Icons.Default.Info, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                        Text(
                            stringResource(if (isDownloading) R.string.status_downloading else R.string.not_available_locally),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                progress?.let {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MyShelfReadingProgress(it, Modifier.weight(1f))
                        Text("${it.roundToInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
internal fun MyShelfReadingProgress(progress: Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        progress = { progress / 100f },
        modifier = modifier.height(3.dp).clip(CircleShape).clearAndSetSemantics {},
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
        gapSize = 0.dp,
        drawStopIndicator = {},
    )
}

@Composable
private fun BookFormatLabel(item: RecentFileItem) {
    Text(
        if (item.type == FileType.UNKNOWN) "FILE" else item.type.name,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
    )
}

@Composable
private fun SelectedBookIndicator(modifier: Modifier) {
    Icon(
        Icons.Default.Check,
        stringResource(R.string.content_desc_selected),
        modifier.size(28.dp).background(MaterialTheme.colorScheme.primary, CircleShape).padding(5.dp),
        tint = MaterialTheme.colorScheme.onPrimary,
    )
}

@Composable
private fun bookCardStateDescription(
    item: RecentFileItem,
    isSelected: Boolean,
    isDownloading: Boolean,
    progressDescription: String?,
): String = listOfNotNull(
    progressDescription,
    stringResource(R.string.content_desc_selected).takeIf { isSelected },
    if (!item.isAvailable) stringResource(if (isDownloading) R.string.status_downloading else R.string.not_available_locally) else null,
).joinToString(", ")
