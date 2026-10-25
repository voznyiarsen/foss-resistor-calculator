package org.foss.resistorcalculator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * Draws the resistor preview using the original Flutter artwork.
 *
 * The original 1410x297 artwork is shipped as vertical slices: a left cap
 * (`0_leer`), one slice per ring position (`1_*` … `6_*`) and a right cap
 * (`7_leer`). Concatenating them reproduces the exact original rendering,
 * including the body shading and the ring spacing.
 *
 * [setBands] expects one colour key per ring (e.g. "brown", "black", "red", "gold").
 */
class ResistorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        // Decoded slice bitmaps are immutable and shared across all views, so
        // cache them by drawable resource id to avoid re-decoding on every
        // band change / recompose.
        private val sliceCache = object : android.util.LruCache<Int, Bitmap>(4 * 1024 * 1024) {
            override fun sizeOf(key: Int, value: Bitmap): Int = value.byteCount

            override fun entryRemoved(evicted: Boolean, key: Int, oldValue: Bitmap, newValue: Bitmap?) {
                if (evicted && !oldValue.isRecycled) oldValue.recycle()
            }
        }
    }

    private val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    /** Exact pixel copies, so slice boundaries stay seamless. */
    private val composePaint = Paint().apply { isFilterBitmap = false }
    private val composeDest = RectF()
    private var bandKeys: List<String> = emptyList()
    private var composed: Bitmap? = null
    private val dest = RectF()

    fun setBands(keys: List<String>) {
        if (keys == bandKeys && composed != null) return
        bandKeys = keys
        rebuild()
        requestLayout()
        invalidate()
    }

    private fun rebuild() {
        composed?.recycle()
        composed = null
        if (bandKeys.isEmpty()) return

        val slices = ArrayList<Bitmap>(8)
        slices.add(decode(ResistorArt.LEFT_CAP))
        // Composite all 6 positions (matching original 1410px width).
        // Positions beyond bandCount use "leer" (empty body) so every band
        // count renders at the same fixed width as the original Flutter app.
        for (pos in 1..6) {
            val key = if (pos <= bandKeys.size) bandKeys[pos - 1] else ResistorBandColors.NONE.key
            slices.add(decode(ResistorArt.slice(pos, key)))
        }
        slices.add(decode(ResistorArt.RIGHT_CAP))

        val width = slices.sumOf { it.width }
        val height = slices.maxOf { it.height }
        if (width <= 0 || height <= 0) {
            slices.forEach { it.recycle() }
            return
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.TRANSPARENT)
        var x = 0f
        for (slice in slices) {
            // Destination rect keeps the slices at their native pixel size and
            // avoids Canvas' density scaling (resources are 160 dpi, the
            // screen is much denser).
            composeDest.set(x, 0f, x + slice.width, slice.height.toFloat())
            canvas.drawBitmap(slice, null, composeDest, composePaint)
            x += slice.width
            // Slices are cached and shared; do not recycle them here.
        }
        composed = bitmap
    }

    private fun decode(resId: Int): Bitmap {
        sliceCache.get(resId)?.let { if (!it.isRecycled) return it }
        val options = BitmapFactory.Options().apply { inScaled = false }
        val bitmap = BitmapFactory.decodeResource(resources, resId, options)
            ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        sliceCache.put(resId, bitmap)
        return bitmap
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val bitmap = composed
        val ratio = if (bitmap != null && bitmap.height > 0) {
            bitmap.width.toFloat() / bitmap.height
        } else {
            1410f / 297f
        }
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = (width / ratio).toInt().coerceAtLeast(1)
        setMeasuredDimension(width, resolveSize(height, heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bitmap = composed ?: return
        val scale = width.toFloat() / bitmap.width
        val drawHeight = bitmap.height * scale
        val top = (height - drawHeight) / 2f
        dest.set(0f, top, width.toFloat(), top + drawHeight)
        canvas.drawBitmap(bitmap, null, dest, paint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        composed?.recycle()
        composed = null
        bandKeys = emptyList()
    }
}
