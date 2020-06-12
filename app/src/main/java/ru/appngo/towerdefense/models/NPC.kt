package ru.appngo.towerdefense.models

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import ru.appngo.towerdefense.activities.CELL_SIZE
import ru.appngo.towerdefense.GameCore
import ru.appngo.towerdefense.enums.Material
import ru.appngo.towerdefense.utils.checkMoveThrought
import ru.appngo.towerdefense.utils.getCoordinateNow
import ru.appngo.towerdefense.utils.runOnUiThread

class NPC(
    val element: Element
){

    fun move(
        dx:Int,
        dy:Int,
        container:FrameLayout,
        elementsOnContainer:List<Element>,
        gameCore: GameCore,
        score: Int
    ) {
        val view = container.findViewById<View>(element.viewId) ?: return
        val textView = container.findViewById<TextView>(element.textViewId)
        val saveCoordinate = view.getCoordinateNow()
        val nextCoordinate = getNextCoordinate(dx, dy, view)
        getNextTextCoordinate(dx, dy, textView)
        if (view.checkMoveThrought(nextCoordinate)
            && element.checkCanMoveThroughMaterial(nextCoordinate, elementsOnContainer, container, gameCore, score)
        ) {
            viewMoving(container, view, textView)
            element.coordinate = nextCoordinate
        } else {
            element.coordinate = saveCoordinate
            (view.layoutParams as FrameLayout.LayoutParams).topMargin = saveCoordinate.top
            (view.layoutParams as FrameLayout.LayoutParams).leftMargin = saveCoordinate.left
            (textView.layoutParams as FrameLayout.LayoutParams).topMargin = saveCoordinate.top - CELL_SIZE
            (textView.layoutParams as FrameLayout.LayoutParams).leftMargin = saveCoordinate.left + CELL_SIZE/2
        }
    }


    private fun getNextCoordinate(dx:Int, dy:Int, view:View):Coordinate {
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        (view.layoutParams as FrameLayout.LayoutParams).topMargin += dy
        (view.layoutParams as FrameLayout.LayoutParams).leftMargin += dx
        return Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
    }

    private fun getNextTextCoordinate(dx:Int, dy:Int, textView:TextView) {
        val layoutParams = textView.layoutParams as FrameLayout.LayoutParams
        (textView.layoutParams as FrameLayout.LayoutParams).topMargin += dy
        (textView.layoutParams as FrameLayout.LayoutParams).leftMargin += dx
    }


    private fun Element.checkCanMoveThroughMaterial(
        coordinate: Coordinate,
        elementsOnContainer:List<Element>,
        container: FrameLayout,
        gameCore: GameCore,
        score: Int
    ): Boolean {
        for (npcCoord in getCoordinates(coordinate)){
            val findElement = compareCoordinate(npcCoord, elementsOnContainer)
            if (findElement != null && findElement.material == Material.LAVA){
                this.hp -= 1
                continue
            }
            if (findElement != null && !findElement.material.tankCanGoThrough) {
                if(this == findElement) {
                    continue
                }
                if (findElement.material == Material.PON4IK) {
                    container.runOnUiThread {
                        container.removeView(container.findViewById(findElement.viewId))
                    }
                    gameCore.destroy(score)
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
                topLeftCoordinate.top + CELL_SIZE*2,
                topLeftCoordinate.left
            )
        ) //bottom_left
        coordinateList.add(
            Coordinate(
                topLeftCoordinate.top,
                topLeftCoordinate.left + CELL_SIZE*2
            )
        ) //top_right
        coordinateList.add(
            Coordinate(
                topLeftCoordinate.top + CELL_SIZE*2,
                topLeftCoordinate.left + CELL_SIZE*2
            )
        ) //bottom_right
        coordinateList.add(
            Coordinate(
                topLeftCoordinate.top + CELL_SIZE,
                topLeftCoordinate.left + CELL_SIZE
            )
        ) //bottom_right
        return coordinateList
    }

    private fun viewMoving(container: FrameLayout, view: View, textView: TextView){
        container.runOnUiThread {
            container.removeView(view)
            container.addView(view)
            textView.text = element.hp.toString()
            container.removeView(textView)
            container.addView(textView)
        }
    }

}
