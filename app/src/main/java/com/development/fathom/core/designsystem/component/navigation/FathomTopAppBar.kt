package com.development.fathom.core.designsystem.component.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development.fathom.core.designsystem.component.core.FathomIconButton
import com.development.fathom.core.designsystem.theme.FathomTheme
import com.development.fathom.core.designsystem.theme.FathomTypography

@Preview
@Composable
fun FathomTopAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = { }
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(60.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                actions()
            }
        if (leading != null) leading()
        else if (onBack != null) FathomIconButton(
            label = "Back",
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            onClick = onBack
        )
        Column(modifier = Modifier
            .weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)) {
            if (title != null) {
                Text(
                    text = title,
                    style = FathomTypography.titleLarge.copy(color = FathomTheme.colors.fgPrimary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
            }
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = FathomTypography.labelMedium.copy(color = FathomTheme.colors.fgTertiary)
                )
            }
        }
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            actions()
        }
    }
}