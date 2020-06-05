package ru.appngo.towerdefense.models


import android.view.View
import android.widget.FrameLayout
import ru.appngo.towerdefense.CELL_SIZE
import ru.appngo.towerdefense.enums.Direction
import ru.appngo.towerdefense.utils.checkMoveThrought
import ru.appngo.towerdefense.utils.getElementByCoordinates
import ru.appngo.towerdefense.utils.runOnUiThread

class test(
    val element: Element,
    var direction: Direction
){

    fun move(direction: Direction, container:FrameLayout, elementsOnContainer:List<Element>) {
        val view = container.findViewById<View>(element.viewId) ?: return
        val saveCoordinate = getCoordinateNow(view)
        this.direction = direction

//        val nextCoordinate = Coordinate(
//            (view.layoutParams as FrameLayout.LayoutParams).topMargin,
//            (view.layoutParams as FrameLayout.LayoutParams).leftMargin
//        ) //save after change
        val nextCoordinate = getNextCoordinate(view)
        if (view.checkMoveThrought(nextCoordinate)
            && element.checkCanMoveThroughMaterial(nextCoordinate, elementsOnContainer)
        ) {
            viewMoving(container, view)
//            container.removeView(view)
//            container.addView(view, 0)
            element.coordinate = nextCoordinate
        } else {
            element.coordinate = saveCoordinate
            (view.layoutParams as FrameLayout.LayoutParams).topMargin = saveCoordinate.top
            (view.layoutParams as FrameLayout.LayoutParams).leftMargin = saveCoordinate.left
        }
    }

    private  fun getCoordinateNow(view:View):Coordinate {
        return Coordinate(
            (view.layoutParams as FrameLayout.LayoutParams).topMargin,
            (view.layoutParams as FrameLayout.LayoutParams).leftMargin
        )
    }

    private fun getNextCoordinate(view:View):Coordinate {
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        when (direction) {
            Direction.UP -> {
                (view.layoutParams as FrameLayout.LayoutParams).topMargin += -CELL_SIZE
            }
            Direction.BOTTOM -> {
                (view.layoutParams as FrameLayout.LayoutParams).topMargin += CELL_SIZE
            }
            Direction.RIGHT -> {
                (view.layoutParams as FrameLayout.LayoutParams).leftMargin += CELL_SIZE
            }
            Direction.LEFT -> {
                (view.layoutParams as FrameLayout.LayoutParams).leftMargin += -CELL_SIZE
            }
        }
        return Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
    }


    private fun Element.checkCanMoveThroughMaterial(
        coordinate: Coordinate,
        elementsOnContainer:List<Element>
    ): Boolean {
        for (npcCoord in getCoordinates(coordinate)){
            val element = getElementByCoordinates(npcCoord, elementsOnContainer)
            if (element != null && !element.material.tankCanGoThrough) {
                if(this == element) {
                    continue
                }
                return false
            }
        }
        return true
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
