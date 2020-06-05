package ru.appngo.towerdefense.models

import android.view.View
import android.widget.FrameLayout
import ru.appngo.towerdefense.CELL_SIZE
import ru.appngo.towerdefense.enums.Direction
import ru.appngo.towerdefense.utils.checkMoveThrought
import ru.appngo.towerdefense.utils.getCoordinateNow
import ru.appngo.towerdefense.utils.getElementByCoordinates
import ru.appngo.towerdefense.utils.runOnUiThread
import kotlin.text.Typography.bullet

class NPC(
    val element: Element
){

    fun move(dx:Int, dy:Int, container:FrameLayout, elementsOnContainer:List<Element>) {
        val view = container.findViewById<View>(element.viewId) ?: return
        val saveCoordinate = view.getCoordinateNow()
        val nextCoordinate = getNextCoordinate(dx, dy, view)
        if (view.checkMoveThrought(nextCoordinate)
            && element.checkCanMoveThroughMaterial(nextCoordinate, elementsOnContainer)
        ) {
            viewMoving(container, view)
            element.coordinate = nextCoordinate
        } else {
            element.coordinate = saveCoordinate
            (view.layoutParams as FrameLayout.LayoutParams).topMargin = saveCoordinate.top
            (view.layoutParams as FrameLayout.LayoutParams).leftMargin = saveCoordinate.left
        }
    }


    private fun getNextCoordinate(dx:Int, dy:Int, view:View):Coordinate {
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        (view.layoutParams as FrameLayout.LayoutParams).topMargin += dy
        (view.layoutParams as FrameLayout.LayoutParams).leftMargin += dx
        return Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
    }


    private fun Element.checkCanMoveThroughMaterial(
        coordinate: Coordinate,
        elementsOnContainer:List<Element>
    ): Boolean {
        for (npcCoord in getCoordinates(coordinate)){
            val element = compareCoordinate(npcCoord, elementsOnContainer)
            if (element != null && !element.material.tankCanGoThrough) {
                if(this == element) {
                    continue
                }
                return false
            }
        }
        return true
    }

    fun compareCoordinate(coordinate: Coordinate, elementsOnContainer: List<Element>): Element? {

        for (i in elementsOnContainer) {
            if (coordinate.top >= i.coordinate.top
                && coordinate.top <= i.coordinate.top + i.height * CELL_SIZE
                && coordinate.left >= i.coordinate.left
                && coordinate.left <= i.coordinate.left + i.width * CELL_SIZE
            )
            {
                return i
            }
        }
        return null
    }


    private fun getCoordinates(topLeftCoordinate: Coordinate): List<Coordinate> {
        val coordinateList = mutableListOf<Coordinate>()
        coordinateList.add(topLeftCoordinate)
        coordinateList.add(
            Coordinate(
                topLeftCoordinate.top + CELL_SIZE,
                topLeftCoordinate.left
            )
        ) //bottom_left
        coordinateList.add(
            Coordinate(
                topLeftCoordinate.top,
                topLeftCoordinate.left + CELL_SIZE
            )
        ) //top_right
        coordinateList.add(
            Coordinate(
                topLeftCoordinate.top + CELL_SIZE,
                topLeftCoordinate.left + CELL_SIZE
            )
        ) //bottom_right
        return coordinateList
    }

    private fun viewMoving(container: FrameLayout, view: View){
        container.runOnUiThread {
            container.removeView(view)
            container.addView(view)
        }
    }

}
