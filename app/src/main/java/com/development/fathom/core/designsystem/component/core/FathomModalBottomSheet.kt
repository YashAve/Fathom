package com.development.fathom.core.designsystem.component.core

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.AutoMirrored.Outlined
import androidx.compose.material.icons.automirrored.outlined.PlaylistAdd
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development.fathom.core.designsystem.theme.FathomMotion
import com.development.fathom.core.designsystem.theme.FathomTheme
import com.development.fathom.core.designsystem.theme.FathomTypography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class FathomSheetItem(
    val label: String,
    val icon: ImageVector? = null,
    val meta: String? = null,
    val warm: Boolean = false,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FathomSheetItemRow(
    modifier: Modifier = Modifier,
    item: FathomSheetItem = previewSheetItems[0],
    onDismiss: () -> Unit = { },
    sheetState: () -> SheetState,
    scope: () -> CoroutineScope
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState().value
    val itemColor = when {
        isPressed -> FathomTheme.colors.textAccent
        item.warm -> FathomTheme.colors.warm
        else -> FathomTheme.colors.fgPrimary
    }
    val pillColor = if (item.warm) FathomTheme.colors.warmWash else FathomTheme.colors.currentWash
    val pressColor by animateColorAsState(
        targetValue = if (isPressed) pillColor else pillColor.copy(alpha = 0f),
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = {
                    item.onClick()
                    scope().launch {
                        sheetState().hide()
                    }.invokeOnCompletion {
                        if (!sheetState().isVisible) onDismiss()
                    }
                }
            )
            .drawBehind {
                drawRoundRect(pressColor, cornerRadius = CornerRadius(20.dp.toPx()))
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.icon != null) {
            Icon(
                imageVector = item.icon,
                tint = itemColor,
                contentDescription = item.label,
                modifier = Modifier.size(21.dp)
            )
        }
        Text(
            text = item.label,
            style = FathomTypography.bodyLarge.copy(color = itemColor),
            modifier = Modifier.weight(1f)
        )
        if (item.meta != null) {
            Text(
                text = item.meta,
                style = FathomTypography.labelMedium.copy(color = FathomTheme.colors.fgTertiary)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FathomBottomSheet(
    modifier: Modifier = Modifier,
    title: String? = null,
    items: List<FathomSheetItem> = emptyList(),
    onDismiss: () -> Unit = { },
    content: @Composable ColumnScope.() -> Unit = { }
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = FathomTheme.colors.bgElev,
        scrimColor = FathomTheme.colors.scrim,
        dragHandle = {
            Spacer(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 16.dp)
                    .size(44.dp, 4.dp)
                    .background(FathomTheme.colors.fgDisabled, CircleShape)
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            if (title != null) {
                Text(
                    text = title.uppercase(),
                    style = FathomTypography.labelSmall.copy(color = FathomTheme.colors.fgTertiary),
                    modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
                )
            }
            items.forEach { item ->
                FathomSheetItemRow(
                    item = item,
                    onDismiss = onDismiss,
                    sheetState = { sheetState }) { scope }
            }
            content()
        }
    }
}

private val previewSheetItems = listOf(
    FathomSheetItem(label = "Play next", icon = Outlined.PlaylistPlay, onClick = {}),
    FathomSheetItem(label = "Add to queue", icon = Outlined.QueueMusic, onClick = {}),
    FathomSheetItem(label = "Add to playlist", icon = Outlined.PlaylistAdd, onClick = {}),
    FathomSheetItem(
        label = "Go to album",
        icon = Icons.Outlined.Album,
        meta = "Undertow",
        onClick = {}),
    FathomSheetItem(label = "Track info", icon = Icons.Outlined.Info, meta = "3:34", onClick = {}),
    FathomSheetItem(
        label = "Remove from library",
        icon = Icons.Outlined.Delete,
        warm = true,
        onClick = {}),
)

@Preview
@Composable
private fun FathomBottomSheetPreview() {
    FathomTheme {
        FathomBottomSheet(
            title = "Undertow · Ilse Fen",
            items = previewSheetItems,
            onDismiss = { }
        )
    }
}