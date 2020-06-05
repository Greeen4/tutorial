package ru.appngo.towerdefense.drawers

import android.view.View
import android.widget.FrameLayout
import ru.appngo.towerdefense.CELL_SIZE
import ru.appngo.towerdefense.enums.Direction
import ru.appngo.towerdefense.enums.Material
import ru.appngo.towerdefense.models.Coordinate
import ru.appngo.towerdefense.models.Element
import ru.appngo.towerdefense.models.NPC
import ru.appngo.towerdefense.models.test
import ru.appngo.towerdefense.utils.drawElement
import ru.appngo.towerdefense.utils.runOnUiThread
import kotlin.math.sqrt


private const val ENEMY_AMOUNT = 20
private  val  HP_ENEMY = 2

class EnemyDrawer(
    private val container: FrameLayout,
    private val elements:MutableList<Element>,
    private var target:Coordinate
) {
    var enemies = mutableListOf<NPC>()
    var removeEnemiesOnContainer = mutableListOf<Element>()
    var removeEnemiesIndexList = mutableListOf<Int>()
    private  val respawn:Coordinate = Coordinate(0,0)
    private var amount = 0


    fun startEnemy(){
        Thread(Runnable{
            while(amount < ENEMY_AMOUNT) {
                drawEnemy()
                Thread.sleep(1000)
                amount++
            }
        }).start()
    }

    private fun drawEnemy(){
        val enemy = NPC(Element(
            material = Material.ENEMY,
            coordinate = respawn,
            hp = 2
        )//, Direction.BOTTOM
        )
        enemy.element.drawElement(container)
        //elements.add(enemy.element)
        enemies.add(enemy)
    }



    fun moveEnemy(){
        val tmp = elements.firstOrNull { it.material==Material.PON4IK }
        if (tmp != null)
            target = Coordinate(
                tmp.coordinate.top + tmp.height* CELL_SIZE,
                tmp.coordinate.left + tmp.height* CELL_SIZE
            )
        val y1 = respawn.top.toDouble()
        val x1 = respawn.left.toDouble()
        val x2 = target.left.toDouble()
        val y2 = target.top.toDouble()
        var dy = (y2 - y1) / (sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)))
        var dx = (x2 - x1) / (sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)))
        Thread(Runnable{
            while(true){
                    goThroughtAllEnemies(dx, dy)
                Thread.sleep(100)
            }
        }).start()
    }

    private fun removeKilledEnemies() {
        removeEnemiesIndexList.forEach {
            removeEnemy(it)
        }
        removeEnemiesIndexList.clear()
        removeEnemiesOnContainer.forEach {
            elements.remove(it)
        }
        removeEnemiesOnContainer.clear()
    }

    private fun removeKilled(){
        val removing = mutableListOf<NPC>()
        val allEnemies = elements.filter {it.material==Material.ENEMY}
        enemies.toList().forEach {
            if(!allEnemies.contains(it.element))
                removing.add(it)
        }
        enemies.removeAll(removing)
    }

    private fun goThroughtAllEnemies(dx: Double, dy: Double){
            enemies.toList().forEach {
                it.move((dx*10).toInt(), (dy*10).toInt(), container, elements)
            }
}

    fun removeEnemy(enemyIndex: Int) {
        if (enemyIndex < 0 ) return
        enemies.removeAt(enemyIndex)

    }

    fun addToRemoveEnemiesList(enemyIndex: Int){
        removeEnemiesIndexList.add(enemyIndex)
    }


}