package maliauka.sasha.gestureapp

import android.os.Bundle
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var mDetector: GestureDetectorCompat

    private var statusBarHeight: Int? = null
    private val circles: ArrayDeque<Circle?> = ArrayDeque()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDetector = GestureDetectorCompat(this, MyGestureListener())

        // костыль
        statusBarHeight = getStatusBarHeight()
    }

    fun generateCircles(count: Int) {
        if (circles.size + count >= 1000) {
            Toast.makeText(
                this,
                getString(R.string.too_many_circles), Toast.LENGTH_SHORT
            ).show()
            return
        }

        for (i in 1..count) {
            val circle = Circle(
                this,
                Random.nextInt(50, 200).toFloat(),
                my_layout.width,
                my_layout.height
            )

            circles.offerFirst(circle)

            addContentView(
                circle,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )

        }
    }

    private fun deleteCircle(x: Float, y: Float) {
        var toDelete: Circle? = null

        for (circle in circles) {
            if (circle!!.contains(x, y)) {
                circle.isVisible = false
                toDelete = circle
                break
            }
        }

        circles.remove(toDelete)

        if (circles.isEmpty()) {
            twTap.text = getString(R.string.no_more_circles)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            if (event.action == MotionEvent.ACTION_MOVE) {
                deleteCircle(
                    event.rawX,
                    event.rawY - statusBarHeight!!
                )
            } else {
                return mDetector.onTouchEvent(event)
            }
        }

        return super.onTouchEvent(event)
    }

    inner class MyGestureListener : SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            generateCircles(Random.nextInt(5, 15))
            twTap.text = ""
            return super.onDoubleTap(e)
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if (e != null) {
                deleteCircle(e.rawX, e.rawY - statusBarHeight!!)
            }
            return super.onSingleTapConfirmed(e)
        }
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier(
            "status_bar_height",
            "dimen",
            "android"
        )
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}