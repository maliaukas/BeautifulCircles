package maliauka.sasha.gestureapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.random.Random


class Circle(
    context: Context?,
    private val radius: Int,
    maxWidth: Int,
    maxHeight: Int
) : View(context) {
    private val paint: Paint = Paint()

    private val x: Int = Random.nextInt(radius / 2, maxWidth - radius / 2)
    private val y: Int = Random.nextInt(radius / 2,maxHeight - radius / 2)

    companion object {
        private val colors = arrayOf(
            Color.WHITE, Color.BLUE,
            Color.RED,
            Color.GREEN, Color.YELLOW,
            Color.MAGENTA, Color.CYAN,
        )
    }

    override fun onDraw(canvas: Canvas) {
        paint.color = colors[Random.nextInt(colors.size)]
        paint.alpha = Random.nextInt(40, 100)
        paint.isAntiAlias = true

        canvas.drawCircle(
            x.toFloat(),
            y.toFloat(),
            radius.toFloat(),
            paint
        )
    }

    fun contains(_x: Float, _y: Float): Boolean {
        return (x - _x) * (x - _x) + (y - _y) * (y - _y) <= radius * radius
    }
}