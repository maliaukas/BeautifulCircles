package maliauka.sasha.gestureapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.random.Random


class Circle(
    context: Context?,
    private val radius: Float,
    maxWidth: Int,
    maxHeight: Int
) : View(context) {
    private val paint: Paint = Paint()

    private val x: Int = Random.nextInt(maxWidth)
    private val y: Int = Random.nextInt(maxHeight)

    companion object {
        private val colors = arrayOf(
            Color.BLACK, Color.BLUE,
            Color.WHITE, Color.RED,
            Color.GREEN, Color.YELLOW,
            Color.MAGENTA, Color.CYAN,
        )
    }

    override fun onDraw(canvas: Canvas) {
        paint.color = colors[Random.nextInt(colors.size)]
        paint.alpha = Random.nextInt(30, 100)

        canvas.drawCircle(
            x.toFloat(),
            y.toFloat(),
            radius,
            paint
        )
    }

    fun contains(_x: Float, _y: Float): Boolean {
        return (x - _x) * (x - _x) + (y - _y) * (y - _y) <= radius * radius
    }
}