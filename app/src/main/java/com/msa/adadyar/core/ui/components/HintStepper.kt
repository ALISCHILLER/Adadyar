package com.msa.adadyar.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HintStepper(
    currentLevel: Int,
    onHintRequested: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(1, 2, 3).forEach { level ->
            val isUnlocked = currentLevel >= level
            val color = if (isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            val contentColor = if (isUnlocked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color)
                    .clickable(enabled = !isUnlocked) { onHintRequested(level) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = "H$level", style = MaterialTheme.typography.labelLarge, color = contentColor)
            }
        }
    }
}

@Preview
@Composable
private fun HintStepperPreview() {
    HintStepper(currentLevel = 1, onHintRequested = {})
}