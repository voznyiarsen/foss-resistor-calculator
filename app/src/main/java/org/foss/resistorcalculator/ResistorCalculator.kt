/*
 * FOSS Resistor Calculator
 * Copyright (C) 2026
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.foss.resistorcalculator

/**
 * Computes the resistance value from a list of selected band colors.
 *
 * Band layout (0‑based indices):
 *   3‑band: digits [0,1], multiplier [2], tolerance defaults to ±20%
 *   4‑band: digits [0,1], multiplier [2], tolerance [3]
 *   5‑band: digits [0,1,2], multiplier [3], tolerance [4]
 *   6‑band: digits [0,1,2], multiplier [3], tolerance [4], tempco [5]
 */
object ResistorCalculator {
    fun calculate(bandCount: Int, bands: List<ResistorBandColor>): ResistorResult? {
        if (bands.size < bandCount) return null

        // Significant digits
        val numDigits = if (bandCount <= 4) 2 else 3
        var significant = 0L
        for (i in 0 until numDigits) {
            val d = bands.getOrNull(i)?.digit ?: return null
            significant = significant * 10 + d
        }

        // Multiplier band
        val multiplierIndex = if (bandCount <= 4) 2 else 3
        val multiplier = bands.getOrNull(multiplierIndex)?.multiplier ?: return null
        val nominal = significant.toDouble() * multiplier

        // Tolerance & temperature coefficient
        val tolerance: Double
        val tempcoPpm: Int?
        when (bandCount) {
            3 -> { tolerance = 0.20; tempcoPpm = null } // no tolerance band → ±20%
            4 -> {
                tolerance = bands.getOrNull(3)?.tolerance ?: return null
                tempcoPpm = null
            }
            5 -> {
                tolerance = bands.getOrNull(4)?.tolerance ?: return null
                tempcoPpm = null
            }
            6 -> {
                tolerance = bands.getOrNull(4)?.tolerance ?: return null
                tempcoPpm = bands.getOrNull(5)?.tempcoPpm
            }
            else -> return null
        }

        return ResistorResult(
            nominalOhms = nominal,
            tolerance = tolerance,
            tempcoPpm = tempcoPpm,
            bandCount = bandCount,
            minOhms = nominal * (1 - tolerance),
            maxOhms = nominal * (1 + tolerance)
        )
    }
}
