package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ActiveInputTarget
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder

@Composable
fun RadicalDisplayCard(
    indexInput: String,
    radicandInput: String,
    activeTarget: ActiveInputTarget,
    onSelectTarget: (ActiveInputTarget) -> Unit,
    onSwap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isIndexActive = activeTarget == ActiveInputTarget.INDEX
    val isRadicandActive = activeTarget == ActiveInputTarget.RADICAND

    val indexBorderColor by animateColorAsState(
        targetValue = if (isIndexActive) RadiantGold else Color.Transparent,
        animationSpec = tween(250),
        label = "indexBorder"
    )

    val radicandBorderColor by animateColorAsState(
        targetValue = if (isRadicandActive) ElectricCyan else Color.Transparent,
        animationSpec = tween(250),
        label = "radicandBorder"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("radical_display_card"),
        shape = RoundedCornerShape(24.dp),
        color = SlateCard,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, SlateCardBorder),
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with target indicator and swap button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TargetChip(
                        label = "Index (n)",
                        isActive = isIndexActive,
                        activeColor = RadiantGold,
                        onClick = { onSelectTarget(ActiveInputTarget.INDEX) },
                        testTag = "chip_target_index"
                    )
                    TargetChip(
                        label = "Radicand (x)",
                        isActive = isRadicandActive,
                        activeColor = ElectricCyan,
                        onClick = { onSelectTarget(ActiveInputTarget.RADICAND) },
                        testTag = "chip_target_radicand"
                    )
                }

                IconButton(
                    onClick = onSwap,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF0F1A30), CircleShape)
                        .testTag("btn_swap_inputs")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap Index and Radicand",
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Mathematical Radical Display: ⁿ√x
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Superscript Index n
                Box(
                    modifier = Modifier
                        .offset(y = (-16).dp, x = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isIndexActive) Color(0x33FFB703) else Color(0x1AFFFFFF)
                        )
                        .border(1.5.dp, indexBorderColor, RoundedCornerShape(8.dp))
                        .clickable { onSelectTarget(ActiveInputTarget.INDEX) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("input_box_index"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = indexInput.ifEmpty { "n" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isIndexActive) RadiantGold else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Custom Drawn Radical Symbol + Radicand with vinculum
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    // Radical Sign '√'
                    Text(
                        text = "√",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 52.sp,
                            fontWeight = FontWeight.Light,
                            color = ElectricCyan
                        ),
                        modifier = Modifier.offset(y = (-2).dp)
                    )

                    // Radicand Box with vinculum (top bar) drawn over it
                    Box(
                        modifier = Modifier
                            .offset(x = (-4).dp)
                            .drawBehind {
                                // Draw vinculum horizontal line across the top
                                drawLine(
                                    color = ElectricCyan,
                                    start = Offset(0f, 6.dp.toPx()),
                                    end = Offset(size.width, 6.dp.toPx()),
                                    strokeWidth = 3.dp.toPx()
                                )
                            }
                            .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                            .background(
                                if (isRadicandActive) Color(0x2A00E5FF) else Color(0x1AFFFFFF)
                            )
                            .border(1.5.dp, radicandBorderColor, RoundedCornerShape(8.dp))
                            .clickable { onSelectTarget(ActiveInputTarget.RADICAND) }
                            .padding(start = 12.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
                            .testTag("input_box_radicand"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = radicandInput.ifEmpty { "x" },
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                color = if (isRadicandActive) ElectricCyan else MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Hint Text
            Text(
                text = if (isIndexActive) "Editing Index (degree of root)" else "Editing Radicand (number under root)",
                style = MaterialTheme.typography.labelSmall,
                color = if (isIndexActive) RadiantGold.copy(alpha = 0.8f) else ElectricCyan.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun TargetChip(
    label: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isActive) activeColor.copy(alpha = 0.2f) else Color(0xFF0F1A30)
            )
            .border(
                1.dp,
                if (isActive) activeColor else SlateCardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
