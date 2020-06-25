package ru.appngo.towerdefense.enums

import kotlinx.android.synthetic.main.activity_main.view.*
import ru.appngo.tankstutorial.R
import ru.appngo.towerdefense.drawers.EnemyDrawer

const val PON4IK_HEIGHT = 3
const val PON4IK_WIDTH = 4
const val ENEMY_SIZE = 2
const val STD_SIZE= 1

enum class Material(
    val CanGoThrough: Boolean,
    val bulletThrough: Boolean,
    val canDestroy: Boolean,
    val amount: Int,
    val width:Int,
    val height:Int,
    val image:Int
) {
    EMPTY(true, true, true, 0, 0, 0, 0),
    BRICK(false, false, true, 0, STD_SIZE, STD_SIZE, R.drawable.brick),
    CONCRETE(false, false, false, 0, STD_SIZE, STD_SIZE, R.drawable.concrete),
    GRASS(true, true, false, 0, STD_SIZE, STD_SIZE, R.drawable.grass),
    PON4IK (false, true, false, 1, PON4IK_WIDTH, PON4IK_HEIGHT, R.drawable.pon4ik),
    ENEMY (false, false, true, 0, ENEMY_SIZE, ENEMY_SIZE, R.drawable.demon),
    ENEMY_2 (false, false, true, 0, ENEMY_SIZE, ENEMY_SIZE, R.drawable.demon_1hp),
    PLAYER(false, false, true, 0, ENEMY_SIZE, ENEMY_SIZE, R.drawable.assasin),
    EIFEL(false, false, false, 5, STD_SIZE, STD_SIZE, R.drawable.eiffel_tower),
    LAVA(true, true, false, 10, STD_SIZE, STD_SIZE, R.drawable.lava)

}
