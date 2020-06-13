package ru.appngo.towerdefense.drawers

import android.view.View
import android.widget.FrameLayout
import ru.appngo.towerdefense.activities.CELL_SIZE
import ru.appngo.towerdefense.enums.Direction
import ru.appngo.towerdefense.models.Coordinate
import ru.appngo.towerdefense.models.Element
import ru.appngo.towerdefense.utils.checkMoveThrought
import ru.appngo.towerdefense.utils.getElementByCoordinates


//not used

class AssasinDrawer(val container: FrameLayout) {

//
//        var currentDirection = Direction.UP
//
//        fun move(myTank: View, direction: Direction, elementsOnContainer: List<Element>, debug: TextView) {
//            val layoutParams = myTank.layoutParams as FrameLayout.LayoutParams
//            val currentCoordinate = Coordinate(layoutParams.topMargin, layoutParams.leftMargin) //save before change
//            currentDirection = direction
//            when (direction) {
//                Direction.UP -> {
//                    (myTank.layoutParams as FrameLayout.LayoutParams).topMargin += -CELL_SIZE
//                }
//                Direction.BOTTOM -> {
//                    (myTank.layoutParams as FrameLayout.LayoutParams).topMargin += CELL_SIZE
//                }
//                Direction.RIGHT -> {
//                    (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin += CELL_SIZE
//                }
//                Direction.LEFT -> {
//                    (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin += -CELL_SIZE
//                }
//            }
//            val nextCoordinate = Coordinate(layoutParams.topMargin, layoutParams.leftMargin) //save after change
////            debug.text = nextCoordinate.top.toString() +"|"+ nextCoordinate.left.toString()+"#"+ elementsOnContainer[0].coordinate.top.toString() + elementsOnContainer[0].coordinate.left.toString()
//            if (myTank.checkMoveThrought(nextCoordinate)//myTank.checkMoveThrough(nextCoordinate)
//                && checkTankCanMoveThroughMaterial(nextCoordinate, elementsOnContainer)
//            ) {
//                container.removeView(myTank)
//                container.addView(myTank, 0)
//            } else {
//                (myTank.layoutParams as FrameLayout.LayoutParams).topMargin = currentCoordinate.top
//                (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin = currentCoordinate.left
//            }
//        }
//
//        private fun checkTankCanMoveThroughMaterial(coordinate: Coordinate, elementsOnContainer: List<Element>): Boolean {
//            getTankCoordinates(coordinate).forEach {
//                val element = getElementByCoordinates(it, elementsOnContainer)
//                if (element != null && !element.material.tankCanGoThrough) {
//                    return false
//                }
//            }
//            return true
//        }
//
//        private fun getTankCoordinates(topLeftCoordinate: Coordinate): List<Coordinate> {
//            val coordinateList = mutableListOf<Coordinate>()
//            coordinateList.add(topLeftCoordinate)
//            coordinateList.add(Coordinate(topLeftCoordinate.top + CELL_SIZE, topLeftCoordinate.left)) //bottom_left
//            coordinateList.add(Coordinate(topLeftCoordinate.top, topLeftCoordinate.left + CELL_SIZE)) //top_right
//            coordinateList.add(
//                Coordinate(
//                    topLeftCoordinate.top + CELL_SIZE,
//                    topLeftCoordinate.left + CELL_SIZE
//                )
//            ) //bottom_right
//            return coordinateList
//        }
//    }
    fun move(assasin: View, direction: Direction, elementsOnContainer:List<Element>) {
        val layoutParams = assasin.layoutParams as FrameLayout.LayoutParams
        val saveCoordinate = Coordinate(
            layoutParams.topMargin,
            layoutParams.leftMargin
        ) //save before change
        when (direction) {
            Direction.UP -> {
                (assasin.layoutParams as FrameLayout.LayoutParams).topMargin += -CELL_SIZE
            }
            Direction.BOTTOM -> {
                (assasin.layoutParams as FrameLayout.LayoutParams).topMargin += CELL_SIZE
            }
            Direction.RIGHT -> {
                (assasin.layoutParams as FrameLayout.LayoutParams).leftMargin += CELL_SIZE
            }
            Direction.LEFT -> {
                (assasin.layoutParams as FrameLayout.LayoutParams).leftMargin += -CELL_SIZE
            }
        }
        val nextCoordinate = Coordinate(
            (assasin.layoutParams as FrameLayout.LayoutParams).topMargin,
            (assasin.layoutParams as FrameLayout.LayoutParams).leftMargin
        ) //save after change
        if (assasin.checkMoveThrought(nextCoordinate)
            && checkTankCanMoveThroughMaterial(nextCoordinate, elementsOnContainer)
        ) {
            container.removeView(assasin)
            container.addView(assasin, 0)
        } else {
            (assasin.layoutParams as FrameLayout.LayoutParams).topMargin = saveCoordinate.top
            (assasin.layoutParams as FrameLayout.LayoutParams).leftMargin = saveCoordinate.left
        }
    }

    private fun checkTankCanMoveThroughMaterial(coordinate: Coordinate, elementsOnContainer:List<Element>): Boolean {
        getTankCoordinates(coordinate).forEach {
            val element = getElementByCoordinates(it, elementsOnContainer)
            if (element != null && !element.material.CanGoThrough) {
                return false
            }
        }
        return true
    }


    private fun getTankCoordinates(topLeftCoordinate: Coordinate): List<Coordinate> {
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

}