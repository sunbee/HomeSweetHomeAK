package com.example.homesweethome

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
        val pathA = Path()
        val pathB = Path()

        val wavePoint1 = Offset(0f, size.height * 1.0f)
        val wavePoint1B = Offset(0f, size.height * 1.8f)
        val wavePoint2 = Offset(size.width * 0.15f, size.height * 0.5f)
        val wavePoint3 = Offset(size.width * 0.9f, size.height * 0.9f)
        val wavePoint4 = Offset(size.width * 1.0f, size.height * 0.0f)
        val wavePoint4B = Offset(size.width * 1.4f, -size.height * 1.0f)  // Outside bounds

        pathA.moveTo(wavePoint1.x, wavePoint1.y)
        pathA.cubicTo(wavePoint2.x, wavePoint2.y,
            wavePoint3.x, wavePoint3.y,
            wavePoint4.x, wavePoint4.y)
        pathA.lineTo(size.width * 1.0f, size.height * 1.0f)
        pathA.lineTo(0f, size.height * 1.0f)
        pathA.close()

        pathB.moveTo(wavePoint1B.x, wavePoint1B.y)
        pathB.cubicTo(wavePoint2.x, wavePoint2.y,
            wavePoint3.x, wavePoint3.y,
            wavePoint4.x, wavePoint4.y)
        pathB.lineTo(size.width * 1.0f, size.height * 1.0f)
        pathB.lineTo(0f, size.height * 1.0f)
        pathB.close()

        drawPath(
            path = pathA,
            color = Color.Yellow.copy(alpha = 0.5f)
        )
        drawPath(
            pathB,
            Color.Yellow.copy(alpha=0.5f)
        )
    }
}
