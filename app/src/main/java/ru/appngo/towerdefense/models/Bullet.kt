package ru.appngo.towerdefense.models

import android.view.View

class Bullet(
    val view: View,
    val origin: Coordinate,
    val target: Coordinate,
    val dx:Int,
    val dy:Int,
    var canMove: Boolean = true
) {
}