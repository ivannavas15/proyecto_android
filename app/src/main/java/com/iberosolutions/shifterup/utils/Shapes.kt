package com.iberosolutions.shifterup.utils

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class BottomBarShape(
    private val fabSize: Float,
    private val fabMargin: Float
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                val cutoutRadius = (fabSize / 2) + fabMargin
                val centerX = size.width / 2

                // Puntos de control para suavizar la curva
                val cornerRadius = 10f // Radio de suavizado de las esquinas superiores

                moveTo(0f, 0f)

                // Línea hasta el inicio de la curva izquierda
                lineTo(centerX - cutoutRadius - cornerRadius, 0f)

                // Curva de entrada suave (Izquierda)
                cubicTo(
                    x1 = centerX - cutoutRadius, y1 = 0f,
                    x2 = centerX - cutoutRadius, y2 = 0f, // Punto de control ajustado
                    x3 = centerX - cutoutRadius + (cornerRadius/2), y3 = cutoutRadius / 2
                )

                // Curva del "bocado" principal (Centro)
                cubicTo(
                    x1 = centerX - (cutoutRadius / 2), y1 = cutoutRadius,
                    x2 = centerX + (cutoutRadius / 2), y2 = cutoutRadius,
                    x3 = centerX + cutoutRadius - (cornerRadius/2), y3 = cutoutRadius / 2
                )

                // Curva de salida suave (Derecha)
                cubicTo(
                    x1 = centerX + cutoutRadius, y1 = 0f,
                    x2 = centerX + cutoutRadius + cornerRadius, y2 = 0f,
                    x3 = centerX + cutoutRadius + cornerRadius + 10f, y3 = 0f
                )

                // Línea hasta el final
                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
        )
    }
}