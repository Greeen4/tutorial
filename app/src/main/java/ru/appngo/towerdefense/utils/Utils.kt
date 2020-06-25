package ru.appngo.towerdefense.utils

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import ru.appngo.towerdefense.activities.CELL_SIZE
import ru.appngo.towerdefense.activities.HORIZONTAL_MAX_SIZE
import ru.appngo.towerdefense.activities.VERTICAL_MAX_SIZE
import ru.appngo.towerdefense.enums.Material
import ru.appngo.towerdefense.models.Coordinate
import ru.appngo.towerdefense.models.Element
import kotlin.math.sqrt


fun View.getCoordinateNow():Coordinate {
    return Coordinate(
        (this.layoutParams as FrameLayout.LayoutParams).topMargin,
        (this.layoutParams as FrameLayout.LayoutParams).leftMargin
    )
}

fun View.checkMoveThrought(coordinate: Coordinate): Boolean {
    if (coordinate.top >= 0
        && coordinate.top + this.height <= HORIZONTAL_MAX_SIZE
        && coordinate.left >= 0
        && coordinate.left + this.width <= VERTICAL_MAX_SIZE
    ) {
        return true
    }
    return false
}

fun calculateStepToMove(origin: Coordinate, target: Coordinate): Coordinate {
    val y1 = origin.top.toDouble()
    val x1 = origin.left.toDouble()
    val x2 = target.left.toDouble()
    val y2 = target.top.toDouble()
    var dy = (y2 - y1) / (sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)))
    var dx = (x2 - x1) / (sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)))
    return Coordinate((dx*10).toInt(), (dy*10).toInt())
}

fun getElementByCoordinates(coordinate: Coordinate, elementsOnContainer: List<Element>): Element? {
    for (element in elementsOnContainer.toList()) {
        for (height in 0 until element.height) {
            for (width in 0 until element.width) {
                val searchingCoordinate = Coordinate(
                    top = element.coordinate.top + height * CELL_SIZE,
                    left = element.coordinate.left + width * CELL_SIZE
                )
                if (coordinate == searchingCoordinate) {
                    return element
                }
            }
        }
    }
    return null
}


fun Element.drawElement(container: FrameLayout) {
    var textView:TextView? = null
    if (this.material == Material.ENEMY){
        textView = TextView(container.context)
        val layoutParams = FrameLayout.LayoutParams(
            this.material.width * CELL_SIZE,
            this.material.height * CELL_SIZE
        )

        layoutParams.topMargin = this.coordinate.top - CELL_SIZE
        layoutParams.leftMargin = this.coordinate.left + CELL_SIZE/2
        textView.id = this.textViewId
        textView.layoutParams = layoutParams
        textView.text = this.hp.toString()
    }
    val view = ImageView(container.context)
    val layoutParams = FrameLayout.LayoutParams(
        this.material.width * CELL_SIZE,
        this.material.height * CELL_SIZE
    )
    this.material.image?.let { view.setImageResource(it) }
    layoutParams.topMargin = this.coordinate.top
    layoutParams.leftMargin = this.coordinate.left

    view.id = this.viewId
    view.layoutParams = layoutParams
    view.scaleType = ImageView.ScaleType.FIT_XY
    container.runOnUiThread{
        container.addView(view)
        if(textView != null)
            container.addView(textView)
    }
}

fun FrameLayout.runOnUiThread(block: () -> Unit){
    (this.context as Activity).runOnUiThread{
        block()
    }
}
