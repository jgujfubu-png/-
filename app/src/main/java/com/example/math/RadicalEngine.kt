package com.example.math

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt

data class ComplexNumber(
    val real: Double,
    val imag: Double
) {
    fun format(precision: Int): String {
        val r = RadicalEngine.formatNumber(real, precision)
        val i = RadicalEngine.formatNumber(abs(imag), precision)

        val isZeroReal = abs(real) < 1e-11
        val isZeroImag = abs(imag) < 1e-11

        return when {
            isZeroReal && isZeroImag -> "0"
            isZeroImag -> r
            isZeroReal -> {
                if (imag < 0) "-${if (i == "1") "" else i}i" else "${if (i == "1") "" else i}i"
            }
            else -> {
                val sign = if (imag >= 0) "+" else "-"
                val imagPart = if (i == "1") "i" else "${i}i"
                "$r $sign $imagPart"
            }
        }
    }

    val modulus: Double get() = sqrt(real * real + imag * imag)
}

data class ComplexRoot(
    val k: Int,
    val real: Double,
    val imag: Double,
    val angleRad: Double,
    val angleDeg: Double,
    val formatted: String
)

data class SimplifiedRadical(
    val outside: Long,
    val inside: Long,
    val index: Int,
    val isNegative: Boolean,
    val steps: List<String>
) {
    fun displayString(): String {
        val sign = if (isNegative) "-" else ""
        return when {
            inside == 1L -> "$sign$outside"
            outside == 1L -> {
                if (index == 2) "${sign}√$inside" else "$sign$index√$inside"
            }
            else -> {
                if (index == 2) "$sign$outside√$inside" else "$sign$outside · $index√$inside"
            }
        }
    }
}

data class RadicalResult(
    val isDefined: Boolean,
    val errorMessage: String? = null,
    val resultString: String = "",
    val isComplex: Boolean = false,
    val complexValue: ComplexNumber? = null,
    val isRealOddRoot: Boolean = false,
    val isExactInteger: Boolean = false,
    val formulaNotation: String = "",
    val explanation: String = "",
    val verification: String = "",
    val simplifiedRadical: SimplifiedRadical? = null,
    val allRoots: List<ComplexRoot> = emptyList()
)

object RadicalEngine {

    /**
     * Calculates the n-th root of x (index-th root of radicand).
     * Supports arbitrary index (integers, negative, decimal) and arbitrary radicands.
     */
    fun calculate(radicand: Double, index: Double, precision: Int = 6): RadicalResult {
        // Edge Case 1: Index is 0
        if (abs(index) < 1e-12) {
            return RadicalResult(
                isDefined = false,
                errorMessage = "Index cannot be zero (division by zero: x^(1/0) is undefined).",
                resultString = "Undefined",
                formulaNotation = "x^(1/0)",
                explanation = "A radical with index 0 corresponds to raising the radicand to 1/0, which is undefined."
            )
        }

        // Edge Case 2: Radicand is 0
        if (abs(radicand) < 1e-12) {
            if (index < 0) {
                return RadicalResult(
                    isDefined = false,
                    errorMessage = "0 raised to a negative exponent (1/n) is undefined (division by zero).",
                    resultString = "Undefined",
                    formulaNotation = "0^(1/$index)",
                    explanation = "0 raised to negative power implies 1 / 0, which is undefined."
                )
            } else {
                return RadicalResult(
                    isDefined = true,
                    resultString = "0",
                    isComplex = false,
                    isExactInteger = true,
                    formulaNotation = "0^(1/${formatNumber(index, 4)}) = 0",
                    explanation = "Any positive root of 0 is equal to 0.",
                    verification = "0^($index) = 0"
                )
            }
        }

        val exponent = 1.0 / index
        val formula = "(${formatNumber(radicand, 4)})^(1/${formatNumber(index, 4)})"

        // Check if index is an integer
        val isIntegerIndex = abs(index - kotlin.math.round(index)) < 1e-9
        val intIndex = kotlin.math.round(index).toInt()
        val isOddIntegerIndex = isIntegerIndex && (abs(intIndex) % 2 == 1)

        // Case 3: Positive Radicand (x > 0)
        if (radicand > 0) {
            val root = exp(exponent * ln(radicand))
            val isExact = abs(root - kotlin.math.round(root)) < 1e-9 && isIntegerIndex

            // Simplify radical if integers
            val simplified = if (isIntegerIndex && intIndex >= 2 && abs(radicand - kotlin.math.round(radicand)) < 1e-9) {
                simplifyRadical(kotlin.math.round(radicand).toLong(), intIndex)
            } else null

            // Generate all roots if integer index between 2 and 20
            val allRootsList = if (isIntegerIndex && intIndex in 2..20) {
                generateAllRoots(radicand, intIndex, precision)
            } else emptyList()

            val formattedRes = formatNumber(root, precision)
            val verificationText = "(${formattedRes})^${formatNumber(index, 4)} ≈ ${formatNumber(radicand, precision)}"

            return RadicalResult(
                isDefined = true,
                resultString = formattedRes,
                isComplex = false,
                isExactInteger = isExact,
                complexValue = ComplexNumber(root, 0.0),
                formulaNotation = formula,
                explanation = "Calculated via exponential definition: x^(1/n) = e^((1/n)·ln(x)).",
                verification = verificationText,
                simplifiedRadical = simplified,
                allRoots = allRootsList
            )
        }

        // Case 4: Negative Radicand (x < 0)
        val absX = abs(radicand)

        if (isOddIntegerIndex) {
            // Odd integer index of negative number has a REAL solution:
            // e.g. (-8)^(1/3) = -2, (-32)^(1/5) = -2, (-8)^(-1/3) = -0.5
            val absRoot = exp(exponent * ln(absX))
            val realRoot = -absRoot
            val isExact = abs(realRoot - kotlin.math.round(realRoot)) < 1e-9

            val simplified = if (intIndex >= 3 && abs(radicand - kotlin.math.round(radicand)) < 1e-9) {
                simplifyRadical(kotlin.math.round(absX).toLong(), intIndex, isNegative = true)
            } else null

            val allRootsList = if (intIndex in 2..20) {
                generateAllRoots(radicand, intIndex, precision)
            } else emptyList()

            val formattedRes = formatNumber(realRoot, precision)
            val verificationText = "(${formattedRes})^$intIndex = ${formatNumber(radicand, precision)}"

            return RadicalResult(
                isDefined = true,
                resultString = formattedRes,
                isComplex = false,
                isRealOddRoot = true,
                isExactInteger = isExact,
                complexValue = ComplexNumber(realRoot, 0.0),
                formulaNotation = formula,
                explanation = "Odd root of a negative radicand produces a real negative number: -|x|^(1/n).",
                verification = verificationText,
                simplifiedRadical = simplified,
                allRoots = allRootsList
            )
        } else {
            // Even integer or decimal index of negative number:
            // Produces a COMPLEX root: |x|^(1/n) * (cos(pi/n) + i * sin(pi/n))
            val r = exp(exponent * ln(absX))
            val angle = PI / index
            val realPart = r * cos(angle)
            val imagPart = r * sin(angle)
            val c = ComplexNumber(realPart, imagPart)

            val allRootsList = if (isIntegerIndex && intIndex in 2..20) {
                generateAllRoots(radicand, intIndex, precision)
            } else emptyList()

            val simplified = if (isIntegerIndex && intIndex == 2 && abs(radicand - kotlin.math.round(radicand)) < 1e-9) {
                simplifyRadical(kotlin.math.round(absX).toLong(), 2)
            } else null

            return RadicalResult(
                isDefined = true,
                resultString = c.format(precision),
                isComplex = true,
                complexValue = c,
                formulaNotation = "$formula = |x|^(1/n) · [cos(π/n) + i·sin(π/n)]",
                explanation = "Even/fractional roots of negative numbers have no real solution. The principal complex root is evaluated using Euler's formula.",
                verification = "(Principal complex root in polar form, modulus |z| = ${formatNumber(r, 4)})",
                simplifiedRadical = simplified,
                allRoots = allRootsList
            )
        }
    }

    /**
     * Generates all n distinct complex roots for a positive integer index n (2..20).
     * w_k = r^(1/n) * e^(i * (theta_0 + 2*pi*k) / n) for k = 0 until n
     */
    fun generateAllRoots(radicand: Double, n: Int, precision: Int = 4): List<ComplexRoot> {
        if (n <= 1) return emptyList()

        val absX = abs(radicand)
        val r = exp((1.0 / n) * ln(absX))
        val theta0 = if (radicand >= 0) 0.0 else PI

        return (0 until n).map { k ->
            val angle = (theta0 + 2.0 * PI * k) / n
            // Normalize angle to (-PI, PI]
            var normAngle = angle % (2.0 * PI)
            if (normAngle > PI) normAngle -= 2.0 * PI

            val real = r * cos(angle)
            val imag = r * sin(angle)
            val angleDeg = Math.toDegrees(normAngle)

            val complex = ComplexNumber(real, imag)
            ComplexRoot(
                k = k,
                real = real,
                imag = imag,
                angleRad = normAngle,
                angleDeg = angleDeg,
                formatted = complex.format(precision)
            )
        }
    }

    /**
     * Simplifies integer radicals: a * n√b
     * Example: √72 = 6√2, ³√54 = 3³√2
     */
    fun simplifyRadical(radicand: Long, index: Int, isNegative: Boolean = false): SimplifiedRadical? {
        if (index < 2 || radicand <= 1) return null

        val factors = primeFactorization(radicand)
        var outside = 1L
        var inside = 1L
        val steps = mutableListOf<String>()

        steps.add("Prime factorization of $radicand: ${factors.entries.joinToString(" × ") { "${it.key}^${it.value}" }}")

        for ((prime, count) in factors) {
            val extractedPower = count / index
            val remainingPower = count % index

            if (extractedPower > 0) {
                var multiplier = 1L
                for (i in 0 until extractedPower) {
                    multiplier *= prime
                }
                outside *= multiplier
                steps.add("Extract factor: $prime^$count = $prime^(${extractedPower * index}) × $prime^$remainingPower → move $prime^$extractedPower = $multiplier outside the radical.")
            }

            for (i in 0 until remainingPower) {
                inside *= prime
            }
        }

        // Only return if it actually simplified or was a perfect n-th power
        if (outside > 1) {
            val rootSymbol = if (index == 2) "√" else "$index√"
            if (inside == 1L) {
                steps.add("Perfect $index-th power! $rootSymbol$radicand = $outside")
            } else {
                steps.add("Simplified radical form: $outside$rootSymbol$inside")
            }
            return SimplifiedRadical(
                outside = outside,
                inside = inside,
                index = index,
                isNegative = isNegative,
                steps = steps
            )
        }

        return null
    }

    private fun primeFactorization(n: Long): Map<Long, Int> {
        val factors = mutableMapOf<Long, Int>()
        var d = 2L
        var temp = n
        while (d * d <= temp) {
            while (temp % d == 0L) {
                factors[d] = (factors[d] ?: 0) + 1
                temp /= d
            }
            d++
        }
        if (temp > 1) {
            factors[temp] = (factors[temp] ?: 0) + 1
        }
        return factors
    }

    fun formatNumber(num: Double, precision: Int): String {
        if (abs(num) < 1e-12) return "0"
        if (num.isNaN()) return "NaN"
        if (num.isInfinite()) return if (num > 0) "∞" else "-∞"

        return try {
            val bd = BigDecimal(num).setScale(precision, RoundingMode.HALF_UP)
            val stripped = bd.stripTrailingZeros()
            // If absolute value is extremely large or tiny, stripTrailingZeros toPlainString
            stripped.toPlainString()
        } catch (e: Exception) {
            "%.${precision}f".format(num).trimEnd('0').trimEnd('.')
        }
    }
}
