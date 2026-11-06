package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder

@Composable
fun RadicalKeypad(
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onToggleSign: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onPresetIndex: (Double) -> Unit,
    onCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    fun triggerHaptic() {
        try {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        } catch (_: Exception) {}
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("radical_keypad"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Preset Row: Quick Root Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetButton(
                label = "√ (n=2)",
                onClick = { triggerHaptic(); onPresetIndex(2.0) },
                modifier = Modifier.weight(1f),
                testTag = "preset_root_2"
            )
            PresetButton(
                label = "³√ (n=3)",
                onClick = { triggerHaptic(); onPresetIndex(3.0) },
                modifier = Modifier.weight(1f),
                testTag = "preset_root_3"
            )
            PresetButton(
                label = "⁴√ (n=4)",
                onClick = { triggerHaptic(); onPresetIndex(4.0) },
                modifier = Modifier.weight(1f),
                testTag = "preset_root_4"
            )
            PresetButton(
                label = "⁵√ (n=5)",
                onClick = { triggerHaptic(); onPresetIndex(5.0) },
                modifier = Modifier.weight(1f),
                testTag = "preset_root_5"
            )
        }

        // Row 1: C, ±, π, ⌫
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(
                label = "C",
                textColor = MaterialTheme.colorScheme.error,
                backgroundColor = Color(0x29FF4D6D),
                borderColor = Color(0x55FF4D6D),
                onClick = { triggerHaptic(); onClear() },
                modifier = Modifier.weight(1f),
                testTag = "key_clear"
            )
            KeypadButton(
                label = "±",
                textColor = ElectricCyan,
                backgroundColor = Color(0xFF162544),
                onClick = { triggerHaptic(); onToggleSign() },
                modifier = Modifier.weight(1f),
                testTag = "key_sign"
            )
            KeypadButton(
                label = "π",
                textColor = RadiantGold,
                backgroundColor = Color(0xFF162544),
                onClick = { triggerHaptic(); onDigit("3.14159265") },
                modifier = Modifier.weight(1f),
                testTag = "key_pi"
            )
            KeypadIconButton(
                icon = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Backspace",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                backgroundColor = Color(0xFF162544),
                onClick = { triggerHaptic(); onBackspace() },
                modifier = Modifier.weight(1f),
                testTag = "key_backspace"
            )
        }

        // Row 2: 7, 8, 9, e
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadNumberButton(number = "7", onClick = { triggerHaptic(); onDigit("7") }, modifier = Modifier.weight(1f))
            KeypadNumberButton(number = "8", onClick = { triggerHaptic(); onDigit("8") }, modifier = Modifier.weight(1f))
            KeypadNumberButton(number = "9", onClick = { triggerHaptic(); onDigit("9") }, modifier = Modifier.weight(1f))
            KeypadButton(
                label = "e",
                textColor = RadiantGold,
                backgroundColor = Color(0xFF162544),
                onClick = { triggerHaptic(); onDigit("2.71828182") },
                modifier = Modifier.weight(1f),
                testTag = "key_e"
            )
        }

        // Row 3: 4, 5, 6, Inverse (0.5)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadNumberButton(number = "4", onClick = { triggerHaptic(); onDigit("4") }, modifier = Modifier.weight(1f))
            KeypadNumberButton(number = "5", onClick = { triggerHaptic(); onDigit("5") }, modifier = Modifier.weight(1f))
            KeypadNumberButton(number = "6", onClick = { triggerHaptic(); onDigit("6") }, modifier = Modifier.weight(1f))
            KeypadButton(
                label = "½",
                textColor = ElectricCyan,
                backgroundColor = Color(0xFF162544),
                onClick = { triggerHaptic(); onDigit("0.5") },
                modifier = Modifier.weight(1f),
                testTag = "key_half"
            )
        }

        // Row 4: 1, 2, 3, Calculate/Equals Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadNumberButton(number = "1", onClick = { triggerHaptic(); onDigit("1") }, modifier = Modifier.weight(1f))
            KeypadNumberButton(number = "2", onClick = { triggerHaptic(); onDigit("2") }, modifier = Modifier.weight(1f))
            KeypadNumberButton(number = "3", onClick = { triggerHaptic(); onDigit("3") }, modifier = Modifier.weight(1f))
            KeypadIconButton(
                icon = Icons.Default.Calculate,
                contentDescription = "Calculate",
                tint = Color(0xFF080D1A),
                backgroundColor = ElectricCyan,
                borderColor = ElectricCyan,
                onClick = { triggerHaptic(); onCalculate() },
                modifier = Modifier.weight(1f),
                testTag = "key_equals"
            )
        }

        // Row 5: 0, . (Decimal)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadNumberButton(
                number = "0",
                onClick = { triggerHaptic(); onDigit("0") },
                modifier = Modifier.weight(2f)
            )
            KeypadButton(
                label = ".",
                textColor = MaterialTheme.colorScheme.onSurface,
                backgroundColor = Color(0xFF162544),
                onClick = { triggerHaptic(); onDecimal() },
                modifier = Modifier.weight(1f),
                testTag = "key_dot"
            )
            KeypadButton(
                label = "00",
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                backgroundColor = Color(0xFF162544),
                onClick = { triggerHaptic(); onDigit("00") },
                modifier = Modifier.weight(1f),
                testTag = "key_double_zero"
            )
        }
    }
}

@Composable
private fun PresetButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        modifier = modifier
            .height(38.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F1A30),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
    ) {
        Box(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = RadiantGold,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun KeypadNumberButton(
    number: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    KeypadButton(
        label = number,
        textColor = MaterialTheme.colorScheme.onSurface,
        backgroundColor = SlateCard,
        borderColor = SlateCardBorder,
        onClick = onClick,
        modifier = modifier,
        testTag = "key_$number"
    )
}

@Composable
private fun KeypadButton(
    label: String,
    textColor: Color,
    backgroundColor: Color,
    borderColor: Color = SlateCardBorder,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = textColor
            )
        )
    }
}

@Composable
private fun KeypadIconButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    backgroundColor: Color,
    borderColor: Color = SlateCardBorder,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}
