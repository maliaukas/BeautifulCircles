package maliauka.sasha.circles

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import hearsilent.discreteslider.DiscreteSlider
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    companion object {
        private const val darkColor = Color.BLACK
        private const val lightColor = Color.WHITE

        private const val radiusOffset = 7
    }

    private lateinit var mDetector: GestureDetectorCompat
    private val circles: ArrayDeque<Circle> = ArrayDeque()

    private var minRadius: Int = 10
    private var maxRadius: Int = 100

    private var minCount: Int = 5
    private var maxCount: Int = 20

    private var darkMode: Boolean = false

    private var statusBarHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDetector = GestureDetectorCompat(this, MyGestureListener())

        statusBarHeight = getStatusBarHeight()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null && event.action == MotionEvent.ACTION_MOVE) {
            if (darkMode)
                deleteCircle(event.rawX, event.rawY - statusBarHeight)
            else {
                addCircle(
                    event.rawX,
                    event.rawY - statusBarHeight,
                    Random.nextInt(
                        minRadius + radiusOffset,
                        maxRadius + radiusOffset
                    )
                )
            }

        }
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    inner class MyGestureListener : SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            if (!darkMode) {
                generateCircles(Random.nextInt(minCount, maxCount + 1))
            }
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            if (!darkMode)
                showSettingsDialog()
        }

        override fun onFling(
            e1: MotionEvent?, e2: MotionEvent?,
            velocityX: Float, velocityY: Float
        ): Boolean {
            val sensitivity = my_layout.height / 5
            val duration: Long = 500 // 0.5 sec

            if (abs(e1!!.x - e2!!.x) > sensitivity &&
                abs(e1.eventTime - e2.eventTime) < duration
            ) {
                deleteAllCircles()
            } else if (abs(e1.y - e2.y) > sensitivity &&
                abs(e1.eventTime - e2.eventTime) < duration
            ) {
                changeMode()
            }
            return true
        }
    }

    private fun addCircle(x: Float, y: Float, radius: Int) {
        if (circlesOverflow(1, false)) {
            return
        }

        clearText()
        val circle = Circle(this, radius, x, y)
        addCircle(circle)
    }

    private fun addCircle(circle: Circle) {
        circles.offerFirst(circle)

        addContentView(
            circle,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun circlesOverflow(count: Int, makeToast: Boolean = true): Boolean {
        if (circles.size + count >= 700) {
            if (makeToast) {
                Toast.makeText(
                    this,
                    getString(R.string.too_many_circles), Toast.LENGTH_SHORT
                ).show()
            }

            return true
        }
        return false
    }

    private fun clearText() {
        tableInfo.isVisible = false
        twInfo.text = ""
    }

    fun generateCircles(count: Int) {
        if (circlesOverflow(count)) {
            return
        }

        clearText()

        for (i in 1..count) {
            val circle = Circle(
                this,
                Random.nextInt(
                    minRadius + radiusOffset,
                    maxRadius + radiusOffset
                ),
                my_layout.width,
                my_layout.height
            )
            addCircle(circle)
        }
    }

    private fun deleteCircle(x: Float, y: Float) {
        for (circle in circles) {
            if (circle.contains(x, y)) {
                circle.isVisible = false
                (circle.parent as ViewGroup).removeView(circle)
                circles.remove(circle)
                break
            }
        }
        checkCirclesEmpty()
    }

    private fun checkCirclesEmpty() {
        if (circles.isEmpty()) {
            twInfo.text = getString(R.string.no_more_circles)
            twInfo.append(
                getString(
                    when (darkMode) {
                        true -> R.string.what_to_do_dark
                        false -> R.string.what_to_do_light
                    }
                )
            )
        }
    }

    private fun deleteAllCircles() {
        for (toDelete in circles) {
            toDelete.isVisible = false
            (toDelete.parent as ViewGroup).removeView(toDelete)
        }
        circles.clear()
        checkCirclesEmpty()
    }

    private fun changeMode() {
        if (!darkMode) {
            my_layout.setBackgroundColor(darkColor)
            twInfo.setTextColor(lightColor)
        } else {
            my_layout.setBackgroundColor(lightColor)
            twInfo.setTextColor(darkColor)
        }
        darkMode = !darkMode
        checkCirclesEmpty()
    }

    @SuppressLint("SetTextI18n")
    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.settings_title))

        val settingsView = layoutInflater.inflate(R.layout.settings, null)
        builder.setView(settingsView)

        val radiusSeekBar =
            settingsView.findViewById<DiscreteSlider>(R.id.radiusSeekBar)

        radiusSeekBar.count = my_layout.width / 2

        var newMinRadius = minRadius
        var newMaxRadius = maxRadius

        radiusSeekBar.minProgress = newMinRadius
        radiusSeekBar.maxProgress = newMaxRadius

        val twRadius = settingsView.findViewById<TextView>(R.id.twRadius)
        twRadius.text = "$newMinRadius - $newMaxRadius"

        radiusSeekBar.setOnValueChangedListener(object : DiscreteSlider.OnValueChangedListener() {
            override fun onValueChanged(minProgress: Int, maxProgress: Int, fromUser: Boolean) {
                super.onValueChanged(minProgress, maxProgress, fromUser)
                newMinRadius = minProgress
                newMaxRadius = maxProgress
                twRadius.text = "$newMinRadius - $newMaxRadius"
            }
        })

        val countSeekBar =
            settingsView.findViewById<DiscreteSlider>(R.id.countSeekBar)

        var newMinCount = minCount
        var newMaxCount = maxCount

        countSeekBar.minProgress = minCount
        countSeekBar.maxProgress = maxCount

        val twCount = settingsView.findViewById<TextView>(R.id.twCount)
        twCount.text = "$newMinCount - $newMaxCount"

        countSeekBar.setOnValueChangedListener(
            object : DiscreteSlider.OnValueChangedListener() {
                override fun onValueChanged(minProgress: Int, maxProgress: Int, fromUser: Boolean) {
                    super.onValueChanged(minProgress, maxProgress, fromUser)
                    newMinCount = minProgress
                    newMaxCount = maxProgress
                    twCount.text = "$newMinCount - $newMaxCount"
                }
            })

        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.cancel()
        }

        builder.setPositiveButton("Ok") { _: DialogInterface, _: Int ->
            minRadius = newMinRadius
            maxRadius = newMaxRadius

            minCount = newMinCount
            maxCount = newMaxCount
        }

        builder.show()
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