package ru.appngo.towerdefense.drawers

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import ru.appngo.towerdefense.activities.CELL_SIZE
import ru.appngo.tankstutorial.R
import ru.appngo.towerdefense.GameCore
import ru.appngo.towerdefense.enums.Material
import ru.appngo.towerdefense.models.Bullet
import ru.appngo.towerdefense.models.Coordinate
import ru.appngo.towerdefense.models.Element
import ru.appngo.towerdefense.utils.calculateStepToMove
import ru.appngo.towerdefense.utils.checkMoveThrought
import ru.appngo.towerdefense.utils.runOnUiThread
import kotlin.math.sqrt

private const val BULLETE_WIDTH = 10
private const val BULLET_HEIGHT = 10
private const val BULLETE_DAMAGE = 20

class BulletDrawer(
    val container:FrameLayout,
    private val elementsOnContainer: MutableList<Element>,
    private val enemyDrawer: EnemyDrawer,
    private val gameCore: GameCore
) {
    private val allBullet = mutableListOf<Bullet>()


    fun addNewBullete(/*origin:Coordinate*/){
        val originBulletsList = elementsOnContainer.filter { it.material == Material.EIFEL }
        if (originBulletsList.isEmpty())
            return
        var direction:Coordinate = Coordinate(0,0)
        if (!enemyDrawer.enemies.isEmpty())
            direction = calculateDirectionToEnemies()//enemyDrawer.enemies[0].element.coordinate
        originBulletsList.forEach {
            val coord = calculateStepToMove(it.coordinate, direction)
            allBullet.add(
                Bullet(
                    createBullet(it.coordinate),
                    it.coordinate,
                    direction,
                    coord.top,
                    coord.left
                )
            )
        }
    }

    private fun calculateDirectionToEnemies(): Coordinate {
        var direction = enemyDrawer.enemies[0].element.coordinate
        return Coordinate(direction.top - CELL_SIZE/2, direction.left + CELL_SIZE/2)
    }

//    private fun offsetToTatger(origin: Coordinate, direction: Coordinate ): Pair<Int, Int> {
//        val y1 = origin.top.toDouble()
//        val x1 = origin.left.toDouble()
//        val x2 = direction.left.toDouble()
//        val y2 = direction.top.toDouble()
//        var dy = (y2 - y1) / (sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)))
//        var dx = (x2 - x1) / (sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)))
//        return (dx*10).toInt() to (dy*10).toInt()
//    }

    fun moveAllBullets() {
        Thread(Runnable {
            var timeSpawn = 0
            while(true){
                if(!gameCore.isPlay)
                    continue
                if (timeSpawn == 100){
                    addNewBullete()
                    timeSpawn = 0
                }
                allBullet.toList().forEach {// bullet ->
                    val view: View = it.view
                    if(view.checkMoveThrought(Coordinate(view.top, view.left)) && it.canMove) {
                        (view.layoutParams as FrameLayout.LayoutParams).topMargin += it.dy //(dy * 10).toInt()
                        (view.layoutParams as FrameLayout.LayoutParams).leftMargin += it.dx //(dx * 10).toInt()
                        compareCoordinate(
                            it,
                            Coordinate(
                                (view.layoutParams as FrameLayout.LayoutParams).topMargin,
                                (view.layoutParams as FrameLayout.LayoutParams).leftMargin
                            )
                        )
                        container.runOnUiThread {
                            container.removeView(view)
                            container.addView(view)
                        }
                    }else{
                        it.canMove = false
                    }
                }
                timeSpawn +=1
                Thread.sleep(10)
                removeBullets()
                Thread.sleep(10)
            }
        }).start()

    }

    private fun removeBullets(){
        val removingBulletList = allBullet.filter { !it.canMove }
        removingBulletList.forEach {
            container.runOnUiThread {
                container.removeView(it.view)
                }
        }
        allBullet.removeAll(removingBulletList)
    }

    private fun removeView(element: Element){
        val activity = container.context as Activity
        activity.runOnUiThread{
            container.removeView(activity.findViewById(element.viewId))
            container.removeView(activity.findViewById(element.textViewId))
        }
    }
    private fun removeElementAndStopBullet(element: Element?, bullet: Bullet){
        if (element != null){
            if(element.material.canDestroy) {
                bullet.canMove = false
                element.hp -= BULLETE_DAMAGE
                if(element.hp <= 0) {
                    removeView(element)
                    if (element.material.enemy)//material == Material.ENEMY)
                        removeEnemy(element)
                    else
                        elementsOnContainer.remove(element)
                }
            }else{
                bullet.canMove = false
            }
            if (element.material.bulletThrough)
                return
        }
    }

    private fun removeEnemy(element: Element){
        val enemyElements = enemyDrawer.enemies.map{it.element} //TODO optimization
        val enemyIndex = enemyElements.indexOf(element)
        enemyDrawer.removeEnemy(enemyIndex)
        if (enemyDrawer.allEnemiesKilled()){
            gameCore.win(enemyDrawer.getScore())
        }
//        enemyDrawer.addToRemoveEnemiesList(enemyIndex)
//        enemyDrawer.removeEnemiesOnContainer.add(element)
    }
    private fun compareCoordinate(
        bullet: Bullet,
        bulleteCoordinate:Coordinate
    ){
        var intersectElement = compareWithElements(bulleteCoordinate)
        if (intersectElement == null){
            intersectElement = compareWithEnemies(bulleteCoordinate)
        }
        if(intersectElement == null || intersectElement.coordinate == bullet.origin
            || intersectElement.material.CanGoThrough)
            return
        removeElementAndStopBullet(intersectElement, bullet)
    }

    private fun compareWithElements(bulleteCoordinate: Coordinate): Element? {
        for (i in elementsOnContainer) {
            if (bulleteCoordinate.top >= i.coordinate.top
                && bulleteCoordinate.top <= i.coordinate.top + i.height * CELL_SIZE
                && bulleteCoordinate.left >= i.coordinate.left
                && bulleteCoordinate.left <= i.coordinate.left + i.width * CELL_SIZE
            ) {
                return i
            }
        }
        return null
    }

    private fun compareWithEnemies(bulleteCoordinate: Coordinate): Element? {
        for (i in enemyDrawer.enemies.toList()){
            if (bulleteCoordinate.top >= i.element.coordinate.top
                && bulleteCoordinate.top <= i.element.coordinate.top + i.element.height * CELL_SIZE
                && bulleteCoordinate.left >= i.element.coordinate.left
                && bulleteCoordinate.left <= i.element.coordinate.left + i.element.width * CELL_SIZE
            ) {
                return i.element
            }
        }
        return null
    }

    private fun createBullet(begin: Coordinate): ImageView {
        return ImageView(container.context)
            .apply {
                this.setImageResource(R.drawable.bullet)
                val bulletCoordinate = getBulletCoordinates(this, begin)
                this.layoutParams = FrameLayout.LayoutParams(
                    BULLETE_WIDTH,
                    BULLET_HEIGHT
                )
                (this.layoutParams as FrameLayout.LayoutParams).topMargin = bulletCoordinate.top
                (this.layoutParams as FrameLayout.LayoutParams).leftMargin = bulletCoordinate.left
            }
    }

    private fun getBulletCoordinates(bullet: ImageView, objFromBullet: Coordinate): Coordinate {
        return Coordinate(
            objFromBullet.top + CELL_SIZE /2,
            objFromBullet.left + CELL_SIZE /2
        )
    }
}