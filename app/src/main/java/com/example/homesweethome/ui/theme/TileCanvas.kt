package com.example.homesweethome.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
fun TileCanvas() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path()

        val top = size.height / 3.0f
        val bottom = size.height * 2.0f / 3.0f

        // Define control points for the Bezier curves
        val controlX1 = size.width * 0.25f
        val controlY1 = (top + bottom) / 2.0f
        val controlX2 = size.width * 0.75f
        val controlY2 = (top + bottom) / 2.0f
        val endX = size.width
        val endY = top

        path.moveTo(0f, bottom)
        path.lineTo(0f, top)
        path.cubicTo(controlX1, controlY1, controlX2, controlY2, endX, endY)
        path.lineTo(size.width, bottom)
        path.lineTo(0f, bottom)

        path.lineTo(size.width, bottom)
        path.lineTo(0f, bottom)

        drawPath(
            path = path,
            color = Color.Yellow.copy(alpha = 0.7f)
        )
    }

}
