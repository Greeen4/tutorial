package ru.appngo.towerdefense.models

import android.text.style.LineHeightSpan
import android.view.View
import ru.appngo.towerdefense.enums.Material
import ru.appngo.towerdefense.models.Coordinate

data class Element constructor (
    val viewId:Int = View.generateViewId(),
    var material: Material,
    var coordinate: Coordinate,
    val width: Int = material.width,
    val height: Int = material. height,
    var hp: Int = material.hp,
    val textViewId:Int = View.generateViewId()
){
}
