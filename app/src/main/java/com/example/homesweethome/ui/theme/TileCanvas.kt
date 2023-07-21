package com.example.homesweethome.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect

@Composable
fun TileCanvas() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path()

        val wavePoint1 = Offset(0f, size.height * 1.0f)
        val wavePoint2 = Offset(size.width * 0.15f, size.height * 0.5f)
        val wavePoint3 = Offset(size.width * 0.9f, size.height * 0.9f)
        val wavePoint4 = Offset(size.width * 1.0f, size.height * 0.0f)
        val wavePoint5 = Offset(size.width * 1.4f, -size.height * 1.0f)

        path.moveTo(wavePoint1.x, wavePoint1.y)
        path.cubicTo(wavePoint2.x, wavePoint2.y,
            wavePoint3.x, wavePoint3.y,
            wavePoint4.x, wavePoint4.y)
        path.lineTo(size.width * 1.0f, size.height * 1.0f)
        path.lineTo(0f, size.height * 1.0f)

        drawPath(
            path = path,
            color = Color.Yellow.copy(alpha = 0.7f)
        )
    }
}
