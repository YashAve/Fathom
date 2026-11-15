package com.development.fathom.core.designsystem.component.core

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.development.fathom.core.designsystem.theme.FathomFonts
import com.development.fathom.core.designsystem.theme.FathomTheme

enum class FathomWordmarkTone { INK, ACCENT, ON_COLOR, QUIET }

@Preview
@Composable
fun FathomWordmark(
    modifier: Modifier = Modifier,
    tone: FathomWordmarkTone = FathomWordmarkTone.INK
) {
    val color = when (tone) {
        FathomWordmarkTone.INK -> FathomTheme.colors.fgPrimary
        FathomWordmarkTone.ACCENT -> FathomTheme.colors.textAccent
        FathomWordmarkTone.ON_COLOR -> FathomTheme.colors.currentOn
        FathomWordmarkTone.QUIET -> FathomTheme.colors.fgTertiary
    }

    Text(
        text = "fathom",
        style = TextStyle(
            fontFamily = FathomFonts.spaceGrotesk,
            fontWeight = FontWeight.W600,
            fontSize = 22.sp,
            lineHeight = 22.sp,
            letterSpacing = (-0.003).em,
            color = color
        ),
        modifier = modifier
    )
}