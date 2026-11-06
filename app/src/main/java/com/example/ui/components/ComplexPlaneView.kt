package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.ComplexRoot
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ComplexPlaneView(
    roots: List<ComplexRoot>,
    index: Double,
    radicand: Double,
    modifier: Modifier = Modifier
) {
    if (roots.isEmpty()) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Argand Diagram (All Roots)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Available when the index (n) is an integer from 2 to 20.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    var selectedIndex by remember(roots) { mutableIntStateOf(0) }
    val selectedRoot = roots.getOrElse(selectedIndex) { roots.first() }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("complex_plane_card"),
        shape = RoundedCornerShape(20.dp),
        color = SlateCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Argand Complex Plane",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ElectricCyan
                    )
                    Text(
                        text = "All ${roots.size} roots of ($radicand)^(1/${index.toInt()})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F1A30))
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "n = ${roots.size} roots",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = RadiantGold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas drawing the Argand Plane
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF091122))
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .testTag("argand_canvas")
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = (size.minDimension / 2f) * 0.78f

                    // Draw grid circles
                    drawCircle(
                        color = SlateCardBorder.copy(alpha = 0.5f),
                        radius = radius * 0.5f,
                        center = center,
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                        )
                    )

                    // Draw main modulus circle
                    drawCircle(
                        color = ElectricCyan.copy(alpha = 0.35f),
                        radius = radius,
                        center = center,
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                        )
                    )

                    // Draw Real and Imaginary Axes
                    // Re Axis (Horizontal)
                    drawLine(
                        color = SlateCardBorder,
                        start = Offset(16f, center.y),
                        end = Offset(size.width - 16f, center.y),
                        strokeWidth = 1.5.dp.toPx()
                    )
                    // Im Axis (Vertical)
                    drawLine(
                        color = SlateCardBorder,
                        start = Offset(center.x, 16f),
                        end = Offset(center.x, size.height - 16f),
                        strokeWidth = 1.5.dp.toPx()
                    )

                    // Draw all roots
                    roots.forEachIndexed { i, root ->
                        val isSelected = i == selectedIndex
                        val angle = root.angleRad
                        val rootX = center.x + radius * cos(angle).toFloat()
                        val rootY = center.y - radius * sin(angle).toFloat() // Flip y for screen coords
                        val rootPos = Offset(rootX, rootY)

                        // Radial spoke
                        drawLine(
                            color = if (isSelected) RadiantGold.copy(alpha = 0.8f) else ElectricCyan.copy(alpha = 0.25f),
                            start = center,
                            end = rootPos,
                            strokeWidth = if (isSelected) 2.dp.toPx() else 1.dp.toPx()
                        )

                        // Outer glow for selected root
                        if (isSelected) {
                            drawCircle(
                                color = RadiantGold.copy(alpha = 0.35f),
                                radius = 14.dp.toPx(),
                                center = rootPos
                            )
                        }

                        // Root point dot
                        drawCircle(
                            color = if (isSelected) RadiantGold else if (i == 0) ElectricCyan else Color.White,
                            radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                            center = rootPos
                        )
                    }

                    // Origin dot
                    drawCircle(
                        color = Color.White.copy(alpha = 0.5f),
                        radius = 2.5.dp.toPx(),
                        center = center
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Root Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F1A30),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(RadiantGold, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Root w${selectedRoot.k}:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = RadiantGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedRoot.formatted,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "θ = ${"%.1f".format(selectedRoot.angleDeg)}°",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                            color = ElectricCyan
                        )
                        Text(
                            text = "${"%.3f".format(selectedRoot.angleRad)} rad",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal Root Selector Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(roots) { root ->
                    val isSelected = root.k == selectedIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) RadiantGold.copy(alpha = 0.2f) else Color(0xFF0F1A30)
                            )
                            .border(
                                1.dp,
                                if (isSelected) RadiantGold else SlateCardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedIndex = root.k }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("chip_root_${root.k}")
                    ) {
                        Text(
                            text = "w${root.k}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) RadiantGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}
