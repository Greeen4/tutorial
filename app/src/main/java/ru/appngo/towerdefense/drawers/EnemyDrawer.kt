package ru.appngo.towerdefense.drawers

import android.app.Activity
import android.widget.FrameLayout
import ru.appngo.towerdefense.activities.CELL_SIZE
import ru.appngo.towerdefense.GameCore
import ru.appngo.towerdefense.enums.Material
import ru.appngo.towerdefense.models.Coordinate
import ru.appngo.towerdefense.models.Element
import ru.appngo.towerdefense.models.NPC
import ru.appngo.towerdefense.utils.calculateStepToMove
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
    private  val respawn:Coordinate = Coordinate(0,0)
    private var amount = 0
    private var killedEnemy = 0
    private lateinit var offset: Coordinate


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
        ))
        enemy.element.drawElement(container)
        enemies.add(enemy)
    }

    private fun searchOffset() {
        val tmp = elements.firstOrNull { it.material==Material.PON4IK }
        if (tmp != null)
            target = Coordinate(
                tmp.coordinate.top + tmp.height* CELL_SIZE,
                tmp.coordinate.left + tmp.height* CELL_SIZE
            )
        offset = calculateStepToMove(respawn, target)
    }

    private fun moving(){

    }

    fun moveEnemy(){
        searchOffset()
        Thread(Runnable{
            while(true){
                if(!gameCore.isPlay)
                    continue
                goThroughtAllEnemies()//offset.first, offset.second)
                Thread.sleep(100)
            }
        }).start()
    }

    fun allEnemiesKilled():Boolean{
        return killedEnemy == ENEMY_AMOUNT
    }

    fun getScore() = killedEnemy*100

    private fun checkLavaWin(){
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

    private fun goThroughtAllEnemies(){
        enemies.toList().forEach {
            it.move(offset.top, offset.left, container, elements, gameCore, killedEnemy)
        }
        checkLavaWin()
    }

    fun removeEnemy(enemyIndex: Int) {
        killedEnemy ++
        if (enemyIndex < 0 ) return
        enemies.removeAt(enemyIndex)

    }
}