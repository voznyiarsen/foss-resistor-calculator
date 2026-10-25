package org.foss.resistorcalculator

/**
 * Maps band position + colour key to the original Flutter asset slice.
 * Assets are vertical slices of the original 1410x297 resistor artwork:
 * left cap (0_leer) + one slice per band + right cap (7_leer).
 */
object ResistorArt {
    val LEFT_CAP = R.drawable.resistors_0_leer
    val RIGHT_CAP = R.drawable.resistors_7_leer

    fun slice(position: Int, key: String): Int = when (position) {
        1 -> when (key) {
            "black" -> R.drawable.resistors_1_schwarz
            "brown" -> R.drawable.resistors_1_braun
            "red" -> R.drawable.resistors_1_rot
            "orange" -> R.drawable.resistors_1_orange
            "yellow" -> R.drawable.resistors_1_gelb
            "green" -> R.drawable.resistors_1_gruen
            "blue" -> R.drawable.resistors_1_blau
            "violet" -> R.drawable.resistors_1_violett
            "grey" -> R.drawable.resistors_1_grau
            "white" -> R.drawable.resistors_1_weiss
            "gold" -> R.drawable.resistors_1_gold
            "silver" -> R.drawable.resistors_1_silber
            "none" -> R.drawable.resistors_1_leer
            else -> R.drawable.resistors_1_leer
        }
        2 -> when (key) {
            "black" -> R.drawable.resistors_2_schwarz
            "brown" -> R.drawable.resistors_2_braun
            "red" -> R.drawable.resistors_2_rot
            "orange" -> R.drawable.resistors_2_orange
            "yellow" -> R.drawable.resistors_2_gelb
            "green" -> R.drawable.resistors_2_gruen
            "blue" -> R.drawable.resistors_2_blau
            "violet" -> R.drawable.resistors_2_violett
            "grey" -> R.drawable.resistors_2_grau
            "white" -> R.drawable.resistors_2_weiss
            "gold" -> R.drawable.resistors_2_gold
            "silver" -> R.drawable.resistors_2_silber
            "none" -> R.drawable.resistors_2_leer
            else -> R.drawable.resistors_2_leer
        }
        3 -> when (key) {
            "black" -> R.drawable.resistors_3_schwarz
            "brown" -> R.drawable.resistors_3_braun
            "red" -> R.drawable.resistors_3_rot
            "orange" -> R.drawable.resistors_3_orange
            "yellow" -> R.drawable.resistors_3_gelb
            "green" -> R.drawable.resistors_3_gruen
            "blue" -> R.drawable.resistors_3_blau
            "violet" -> R.drawable.resistors_3_violett
            "grey" -> R.drawable.resistors_3_grau
            "white" -> R.drawable.resistors_3_weiss
            "gold" -> R.drawable.resistors_3_gold
            "silver" -> R.drawable.resistors_3_silber
            "none" -> R.drawable.resistors_3_leer
            else -> R.drawable.resistors_3_leer
        }
        4 -> when (key) {
            "black" -> R.drawable.resistors_4_schwarz
            "brown" -> R.drawable.resistors_4_braun
            "red" -> R.drawable.resistors_4_rot
            "orange" -> R.drawable.resistors_4_orange
            "yellow" -> R.drawable.resistors_4_gelb
            "green" -> R.drawable.resistors_4_gruen
            "blue" -> R.drawable.resistors_4_blau
            "violet" -> R.drawable.resistors_4_violett
            "grey" -> R.drawable.resistors_4_grau
            "white" -> R.drawable.resistors_4_weiss
            "gold" -> R.drawable.resistors_4_gold
            "silver" -> R.drawable.resistors_4_silber
            "none" -> R.drawable.resistors_4_leer
            else -> R.drawable.resistors_4_leer
        }
        5 -> when (key) {
            "black" -> R.drawable.resistors_5_schwarz
            "brown" -> R.drawable.resistors_5_braun
            "red" -> R.drawable.resistors_5_rot
            "orange" -> R.drawable.resistors_5_orange
            "yellow" -> R.drawable.resistors_5_gelb
            "green" -> R.drawable.resistors_5_gruen
            "blue" -> R.drawable.resistors_5_blau
            "violet" -> R.drawable.resistors_5_violett
            "grey" -> R.drawable.resistors_5_grau
            "white" -> R.drawable.resistors_5_weiss
            "gold" -> R.drawable.resistors_5_gold
            "silver" -> R.drawable.resistors_5_silber
            "none" -> R.drawable.resistors_5_leer
            else -> R.drawable.resistors_5_leer
        }
        6 -> when (key) {
            "black" -> R.drawable.resistors_6_schwarz
            "brown" -> R.drawable.resistors_6_braun
            "red" -> R.drawable.resistors_6_rot
            "orange" -> R.drawable.resistors_6_orange
            "yellow" -> R.drawable.resistors_6_gelb
            "green" -> R.drawable.resistors_6_gruen
            "blue" -> R.drawable.resistors_6_blau
            "violet" -> R.drawable.resistors_6_violett
            "grey" -> R.drawable.resistors_6_grau
            "white" -> R.drawable.resistors_6_weiss
            "gold" -> R.drawable.resistors_6_gold
            "silver" -> R.drawable.resistors_6_silber
            "none" -> R.drawable.resistors_6_leer
            else -> R.drawable.resistors_6_leer
        }
        else -> R.drawable.resistors_1_leer
    }
}
