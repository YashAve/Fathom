package com.development.fathom.core.designsystem.component.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.development.fathom.core.designsystem.theme.FathomElevation
import com.development.fathom.core.designsystem.theme.FathomFonts
import com.development.fathom.core.designsystem.theme.FathomMotion
import com.development.fathom.core.designsystem.theme.FathomTheme

data class FathomNavItem(
    val id: String,
    val label: String,
    val iconUnselected: ImageVector,
    val iconSelected: ImageVector
)

val fathomNavItems = listOf(
    FathomNavItem("library", "Library", Icons.Outlined.LibraryMusic, Icons.Filled.LibraryMusic),
    FathomNavItem(
        "playlist",
        "Playlist",
        Icons.AutoMirrored.Outlined.QueueMusic,
        Icons.AutoMirrored.Filled.QueueMusic
    ),
    FathomNavItem("search", "Search", Icons.Outlined.Search, Icons.Filled.Search),
    FathomNavItem("profile", "You", Icons.Outlined.Person, Icons.Filled.Person)
)

@Composable
fun FathomBottomNavItem(
    modifier: Modifier = Modifier,
    item: FathomNavItem = fathomNavItems[0],
    selected: Boolean = false,
    onClick: (String) -> Unit = { }
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState().value
    val icon = if (selected) item.iconSelected else item.iconUnselected
    val color by animateColorAsState(
        targetValue = if (selected) FathomTheme.colors.textAccent else FathomTheme.colors.fgTertiary,
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide)
    )
    val pillColor by animateColorAsState(
        targetValue = if (selected) FathomTheme.colors.currentWash else FathomTheme.colors.currentWash.copy(alpha = 0f),
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide)
    )
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = tween(FathomMotion.quick, easing = FathomMotion.settle)
    )

    Column(
        modifier = modifier
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                enabled = true,
                role = Role.Tab,
                onClick = { onClick(item.id) }
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                drawRoundRect(pillColor, cornerRadius = CornerRadius(24.dp.toPx()))
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = icon,
            tint = color,
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = item.label.uppercase(),
            style = TextStyle(
                fontFamily = FathomFonts.jetbrainsMono,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                lineHeight = 10.sp,
                color = color
            )
        )
    }
}

@Preview
@Composable
fun FathomBottomNav(
    modifier: Modifier = Modifier,
    items: List<FathomNavItem> = fathomNavItems,
    selectedId: String = fathomNavItems[0].id,
    onSelect: (String) -> Unit = { }
) {

    val glassTop = FathomTheme.colors.glassTop
    val glassBottom = FathomTheme.colors.glassBottom

    val gradient = Brush.linearGradient(
        Pair(0f, glassTop),
        Pair(1f, glassBottom),
        start = Offset.Zero,
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )

    val mdAlpha =
        if (FathomTheme.colors.isLight) FathomElevation.Light.md else FathomElevation.Dark.md

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .shadow(
                elevation = FathomElevation.float,
                shape = RoundedCornerShape(32.dp),
                ambientColor = FathomTheme.colors.shadowTint.copy(alpha = mdAlpha),
                spotColor = FathomTheme.colors.shadowTint.copy(alpha = mdAlpha)
            )
            .background(gradient)
            .border(
                width = 1.dp,
                color = FathomTheme.colors.borderSubtle,
                shape = RoundedCornerShape(32.dp)
            )
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            FathomBottomNavItem(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                item = item,
                selected = selectedId == item.id,
                onClick = onSelect
            )
        }
    }
}