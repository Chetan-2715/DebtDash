package com.debtdash.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.debtdash.app.ui.theme.NeonTeal
import com.debtdash.app.ui.theme.OutlineVariant
import com.debtdash.app.ui.theme.SearchBarShape
import com.debtdash.app.ui.theme.SurfaceContainerLow
import com.debtdash.app.ui.theme.TextMuted
import com.debtdash.app.ui.theme.TextPrimary

/**
 * Stealth-themed search bar with neon teal focus border.
 */
@Composable
fun StealthSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search neural records…",
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = TextPrimary
        ),
        cursorBrush = SolidColor(NeonTeal),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = SurfaceContainerLow,
                shape = SearchBarShape
            )
            .border(
                width = 1.dp,
                color = if (query.isNotEmpty()) NeonTeal.copy(alpha = 0.5f)
                else OutlineVariant,
                shape = SearchBarShape
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (query.isNotEmpty()) NeonTeal else TextMuted,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}
