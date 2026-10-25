package org.foss.resistorcalculator

import android.content.Context

/**
 * Result of a resistor color code calculation.
 */
data class ResistorResult(
    val nominalOhms: Double,
    val tolerance: Double,          // fraction, e.g. 0.05 = ±5%
    val tempcoPpm: Int?,            // ppm/°C, null when not applicable
    val bandCount: Int,
    val minOhms: Double,
    val maxOhms: Double
) {
    fun formattedValue(context: Context): String = formatOhms(nominalOhms, context)

    fun formattedMin(context: Context): String = formatOhms(minOhms, context)

    fun formattedMax(context: Context): String = formatOhms(maxOhms, context)

    fun formattedTolerance(context: Context): String {
        val pct = tolerance * 100
        val pctText = if (pct % 1.0 == 0.0) pct.toInt().toString()
        else java.text.DecimalFormat("#.##").format(pct)
        return context.getString(R.string.percent, pctText)
    }

    fun formattedTempco(context: Context): String {
        return if (tempcoPpm == null) "—" else "$tempcoPpm ${context.getString(R.string.unit_ppm)}"
    }

    companion object {
        private fun formatOhms(ohms: Double, context: Context): String {
            val mag = if (ohms < 0) -ohms else ohms
            val unit: String
            val value: Double
            when {
                mag >= 1e9 -> { unit = context.getString(R.string.unit_gohm); value = ohms / 1e9 }
                mag >= 1e6 -> { unit = context.getString(R.string.unit_mohm); value = ohms / 1e6 }
                mag >= 1e3 -> { unit = context.getString(R.string.unit_kohm); value = ohms / 1e3 }
                else -> { unit = context.getString(R.string.unit_ohm); value = ohms }
            }
            // Trim trailing zeros for a clean readout
            val formatted = if (value % 1.0 == 0.0) value.toInt().toString()
            else java.text.DecimalFormat("#.###").format(value)
            return "$formatted $unit"
        }
    }
}
