package org.foss.resistorcalculator

import android.content.Context
import androidx.core.content.ContextCompat

/** Resolves the semantic theme colours, which switch automatically in night mode. */
object ThemeColors {
    fun textPrimary(context: Context): Int = ContextCompat.getColor(context, R.color.colorTextPrimary)
    fun textSecondary(context: Context): Int = ContextCompat.getColor(context, R.color.colorTextSecondary)
    fun surface(context: Context): Int = ContextCompat.getColor(context, R.color.colorSurface)
    fun primary(context: Context): Int = ContextCompat.getColor(context, R.color.colorPrimary)
}
