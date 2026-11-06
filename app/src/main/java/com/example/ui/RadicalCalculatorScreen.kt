package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ComplexPlaneView
import com.example.ui.components.HistorySheet
import com.example.ui.components.RadicalDisplayCard
import com.example.ui.components.RadicalKeypad
import com.example.ui.components.RadicalSimplifierCard
import com.example.ui.theme.DeepNavySurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadicalCalculatorScreen(
    viewModel: RadicalCalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showPrecisionMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF162544))
                                .border(1.dp, ElectricCyan, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ⁿ√x",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan
                                )
                            )
                        }
                        Column {
                            Text(
                                text = "Radical Calculator",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Arbitrary Index & Complex Roots",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Precision Selector Dropdown
                    Box {
                        IconButton(
                            onClick = { showPrecisionMenu = true },
                            modifier = Modifier.testTag("btn_precision_menu")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Decimal Precision",
                                tint = RadiantGold
                            )
                        }
                        DropdownMenu(
                            expanded = showPrecisionMenu,
                            onDismissRequest = { showPrecisionMenu = false },
                            modifier = Modifier.background(DeepNavySurface)
                        ) {
                            Text(
                                text = "Decimal Precision",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ElectricCyan,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            listOf(2, 4, 6, 8, 10, 12).forEach { p ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "$p decimal places",
                                            color = if (uiState.precision == p) RadiantGold else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    trailingIcon = {
                                        if (uiState.precision == p) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = RadiantGold
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.setPrecision(p)
                                        showPrecisionMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Calculation History Icon with Badge
                    IconButton(
                        onClick = { viewModel.toggleHistorySheet(true) },
                        modifier = Modifier.testTag("btn_open_history")
                    ) {
                        BadgedBox(
                            badge = {
                                if (uiState.history.isNotEmpty()) {
                                    Badge(
                                        containerColor = ElectricCyan,
                                        contentColor = ObsidianBg
                                    ) {
                                        Text("${uiState.history.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Calculation History",
                                tint = ElectricCyan
                            )
                        }
                    }

                    // Information / Math rules dialog
                    IconButton(
                        onClick = { viewModel.toggleInfoDialog(true) },
                        modifier = Modifier.testTag("btn_open_info")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Radical Rules Guide",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBg,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Respect navigation bars inset at bottom
            Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars))
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Navigation Tabs
            PrimaryTabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                containerColor = ObsidianBg,
                contentColor = ElectricCyan,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(uiState.selectedTab.ordinal),
                        color = ElectricCyan,
                        width = 48.dp
                    )
                },
                divider = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(SlateCardBorder)
                    )
                }
            ) {
                Tab(
                    selected = uiState.selectedTab == CalculatorTab.CALCULATOR,
                    onClick = { viewModel.setSelectedTab(CalculatorTab.CALCULATOR) },
                    text = {
                        Text(
                            text = "Calculator",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (uiState.selectedTab == CalculatorTab.CALCULATOR) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    modifier = Modifier.testTag("tab_calculator")
                )
                Tab(
                    selected = uiState.selectedTab == CalculatorTab.ALL_ROOTS,
                    onClick = { viewModel.setSelectedTab(CalculatorTab.ALL_ROOTS) },
                    text = {
                        Text(
                            text = "All Roots (Argand)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (uiState.selectedTab == CalculatorTab.ALL_ROOTS) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    modifier = Modifier.testTag("tab_all_roots")
                )
                Tab(
                    selected = uiState.selectedTab == CalculatorTab.SIMPLIFIER,
                    onClick = { viewModel.setSelectedTab(CalculatorTab.SIMPLIFIER) },
                    text = {
                        Text(
                            text = "Simplifier",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (uiState.selectedTab == CalculatorTab.SIMPLIFIER) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    modifier = Modifier.testTag("tab_simplifier")
                )
            }

            // Tab Content
            when (uiState.selectedTab) {
                CalculatorTab.CALCULATOR -> {
                    CalculatorMainContent(
                        uiState = uiState,
                        viewModel = viewModel,
                        onCopy = { text -> viewModel.copyToClipboard(context, text) }
                    )
                }
                CalculatorTab.ALL_ROOTS -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RadicalDisplayCard(
                            indexInput = uiState.indexInput,
                            radicandInput = uiState.radicandInput,
                            activeTarget = uiState.activeTarget,
                            onSelectTarget = { viewModel.setTarget(it) },
                            onSwap = { viewModel.swapInputs() }
                        )

                        ComplexPlaneView(
                            roots = uiState.result.allRoots,
                            index = uiState.indexInput.toDoubleOrNull() ?: 2.0,
                            radicand = uiState.radicandInput.toDoubleOrNull() ?: 1.0
                        )
                    }
                }
                CalculatorTab.SIMPLIFIER -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RadicalDisplayCard(
                            indexInput = uiState.indexInput,
                            radicandInput = uiState.radicandInput,
                            activeTarget = uiState.activeTarget,
                            onSelectTarget = { viewModel.setTarget(it) },
                            onSwap = { viewModel.swapInputs() }
                        )

                        if (uiState.result.simplifiedRadical != null) {
                            RadicalSimplifierCard(
                                simplified = uiState.result.simplifiedRadical,
                                radicand = uiState.radicandInput.toDoubleOrNull() ?: 0.0,
                                index = uiState.indexInput.toDoubleOrNull() ?: 2.0
                            )
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
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
                                    Icon(
                                        imageVector = Icons.Default.Functions,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Radical Already in Simplest Form",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Simplifier works for integer radicands and indices with extractable prime powers (e.g., √72 = 6√2, ³√54 = 3³√2).",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // History Sheet
    if (uiState.showHistorySheet) {
        HistorySheet(
            historyList = uiState.history,
            sheetState = historySheetState,
            onDismiss = { viewModel.toggleHistorySheet(false) },
            onSelectCalculation = { viewModel.selectHistoryItem(it) },
            onCopyResult = { viewModel.copyToClipboard(context, it) },
            onClearHistory = { viewModel.clearHistory() }
        )
    }

    // Info Dialog
    if (uiState.showInfoDialog) {
        RadicalInfoDialog(onDismiss = { viewModel.toggleInfoDialog(false) })
    }
}

@Composable
private fun CalculatorMainContent(
    uiState: RadicalUiState,
    viewModel: RadicalCalculatorViewModel,
    onCopy: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Visual Radical Mathematical Input Card (ⁿ√x)
        RadicalDisplayCard(
            indexInput = uiState.indexInput,
            radicandInput = uiState.radicandInput,
            activeTarget = uiState.activeTarget,
            onSelectTarget = { viewModel.setTarget(it) },
            onSwap = { viewModel.swapInputs() }
        )

        // Result Banner Card
        ResultDisplayCard(
            result = uiState.result,
            onCopy = onCopy
        )

        // Exact Simplified Form Chip (if available)
        if (uiState.result.simplifiedRadical != null) {
            RadicalSimplifierCard(
                simplified = uiState.result.simplifiedRadical,
                radicand = uiState.radicandInput.toDoubleOrNull() ?: 0.0,
                index = uiState.indexInput.toDoubleOrNull() ?: 2.0
            )
        }

        // Tactile Scientific Keypad
        RadicalKeypad(
            onDigit = { viewModel.onDigit(it) },
            onDecimal = { viewModel.onDecimal() },
            onToggleSign = { viewModel.onToggleSign() },
            onBackspace = { viewModel.onBackspace() },
            onClear = { viewModel.onClear() },
            onPresetIndex = { viewModel.setPresetIndex(it) },
            onCalculate = { viewModel.triggerCalculateAndSave() }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ResultDisplayCard(
    result: com.example.math.RadicalResult,
    onCopy: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("result_card"),
        shape = RoundedCornerShape(20.dp),
        color = SlateCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "RESULT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Classification Badges
                    if (result.isDefined) {
                        if (result.isComplex) {
                            BadgeChip(label = "Complex Root (a + bi)", color = ElectricCyan)
                        } else if (result.isRealOddRoot) {
                            BadgeChip(label = "Real Odd Root", color = EmeraldGreen)
                        } else {
                            BadgeChip(label = "Real Root", color = EmeraldGreen)
                        }
                    } else {
                        BadgeChip(label = "Undefined", color = MaterialTheme.colorScheme.error)
                    }
                }

                if (result.isDefined) {
                    IconButton(
                        onClick = { onCopy(result.resultString) },
                        modifier = Modifier.size(36.dp).testTag("btn_copy_result")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy result",
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Result Value
            Text(
                text = result.resultString,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = if (result.isDefined) {
                        if (result.isComplex) ElectricCyan else EmeraldGreen
                    } else MaterialTheme.colorScheme.error
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Formula / Explanation note
            if (result.formulaNotation.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = result.formulaNotation,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (result.verification.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Verification: ${result.verification}",
                    style = MaterialTheme.typography.labelSmall,
                    color = RadiantGold.copy(alpha = 0.9f)
                )
            }

            if (result.errorMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun BadgeChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = color
            )
        )
    }
}

@Composable
private fun RadicalInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepNavySurface,
        title = {
            Text(
                text = "Mathematical Radical Rules",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = ElectricCyan
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRuleItem(
                    title = "1. Definition of a Radical",
                    body = "The radical ⁿ√x is mathematically equivalent to x^(1/n). The index n represents the degree of the root."
                )
                InfoRuleItem(
                    title = "2. Zero Index (n = 0)",
                    body = "An index of 0 means calculating x^(1/0). Because division by zero is undefined, ⁰√x is undefined for all x."
                )
                InfoRuleItem(
                    title = "3. Negative Radicand & Odd Index",
                    body = "When x < 0 and n is an odd integer (e.g., 3, 5, 7), there is a unique real negative root: ∛(-8) = -2 because (-2)³ = -8."
                )
                InfoRuleItem(
                    title = "4. Negative Radicand & Even Index",
                    body = "When x < 0 and n is an even integer or non-integer, no real root exists. The principal root is a complex number a + bi via Euler's formula: |x|^(1/n) · [cos(π/n) + i·sin(π/n)]."
                )
                InfoRuleItem(
                    title = "5. Negative Index",
                    body = "A negative index ⁻ⁿ√x represents x^(-1/n) = 1 / (ⁿ√x). For instance, ⁻²√100 = 100^(-1/2) = 0.1."
                )
                InfoRuleItem(
                    title = "6. All n Roots of a Number",
                    body = "By the Fundamental Theorem of Algebra, every non-zero number has exactly n distinct complex roots, symmetrically spaced around a circle of radius |x|^(1/n) in the Argand plane."
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got It", color = ElectricCyan, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun InfoRuleItem(title: String, body: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = RadiantGold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
