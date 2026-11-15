package com.example.mrapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

/**
 * Прозрачный View поверх камеры, который рисует скелет рук.
 * Должен быть top-level классом — тогда XML сможет его найти по имени
 * com.example.mrapp.HandOverlayView
 */
class HandOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Индексы связей между landmarks (стандарт MediaPipe)
    private val connections = listOf(
        // большой палец
        0 to 1, 1 to 2, 2 to 3, 3 to 4,
        // указательный
        0 to 5, 5 to 6, 6 to 7, 7 to 8,
        // средний
        5 to 9, 9 to 10, 10 to 11, 11 to 12,
        // безымянный
        9 to 13, 13 to 14, 14 to 15, 15 to 16,
        // мизинец
        13 to 17, 17 to 18, 18 to 19, 19 to 20,
        // ладонь
        0 to 17
    )

    private val linePaint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 6f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val pointPaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    @Volatile
    private var results: HandLandmarkerResult? = null

    fun setResults(result: HandLandmarkerResult) {
        this.results = result
        postInvalidate() // безопасно из фонового потока
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val res = results ?: return
        val w = width.toFloat()
        val h = height.toFloat()

        for (hand in res.landmarks()) {
            // Сначала линии
            for ((a, b) in connections) {
                val p1 = hand[a]
                val p2 = hand[b]
                canvas.drawLine(
                    p1.x() * w, p1.y() * h,
                    p2.x() * w, p2.y() * h,
                    linePaint
                )
            }
            // Потом точки
            for (lm: NormalizedLandmark in hand) {
                canvas.drawCircle(lm.x() * w, lm.y() * h, 10f, pointPaint)
            }
        }
    }
}
