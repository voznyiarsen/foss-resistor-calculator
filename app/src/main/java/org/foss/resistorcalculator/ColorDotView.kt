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

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/**
 * Circular colour swatch used to pick a ring colour. The selected swatch is
 * surrounded by a ring, mirroring the outlined circle asset of the original app.
 */
class ColorDotView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // Theme colours are stable for the lifetime of this view instance
    // (views are recreated when night mode changes), so cache them.
    private val colorTextPrimary: Int by lazy { ThemeColors.textPrimary(context) }
    private val colorTextSecondary: Int by lazy { ThemeColors.textSecondary(context) }
    private val colorSurface: Int by lazy { ThemeColors.surface(context) }

    private val density: Float by lazy { resources.displayMetrics.density }

    var bandColor: Int = Color.BLACK
        set(value) { field = value; invalidate() }

    var isNone: Boolean = false
        set(value) { field = value; invalidate() }

    var chosen: Boolean = false
        set(value) { field = value; invalidate() }

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val slash = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }

    private fun dp(value: Float): Float = value * density

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = dp(44f).toInt()
        setMeasuredDimension(
            resolveSize(size, widthMeasureSpec),
            resolveSize(size, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val outer = min(width, height) / 2f - dp(2f)

        if (chosen) {
            ring.color = colorTextPrimary
            ring.strokeWidth = dp(3f)
            canvas.drawCircle(cx, cy, outer, ring)
        }

        val radius = outer - dp(5f)
        if (isNone) {
            fill.color = colorSurface
            canvas.drawCircle(cx, cy, radius, fill)
            ring.color = colorTextSecondary
            ring.strokeWidth = dp(1.5f)
            canvas.drawCircle(cx, cy, radius, ring)
            slash.color = colorTextPrimary
            slash.strokeWidth = dp(2f)
            canvas.drawLine(cx - radius * 0.7f, cy + radius * 0.7f, cx + radius * 0.7f, cy - radius * 0.7f, slash)
        } else {
            fill.color = bandColor
            canvas.drawCircle(cx, cy, radius, fill)
            ring.color = withAlpha(colorTextSecondary, 0x55)
            ring.strokeWidth = dp(1f)
            canvas.drawCircle(cx, cy, radius, ring)
        }
    }

    private fun withAlpha(color: Int, alpha: Int): Int {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }
}
