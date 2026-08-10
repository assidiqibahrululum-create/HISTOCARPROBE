package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

// 1. Radar Chart 4 Dimension Canvas
@Composable
fun DiagnosticRadarChart(
    scoreIdentifikasi: Float,
    scoreInterpretasi: Float,
    scoreKontekstualisasi: Float,
    scoreEvaluasi: Float,
    modifier: Modifier = Modifier
) {
    val scores = listOf(scoreIdentifikasi, scoreInterpretasi, scoreKontekstualisasi, scoreEvaluasi)
    val labels = listOf("Identifikasi", "Interpretasi", "Kontekstualisasi", "Evaluasi Kritis")

    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800),
        label = "RadarAnim"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.width.coerceAtMost(size.height) / 2f) - 48.dp.toPx()

            // Draw background web polygons (4 concentric layers: 25%, 50%, 75%, 100%)
            val numSides = 4
            val angleStep = (2 * Math.PI / numSides).toFloat()
            val startAngle = -Math.PI.toFloat() / 2f // Top angle

            for (level in 1..4) {
                val levelRadius = radius * (level / 4f)
                val path = Path()
                for (i in 0 until numSides) {
                    val angle = startAngle + i * angleStep
                    val x = center.x + levelRadius * cos(angle)
                    val y = center.y + levelRadius * sin(angle)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(
                    path = path,
                    color = Color.LightGray.copy(alpha = 0.4f),
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Draw axis lines
            for (i in 0 until numSides) {
                val angle = startAngle + i * angleStep
                val x = center.x + radius * cos(angle)
                val y = center.y + radius * sin(angle)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.6f),
                    start = center,
                    end = Offset(x, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Draw Score Polygon
            val scorePath = Path()
            val scorePoints = mutableListOf<Offset>()
            for (i in 0 until numSides) {
                val angle = startAngle + i * angleStep
                val scoreRatio = (scores[i].coerceIn(0f, 100f) / 100f) * animProgress
                val currentRadius = radius * scoreRatio
                val x = center.x + currentRadius * cos(angle)
                val y = center.y + currentRadius * sin(angle)
                scorePoints.add(Offset(x, y))
                if (i == 0) scorePath.moveTo(x, y) else scorePath.lineTo(x, y)
            }
            scorePath.close()

            // Fill & Stroke Score Polygon
            drawPath(
                path = scorePath,
                color = TealPrimary.copy(alpha = 0.35f)
            )
            drawPath(
                path = scorePath,
                color = TealPrimary,
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw Dots at Score Vertices
            for (pt in scorePoints) {
                drawCircle(color = MustardDark, radius = 5.dp.toPx(), center = pt)
                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = pt)
            }
        }

        // Overlay Labels for Dimensions
        val labelOffsets = listOf(
            Alignment.TopCenter to "Identifikasi\n${scoreIdentifikasi.toInt()}%",
            Alignment.CenterEnd to "Interpretasi\n${scoreInterpretasi.toInt()}%",
            Alignment.BottomCenter to "Kontekstualisasi\n${scoreKontekstualisasi.toInt()}%",
            Alignment.CenterStart to "Evaluasi Kritis\n${scoreEvaluasi.toInt()}%"
        )

        for ((alignment, text) in labelOffsets) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = alignment
            ) {
                Surface(
                    color = SurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = NavyDark
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

// 2. Mini 4-Bar Chart for Student List
@Composable
fun Mini4BarChart(
    s1: Float,
    s2: Float,
    s3: Float,
    s4: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(80.dp)
            .height(36.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        val bars = listOf(s1 to TealPrimary, s2 to TealLight, s3 to MustardDark, s4 to GroupEPink)
        for ((score, color) in bars) {
            val barHeightRatio = (score / 100f).coerceIn(0.1f, 1f)
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .fillMaxHeight(barHeightRatio)
                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    .background(color)
            )
        }
    }
}

// 3. Colored Group Badge (A, B, C, D, E)
@Composable
fun GroupBadge(
    group: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (group.uppercase()) {
        "A" -> Triple(GroupAGreen, Color.White, "Kelompok A — Sangat Mahir")
        "B" -> Triple(GroupABlue, Color.White, "Kelompok B — Berkembang Lanjut")
        "C" -> Triple(GroupCYellow, NavyDark, "Kelompok C — Berkembang")
        "D" -> Triple(GroupDOrange, Color.White, "Kelompok D — Mulai Berkembang")
        else -> Triple(GroupEPink, Color.White, "Kelompok E — Perlu Intervensi")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// 4. Custom Full-Width Large Touch Target Button
@Composable
fun PrimaryLargeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    containerColor: Color = TealPrimary
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}
