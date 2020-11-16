package maliauka.sasha.circles

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.random.Random

class Circle(context: Context?) : View(context) {
    private var _x: Float = 0.0f
    private var _y: Float = 0.0f
    private var radius: Int = 0

    private val paint: Paint = Paint()

    constructor(context: Context?, radius: Int, maxX: Int, maxY: Int) : this(context) {
        _x = Random.nextInt(radius / 2, maxX - radius / 2).toFloat()
        _y = Random.nextInt(radius / 2, maxY - radius / 2).toFloat()
        this.radius = radius
    }

    constructor(context: Context?, radius: Int, x: Float, y: Float) : this(context) {
        _x = x
        _y = y
        this.radius = radius
    }

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
            _x,
            _y,
            radius.toFloat(),
            paint
        )
    }

    fun contains(x: Float, y: Float): Boolean {
        return (_x - x) * (_x - x) + (_y - y) * (_y - y) <= radius * radius
    }
}