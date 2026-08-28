package com.shuvo.quran.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val LaunchGreen = Color(0xFF063B2C)
private val Gold = Color(0xFFE7B85C)

@Composable
fun LaunchScreen(
    onFinished: () -> Unit
) {

    LaunchedEffect(Unit) {
        delay(1800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LaunchGreen),
        contentAlignment = Alignment.Center
    ) {

        // Subtle decorative pattern
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val spacing = 90f

            for (x in 0..size.width.toInt() step spacing.toInt()) {
                for (y in 0..size.height.toInt() step spacing.toInt()) {

                    drawCircle(
                        color = Gold.copy(alpha = 0.035f),
                        radius = 32f,
                        center = androidx.compose.ui.geometry.Offset(
                            x.toFloat(),
                            y.toFloat()
                        )
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Islamic geometric emblem
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {

                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {

                    val path = Path()

                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val outerRadius = size.minDimension * 0.45f
                    val innerRadius = size.minDimension * 0.32f

                    for (i in 0 until 16) {

                        val angle =
                            Math.toRadians((i * 22.5) - 90)

                        val radius =
                            if (i % 2 == 0)
                                outerRadius
                            else
                                innerRadius

                        val x =
                            centerX + radius *
                                    kotlin.math.cos(angle).toFloat()

                        val y =
                            centerY + radius *
                                    kotlin.math.sin(angle).toFloat()

                        if (i == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }

                    path.close()

                    drawPath(
                        path = path,
                        color = Gold,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 5f
                        )
                    )
                }

                Text(
                    text = "الْقُرْآن",
                    color = Gold,
                    fontSize = 31.sp,
                    lineHeight = 42.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(45.dp))

            Text(
                text = "Simple",
                color = Gold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Lightweight",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Customizable",
                color = Gold,
                fontSize = 16.sp
            )
        }
    }
}