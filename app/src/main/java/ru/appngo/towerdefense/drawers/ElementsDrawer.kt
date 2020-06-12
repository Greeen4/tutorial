package ru.appngo.towerdefense.drawers

import android.widget.FrameLayout
import ru.appngo.towerdefense.activities.CELL_SIZE
import ru.appngo.towerdefense.enums.Material
import ru.appngo.towerdefense.models.Coordinate
import ru.appngo.towerdefense.models.Element
import ru.appngo.towerdefense.utils.drawElement
import ru.appngo.towerdefense.utils.getElementByCoordinates



class ElementsDrawer(val container: FrameLayout) {

    var currentMaterial = Material.EMPTY
    val elementsOnContainer = mutableListOf<Element>()

    fun onTouchContainer(x: Float, y: Float) {
        val topMargin = y.toInt() - (y.toInt() % CELL_SIZE)
        val leftMargin = x.toInt() - (x.toInt() % CELL_SIZE)
        val coordinate =
            Coordinate(topMargin, leftMargin)
        if (currentMaterial == Material.EMPTY) {
            eraseView(coordinate)
        } else {
            drawOrReplaceView(coordinate)
        }
    }

    private fun drawOrReplaceView(coordinate: Coordinate) {
        val viewOnCoordinate = getElementByCoordinates(coordinate, elementsOnContainer)
        if (viewOnCoordinate == null) {
            createElementDrawView(coordinate)
            return
        }
        if (viewOnCoordinate.material != currentMaterial) {
            replaceView(coordinate)
        }
    }

    private fun replaceView(coordinate: Coordinate) {
        eraseView(coordinate)
        createElementDrawView(coordinate)
    }


    private fun eraseView(coordinate: Coordinate) {
        removeElement(getElementByCoordinates(coordinate, elementsOnContainer))
        for (element in getElementsUnderCurrent(coordinate))
            removeElement(element)
    }

//    private fun removeIfItOne(){
//        if (currentMaterial.amount == 1)
//            elementsOnContainer.firstOrNull{it.material == currentMaterial}?.coordinate?.let{eraseView(it)}
//    }

    fun removeAll(){
        elementsOnContainer.forEach {container.removeView(container.findViewById(it.viewId))}
        elementsOnContainer.clear()
    }

    fun removeElement(element: Element?){
        if(element != null){
            container.removeView(container.findViewById(element.viewId))
            elementsOnContainer.remove(element)
        }
    }

    private fun getElementsUnderCurrent(coordinate: Coordinate):List<Element>{
        val elements:MutableList<Element> = mutableListOf<Element>()
        for (element in elementsOnContainer) {
            for (height in 0 until currentMaterial.height) {
                for (width in 0 until currentMaterial.width) {
                    if (coordinate == Coordinate(
                        top = element.coordinate.top + height * CELL_SIZE,
                        left = element.coordinate.left + width * CELL_SIZE
                    ))
                        elements.add(element)
                }
            }
        }
        return elements
    }

    private fun removeUnwantedInstances() {
        if (currentMaterial.amount != 0) {
            val erasingElements = elementsOnContainer.filter { it.material == currentMaterial }
            if (erasingElements.size >= currentMaterial.amount) {
                eraseView(erasingElements[0].coordinate)
            }
        }
    }

    private fun drawElement(element: Element){
        removeUnwantedInstances()
        element.drawElement(container)
        elementsOnContainer.add(element)
    }

    private fun createElementDrawView(coordinate: Coordinate){
//        removeIfItOne()
        val element = Element(
                material = currentMaterial,
                coordinate = coordinate
            )
        drawElement(element)
//        element.drawElement(container)
//        elementsOnContainer.add(element)
    }

    fun drawListElem(elements: List<Element>?){
        if (elements==null)
            return
        for (elem in elements){
            currentMaterial = elem.material
            drawElement(elem)
        }
        currentMaterial = Material.EMPTY
    }

}
