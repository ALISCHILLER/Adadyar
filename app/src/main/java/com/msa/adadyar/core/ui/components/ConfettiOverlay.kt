package com.msa.adadyar.core.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun ConfettiOverlay(
    show: Boolean,
    modifier: Modifier = Modifier
) {
    if (!show) return
    val transition = rememberInfiniteTransition(label = "confetti")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 2000), RepeatMode.Restart),
        label = "offset"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        drawConfetti(offset)
    }
}

private fun DrawScope.drawConfetti(progress: Float) {
    val colors = listOf(Color(0xFFFFC107), Color(0xFF4CAF50), Color(0xFF29B6F6), Color(0xFFFF7043))
    val random = Random(0)
    repeat(40) { index ->
        val x = (size.width * (index / 40f)) + random.nextFloat() * 40f
        val y = size.height * ((index + progress) % 1f)
        drawCircle(
            color = colors[index % colors.size],
            radius = 6.dp.toPx(),
            center = Offset(x % size.width, y)
        )
    }
}

@Preview
@Composable
private fun ConfettiOverlayPreview() {
    ConfettiOverlay(show = true, modifier = Modifier.size(200.dp))
}