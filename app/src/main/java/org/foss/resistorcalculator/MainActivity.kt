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
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import org.foss.resistorcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    private var vibrator: Vibrator? = null

    private var bandCount = DEFAULT_BAND_COUNT

    /** Selected colours for the current band count. */
    private val selectedColors = mutableListOf<ResistorBandColor>()

    /** Remembered selections per band count, so switching back restores them. */
    private val selectionsByCount = mutableMapOf<Int, List<ResistorBandColor>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        applyTheme()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        @Suppress("DEPRECATION")
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        applyWindowInsets()
        updateSystemBarAppearance()

        vibrator = resolveVibrator()
        lastNightMode = isNightMode()
        bandCount = prefs.getInt(KEY_BAND_COUNT, DEFAULT_BAND_COUNT).coerceIn(3, 6)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.title_calculator)

        setupBandCountChips()
        setupBandRows()
    }

    override fun onResume() {
        super.onResume()
        // The theme can be changed in Settings while this activity is paused.
        applyTheme()
        updateSystemBarAppearance()
        updateBandRowsIfNeeded()
    }

    private fun applyWindowInsets() {
        val toolbarHeight = resources.getDimensionPixelSize(R.dimen.toolbar_height)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            binding.root.updatePadding(left = bars.left, right = bars.right, bottom = bars.bottom)
            binding.toolbar.updatePadding(top = bars.top)
            binding.toolbar.layoutParams = binding.toolbar.layoutParams.apply {
                height = toolbarHeight + bars.top
            }
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)
    }

    private fun updateSystemBarAppearance() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        val light = !isNightMode()
        controller.isAppearanceLightStatusBars = light
        controller.isAppearanceLightNavigationBars = light
    }

    private fun applyTheme() {
        val theme = prefs.getString(KEY_THEME, "system") ?: "system"
        val mode = when (theme) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Band count selector
    // ---------------------------------------------------------------------------------------------

    private fun setupBandCountChips() {
        val chips = listOf(
            binding.chip3 to 3,
            binding.chip4 to 4,
            binding.chip5 to 5,
            binding.chip6 to 6
        )
        for ((chip, count) in chips) {
            chip.isChecked = count == bandCount
            chip.setOnClickListener {
                // Always select this chip; tapping an already-selected chip keeps it selected.
                for ((otherChip, _) in chips) {
                    otherChip.isChecked = false
                }
                chip.isChecked = true
                selectionsByCount[bandCount] = selectedColors.toList()
                bandCount = count
                prefs.edit().putInt(KEY_BAND_COUNT, count).apply()
                vibrate()
                setupBandRows()
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Ring selector rows
    // ---------------------------------------------------------------------------------------------

    private fun setupBandRows() {
        binding.bandsContainer.removeAllViews()
        selectedColors.clear()

        val defaults = selectionsByCount[bandCount] ?: defaultSelection(bandCount)
        for (index in 0 until bandCount) {
            val color = defaults.getOrNull(index) ?: defaultSelection(bandCount)[index]
            selectedColors.add(color)
        }

        for (index in 0 until bandCount) {
            binding.bandsContainer.addView(buildBandRow(index))
        }

        refresh()
    }

    private fun defaultSelection(count: Int): List<ResistorBandColor> = when (count) {
        3 -> listOf(ResistorBandColors.BROWN, ResistorBandColors.BLACK, ResistorBandColors.RED)
        4 -> listOf(
            ResistorBandColors.BROWN, ResistorBandColors.BLACK,
            ResistorBandColors.RED, ResistorBandColors.GOLD
        )
        5 -> listOf(
            ResistorBandColors.BROWN, ResistorBandColors.BLACK, ResistorBandColors.BLACK,
            ResistorBandColors.BROWN, ResistorBandColors.BROWN
        )
        else -> listOf(
            ResistorBandColors.BROWN, ResistorBandColors.BLACK, ResistorBandColors.BLACK,
            ResistorBandColors.BROWN, ResistorBandColors.BROWN, ResistorBandColors.BROWN
        )
    }

    private fun buildBandRow(position: Int): View {
        val density = resources.displayMetrics.density
        fun dp(value: Int): Int = (value * density).toInt()

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(10), dp(14), dp(10))
            setBackgroundResource(R.drawable.bg_band_card)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(8) }
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val title = TextView(this).apply {
            text = "${positionLabel(position)}  ·  ${roleFor(position)}"
            textSize = 14f
            setTextColor(ThemeColors.textPrimary(this@MainActivity))
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val value = TextView(this).apply {
            textSize = 14f
            setTextColor(ThemeColors.textSecondary(this@MainActivity))
        }

        header.addView(title)
        header.addView(value)
        row.addView(header)

        val scroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }
        }

        val dotRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val allowed = allowedColorsFor(position)
        val dots = mutableListOf<ColorDotView>()

        allowed.forEach { color ->
            val dot = ColorDotView(this).apply {
                bandColor = color.color
                isNone = color.key == ResistorBandColors.NONE.key
                chosen = color.key == selectedColors[position].key
                contentDescription = color.name
                isClickable = true
                isFocusable = true
                layoutParams = LinearLayout.LayoutParams(dp(44), dp(44)).apply {
                    marginEnd = dp(4)
                }
                setOnClickListener {
                    if (selectedColors[position].key != color.key) {
                        selectedColors[position] = color
                        dots.forEach { it.chosen = false }
                        chosen = true
                        vibrate()
                        refresh()
                    }
                }
            }
            dots.add(dot)
            dotRow.addView(dot)
        }

        value.text = selectedColors[position].name
        scroll.addView(dotRow)
        row.addView(scroll)

        // Keep references so the header label can be refreshed after selection.
        row.tag = BandRowViews(value, dots)
        return row
    }

    private data class BandRowViews(
        val valueLabel: TextView,
        val dots: List<ColorDotView>
    )

    private fun refresh() {
        val keys = selectedColors.take(bandCount).map { it.key }
        binding.resistorView.setBands(keys)
        binding.colorCodeSummary.text = selectedColors.take(bandCount)
            .joinToString("  ·  ") { it.name }
        updateRowLabels()
        calculateAndShow()
    }

    private fun updateRowLabels() {
        for (index in 0 until binding.bandsContainer.childCount) {
            val row = binding.bandsContainer.getChildAt(index) ?: continue
            val views = row.tag as? BandRowViews ?: continue
            views.valueLabel.text = selectedColors.getOrNull(index)?.name ?: ""
        }
    }

    /** Rebuilds the rows when the night mode changed while the activity was paused. */
    private fun updateBandRowsIfNeeded() {
        val night = isNightMode()
        if (night != lastNightMode) {
            lastNightMode = night
            setupBandRows()
        }
    }

    private var lastNightMode: Boolean = false

    private fun isNightMode(): Boolean {
        val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mode == Configuration.UI_MODE_NIGHT_YES
    }

    // ---------------------------------------------------------------------------------------------
    // Colour / band rules
    // ---------------------------------------------------------------------------------------------

    private fun roleFor(position: Int): String {
        val digitCount = if (bandCount <= 4) 2 else 3
        val multiplierIndex = if (bandCount <= 4) 2 else 3
        return when {
            position < digitCount -> getString(R.string.role_digit)
            position == multiplierIndex -> getString(R.string.role_multiplier)
            bandCount >= 6 && position == 5 -> getString(R.string.role_tempco)
            else -> getString(R.string.role_tolerance)
        }
    }

    private fun positionLabel(position: Int): String = when (position) {
        0 -> getString(R.string.band_first)
        1 -> getString(R.string.band_second)
        2 -> getString(R.string.band_third)
        3 -> getString(R.string.band_fourth)
        4 -> getString(R.string.band_fifth)
        else -> getString(R.string.band_sixth)
    }

    private fun allowedColorsFor(position: Int): List<ResistorBandColor> {
        val digitCount = if (bandCount <= 4) 2 else 3
        val multiplierIndex = if (bandCount <= 4) 2 else 3
        return when {
            position < digitCount -> ResistorBandColors.DIGIT_COLORS
            position == multiplierIndex -> ResistorBandColors.MULTIPLIER_COLORS
            bandCount >= 6 && position == 5 -> ResistorBandColors.TEMPCO_COLORS
            else -> ResistorBandColors.TOLERANCE_COLORS
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Result
    // ---------------------------------------------------------------------------------------------

    private fun calculateAndShow() {
        val result = ResistorCalculator.calculate(bandCount, selectedColors.toList())
        if (result == null) {
            binding.resultCard.visibility = View.GONE
            return
        }
        binding.resultNominal.text = result.formattedValue(this)
        binding.resultTolerance.text =
            "${getString(R.string.result_tolerance)}: ${result.formattedTolerance(this)}"

        if (result.tempcoPpm != null) {
            binding.resultTempco.visibility = View.VISIBLE
            binding.resultTempco.text =
                "${getString(R.string.result_tempco)}: ${result.formattedTempco(this)}"
        } else {
            binding.resultTempco.visibility = View.GONE
        }

        binding.resultMin.text = result.formattedMin(this)
        binding.resultMax.text = result.formattedMax(this)
        binding.resultCard.visibility = View.VISIBLE
    }

    // ---------------------------------------------------------------------------------------------
    // Haptics
    // ---------------------------------------------------------------------------------------------

    private fun resolveVibrator(): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private fun vibrate() {
        if (!prefs.getBoolean(KEY_VIBRATION, true)) return
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(20)
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Menu
    // ---------------------------------------------------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java)); true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val PREFS_NAME = "resistor_prefs"
        const val KEY_THEME = "theme"
        const val KEY_VIBRATION = "vibration"
        const val KEY_BAND_COUNT = "last_band_count"
        private const val DEFAULT_BAND_COUNT = 4
    }
}
