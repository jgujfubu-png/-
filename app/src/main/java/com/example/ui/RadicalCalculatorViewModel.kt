package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalculationEntity
import com.example.data.HistoryRepository
import com.example.math.RadicalEngine
import com.example.math.RadicalResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ActiveInputTarget {
    INDEX,
    RADICAND
}

enum class CalculatorTab {
    CALCULATOR,
    ALL_ROOTS,
    SIMPLIFIER
}

data class RadicalUiState(
    val indexInput: String = "3",
    val radicandInput: String = "27",
    val activeTarget: ActiveInputTarget = ActiveInputTarget.RADICAND,
    val selectedTab: CalculatorTab = CalculatorTab.CALCULATOR,
    val precision: Int = 6,
    val result: RadicalResult = RadicalResult(isDefined = false),
    val history: List<CalculationEntity> = emptyList(),
    val showHistorySheet: Boolean = false,
    val showInfoDialog: Boolean = false,
    val snackbarMessage: String? = null
)

class RadicalCalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository
    private val _uiState = MutableStateFlow(RadicalUiState())
    val uiState: StateFlow<RadicalUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = HistoryRepository(db.calculationDao())

        // Collect calculation history
        viewModelScope.launch {
            repository.allHistory.collectLatest { list ->
                _uiState.update { it.copy(history = list) }
            }
        }

        // Compute initial result
        recalculate()
    }

    private fun recalculate() {
        val state = _uiState.value
        val n = state.indexInput.toDoubleOrNull()
        val x = state.radicandInput.toDoubleOrNull()

        if (n == null || x == null) {
            _uiState.update {
                it.copy(
                    result = RadicalResult(
                        isDefined = false,
                        errorMessage = "Please enter valid numbers for index and radicand.",
                        resultString = "—"
                    )
                )
            }
            return
        }

        val res = RadicalEngine.calculate(radicand = x, index = n, precision = state.precision)
        _uiState.update { it.copy(result = res) }
    }

    fun onDigit(digit: String) {
        val currentTarget = _uiState.value.activeTarget
        _uiState.update { state ->
            if (currentTarget == ActiveInputTarget.INDEX) {
                val current = if (state.indexInput == "0") "" else state.indexInput
                if (current.length < 10) state.copy(indexInput = current + digit) else state
            } else {
                val current = if (state.radicandInput == "0") "" else state.radicandInput
                if (current.length < 12) state.copy(radicandInput = current + digit) else state
            }
        }
        recalculate()
    }

    fun onDecimal() {
        val currentTarget = _uiState.value.activeTarget
        _uiState.update { state ->
            if (currentTarget == ActiveInputTarget.INDEX) {
                if (!state.indexInput.contains(".")) {
                    state.copy(indexInput = if (state.indexInput.isEmpty()) "0." else "${state.indexInput}.")
                } else state
            } else {
                if (!state.radicandInput.contains(".")) {
                    state.copy(radicandInput = if (state.radicandInput.isEmpty()) "0." else "${state.radicandInput}.")
                } else state
            }
        }
        recalculate()
    }

    fun onToggleSign() {
        val currentTarget = _uiState.value.activeTarget
        _uiState.update { state ->
            if (currentTarget == ActiveInputTarget.INDEX) {
                val newVal = when {
                    state.indexInput.startsWith("-") -> state.indexInput.removePrefix("-")
                    state.indexInput.isNotEmpty() && state.indexInput != "0" -> "-${state.indexInput}"
                    else -> state.indexInput
                }
                state.copy(indexInput = newVal)
            } else {
                val newVal = when {
                    state.radicandInput.startsWith("-") -> state.radicandInput.removePrefix("-")
                    state.radicandInput.isNotEmpty() && state.radicandInput != "0" -> "-${state.radicandInput}"
                    else -> state.radicandInput
                }
                state.copy(radicandInput = newVal)
            }
        }
        recalculate()
    }

    fun onBackspace() {
        val currentTarget = _uiState.value.activeTarget
        _uiState.update { state ->
            if (currentTarget == ActiveInputTarget.INDEX) {
                val newVal = if (state.indexInput.isNotEmpty()) state.indexInput.dropLast(1) else ""
                state.copy(indexInput = newVal)
            } else {
                val newVal = if (state.radicandInput.isNotEmpty()) state.radicandInput.dropLast(1) else ""
                state.copy(radicandInput = newVal)
            }
        }
        recalculate()
    }

    fun onClear() {
        val currentTarget = _uiState.value.activeTarget
        _uiState.update { state ->
            if (currentTarget == ActiveInputTarget.INDEX) {
                state.copy(indexInput = "")
            } else {
                state.copy(radicandInput = "")
            }
        }
        recalculate()
    }

    fun onClearAll() {
        _uiState.update {
            it.copy(indexInput = "2", radicandInput = "0")
        }
        recalculate()
    }

    fun setTarget(target: ActiveInputTarget) {
        _uiState.update { it.copy(activeTarget = target) }
    }

    fun swapInputs() {
        _uiState.update { state ->
            state.copy(
                indexInput = state.radicandInput,
                radicandInput = state.indexInput
            )
        }
        recalculate()
    }

    fun setPresetIndex(n: Double) {
        val formatted = RadicalEngine.formatNumber(n, 2)
        _uiState.update {
            it.copy(
                indexInput = formatted,
                activeTarget = ActiveInputTarget.RADICAND // Switch focus to radicand for rapid entry
            )
        }
        recalculate()
    }

    fun setPrecision(precision: Int) {
        _uiState.update { it.copy(precision = precision) }
        recalculate()
    }

    fun setSelectedTab(tab: CalculatorTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleHistorySheet(show: Boolean) {
        _uiState.update { it.copy(showHistorySheet = show) }
    }

    fun toggleInfoDialog(show: Boolean) {
        _uiState.update { it.copy(showInfoDialog = show) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun triggerCalculateAndSave() {
        recalculate()
        val state = _uiState.value
        val n = state.indexInput.toDoubleOrNull()
        val x = state.radicandInput.toDoubleOrNull()

        if (n != null && x != null && state.result.isDefined) {
            viewModelScope.launch {
                repository.saveCalculation(
                    CalculationEntity(
                        indexValue = n,
                        radicandValue = x,
                        resultText = state.result.resultString,
                        isComplex = state.result.isComplex,
                        simplifiedForm = state.result.simplifiedRadical?.displayString()
                    )
                )
                _uiState.update { it.copy(snackbarMessage = "Calculation saved to history") }
            }
        }
    }

    fun selectHistoryItem(item: CalculationEntity) {
        _uiState.update {
            it.copy(
                indexInput = RadicalEngine.formatNumber(item.indexValue, 4),
                radicandInput = RadicalEngine.formatNumber(item.radicandValue, 4),
                showHistorySheet = false
            )
        }
        recalculate()
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _uiState.update { it.copy(snackbarMessage = "History cleared") }
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "Radical Result") {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            _uiState.update { it.copy(snackbarMessage = "Copied to clipboard: $text") }
        } catch (_: Exception) {}
    }
}
