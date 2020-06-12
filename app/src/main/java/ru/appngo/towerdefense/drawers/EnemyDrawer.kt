package ru.appngo.towerdefense.drawers

import android.app.Activity
import android.widget.FrameLayout
import ru.appngo.towerdefense.activities.CELL_SIZE
import ru.appngo.towerdefense.GameCore
import ru.appngo.towerdefense.enums.Material
import ru.appngo.towerdefense.models.Coordinate
import ru.appngo.towerdefense.models.Element
import ru.appngo.towerdefense.models.NPC
import ru.appngo.towerdefense.utils.drawElement
import kotlin.math.sqrt


private const val ENEMY_AMOUNT = 2
private  val  HP_ENEMY = 70

class EnemyDrawer(
    private val container: FrameLayout,
    private val elements:MutableList<Element>,
    private var target:Coordinate,
    private val gameCore: GameCore
) {
    var enemies = mutableListOf<NPC>()
    var removeEnemiesOnContainer = mutableListOf<Element>()
    var removeEnemiesIndexList = mutableListOf<Int>()
    private  val respawn:Coordinate = Coordinate(0,0)
    private var amount = 0
    private var killedEnemy = 0


    fun startEnemy(){
        Thread(Runnable{
            while(amount < ENEMY_AMOUNT) {
                if(!gameCore.isPlay)
                    continue
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
            hp = HP_ENEMY
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
                if(!gameCore.isPlay)
                    continue
                goThroughtAllEnemies(dx, dy)
                Thread.sleep(100)
            }
        }).start()
    }

//    private fun removeKilledEnemies() {
//        removeEnemiesIndexList.forEach {
//            removeEnemy(it)
//        }
//        removeEnemiesIndexList.clear()
//        removeEnemiesOnContainer.forEach {
//            elements.remove(it)
//        }
//        removeEnemiesOnContainer.clear()
//    }

//    private fun removeKilled(){
//        val removing = mutableListOf<NPC>()
//        val allEnemies = elements.filter {it.material==Material.ENEMY}
//        enemies.toList().forEach {
//            if(!allEnemies.contains(it.element))
//                removing.add(it)
//        }
//        enemies.removeAll(removing)
//    }

    fun allEnemiesKilled():Boolean{
        return killedEnemy == ENEMY_AMOUNT
    }

    fun getScore() = killedEnemy*100

//    fun getScore(): Int {
//        return amount*100
//    }

    private fun goThroughtAllEnemies(dx: Double, dy: Double){
        enemies.toList().forEach {
            it.move((dx*10).toInt(), (dy*10).toInt(), container, elements, gameCore, killedEnemy)
        }
        val enemiesKilledByLava = enemies.filter { it.element.hp <=0 }
        killedEnemy += enemiesKilledByLava.size
        enemies.removeAll(enemiesKilledByLava)
        val activity = container.context as Activity
        activity.runOnUiThread{
            enemiesKilledByLava.forEach {
                container.removeView(activity.findViewById(it.element.viewId))
                container.removeView(activity.findViewById(it.element.textViewId))
            }
        }
        if (allEnemiesKilled()){
            gameCore.win(getScore())
        }

}

    fun removeEnemy(enemyIndex: Int) {
        killedEnemy ++
        if (enemyIndex < 0 ) return
        enemies.removeAt(enemyIndex)

    }

//    fun addToRemoveEnemiesList(enemyIndex: Int){
//        removeEnemiesIndexList.add(enemyIndex)
//    }


}