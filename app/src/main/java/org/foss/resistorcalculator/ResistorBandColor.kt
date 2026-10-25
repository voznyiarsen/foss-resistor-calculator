package org.foss.resistorcalculator

/**
 * A single resistor color band entry from the standard EIA color code.
 *
 * Colors are sampled from the original Flutter artwork so the selector dots
 * match the resistor rendering exactly.
 *
 * digit      – significant digit (0‑9), null for colors that only apply as multiplier/tolerance
 * multiplier – multiplier factor, null for digit‑only colors
 * tolerance  – tolerance as a fraction (e.g. 0.01 = ±1%), null when not a tolerance color
 * tempcoPpm  – temperature coefficient in ppm/°C (6th band only), null when not applicable
 * textDark   – whether dark text is legible on this color swatch
 */
data class ResistorBandColor(
    val key: String,
    val name: String,
    val color: Int,
    val digit: Int?,
    val multiplier: Double?,
    val tolerance: Double?,
    val tempcoPpm: Int?,
    val textDark: Boolean
)

object ResistorBandColors {
    private fun c(
        key: String,
        name: String,
        color: Int,
        digit: Int? = null,
        multiplier: Double? = null,
        tolerance: Double? = null,
        tempcoPpm: Int? = null,
        textDark: Boolean = false
    ) = ResistorBandColor(key, name, color, digit, multiplier, tolerance, tempcoPpm, textDark)

    // Colours sampled from the original assets (resistors/1_*.png)
    val BLACK = c("black", "Black", 0xFF432C1C.toInt(), digit = 0, multiplier = 1.0)
    val BROWN = c("brown", "Brown", 0xFF754714.toInt(), digit = 1, multiplier = 10.0,
        tolerance = 0.01, tempcoPpm = 100)
    val RED = c("red", "Red", 0xFFE71E14.toInt(), digit = 2, multiplier = 100.0,
        tolerance = 0.02, tempcoPpm = 50)
    val ORANGE = c("orange", "Orange", 0xFFE77723.toInt(), digit = 3, multiplier = 1000.0,
        tempcoPpm = 15, textDark = true)
    val YELLOW = c("yellow", "Yellow", 0xFFFFEF14.toInt(), digit = 4, multiplier = 10000.0,
        tempcoPpm = 25, textDark = true)
    val GREEN = c("green", "Green", 0xFF467036.toInt(), digit = 5, multiplier = 100000.0,
        tolerance = 0.005, tempcoPpm = 20)
    val BLUE = c("blue", "Blue", 0xFF3053B5.toInt(), digit = 6, multiplier = 1000000.0,
        tolerance = 0.0025, tempcoPpm = 10)
    val VIOLET = c("violet", "Violet", 0xFF801E8B.toInt(), digit = 7, multiplier = 10000000.0,
        tolerance = 0.001, tempcoPpm = 5)
    val GREY = c("grey", "Grey", 0xFF7F7066.toInt(), digit = 8, multiplier = 100000000.0,
        tolerance = 0.0005)
    val WHITE = c("white", "White", 0xFFFFFFFF.toInt(), digit = 9, multiplier = 1000000000.0,
        textDark = true)

    // Multiplier / tolerance colours
    val GOLD = c("gold", "Gold", 0xFFBD871D.toInt(), multiplier = 0.1, tolerance = 0.05, textDark = true)
    val SILVER = c("silver", "Silver", 0xFFC6B7AD.toInt(), multiplier = 0.01, tolerance = 0.10)
    val NONE = c("none", "None", 0xFFCD8751.toInt(), tolerance = 0.20, textDark = true)

    // All colors, ordered for display
    val ALL = listOf(BLACK, BROWN, RED, ORANGE, YELLOW, GREEN, BLUE, VIOLET, GREY, WHITE, GOLD, SILVER, NONE)

    // Colors usable as a significant digit
    val DIGIT_COLORS = listOf(BLACK, BROWN, RED, ORANGE, YELLOW, GREEN, BLUE, VIOLET, GREY, WHITE)

    // Colors usable as a multiplier
    val MULTIPLIER_COLORS =
        listOf(BLACK, BROWN, RED, ORANGE, YELLOW, GREEN, BLUE, VIOLET, GREY, WHITE, GOLD, SILVER)

    // Colors usable as a tolerance
    val TOLERANCE_COLORS = listOf(BROWN, RED, GREEN, BLUE, VIOLET, GREY, GOLD, SILVER, NONE)

    // Colors usable as a temperature coefficient (6th band)
    val TEMPCO_COLORS = listOf(BROWN, RED, ORANGE, YELLOW, GREEN, BLUE, VIOLET)

    fun findByKey(key: String): ResistorBandColor? = ALL.firstOrNull { it.key == key }
}
