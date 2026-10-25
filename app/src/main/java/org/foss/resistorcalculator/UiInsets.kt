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

import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/** Shared edge-to-edge inset handling for every screen. */
object UiInsets {

    fun apply(activity: AppCompatActivity, root: View, toolbarId: Int = R.id.toolbar) {
        val toolbar = root.findViewById<View>(toolbarId) as? Toolbar
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        @Suppress("DEPRECATION")
        activity.window.statusBarColor = Color.TRANSPARENT
        @Suppress("DEPRECATION")
        activity.window.navigationBarColor = Color.TRANSPARENT

        val toolbarHeight = root.resources.getDimensionPixelSize(R.dimen.toolbar_height)
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            root.updatePadding(left = bars.left, right = bars.right, bottom = bars.bottom)
            if (toolbar != null) {
                toolbar.updatePadding(top = bars.top)
                toolbar.layoutParams = toolbar.layoutParams.apply {
                    height = toolbarHeight + bars.top
                }
            }
            insets
        }
        ViewCompat.requestApplyInsets(root)
        updateBarIcons(activity)
    }

    fun updateBarIcons(activity: AppCompatActivity) {
        val mode = activity.resources.configuration.uiMode
        val light = mode and Configuration.UI_MODE_NIGHT_MASK != Configuration.UI_MODE_NIGHT_YES
        val controller = WindowCompat.getInsetsController(activity.window,
            activity.window.decorView)
        controller.isAppearanceLightStatusBars = light
        controller.isAppearanceLightNavigationBars = light
    }
}
