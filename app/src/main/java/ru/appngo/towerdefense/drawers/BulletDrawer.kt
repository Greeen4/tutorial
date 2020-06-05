package ru.appngo.towerdefense.drawers

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import ru.appngo.towerdefense.CELL_SIZE
import ru.appngo.tankstutorial.R
import ru.appngo.towerdefense.enums.Material
import ru.appngo.towerdefense.models.Bullet
import ru.appngo.towerdefense.models.Coordinate
import ru.appngo.towerdefense.models.Element
import ru.appngo.towerdefense.utils.checkMoveThrought
import ru.appngo.towerdefense.utils.runOnUiThread
import kotlin.math.sqrt

private const val BULLETE_WIDTH = 10
private const val BULLET_HEIGHT = 10

class BulletDrawer(
    val container:FrameLayout,
    private val elementsOnContainer: MutableList<Element>,
    val enemyDrawer: EnemyDrawer
) {

    private var canBulletGo = true
    private var bulletThread: Thread? = null
    private val allBullet = mutableListOf<Bullet>()



    fun addNewBullete(/*origin:Coordinate*/){
        val originBulletsList = elementsOnContainer.filter { it.material == Material.EIFEL }
        if (originBulletsList.isEmpty())
            return
        var direction:Coordinate = Coordinate(0,0)
        if (!enemyDrawer.enemies.isEmpty())
            direction = enemyDrawer.enemies[0].element.coordinate
        originBulletsList.forEach {
            val y1 = it.coordinate.top.toDouble()
            val x1 = it.coordinate.left.toDouble()
            val x2 = direction.left.toDouble()
            val y2 = direction.top.toDouble()
            var dy = (y2 - y1) / (sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)))
            var dx = (x2 - x1) / (sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)))
            allBullet.add(
                Bullet(
                    createBullet(it.coordinate),
                    it.coordinate,
                    direction,
                    (dx * 10).toInt(),
                    (dy * 10).toInt()
                )
            )
        }
    }

    fun moveAllBullets() {
        Thread(Runnable {
            var timeSpawn = 0
            while(true){
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
        }
    }
    private fun removeElementAndStopBullet(element: Element?, bullet: Bullet){
        if (element != null){
            if (element.material.bulletThrough)
                return
            if(element.material.canDestroy) {
                bullet.canMove = false
                element.hp -= 1
                if(element.hp == 0) {
                    removeView(element)
                    if (element.material == Material.ENEMY)
                        removeEnemy(element)
                    else
                        elementsOnContainer.remove(element)
                }
            }else{
                bullet.canMove = false
            }
        }
    }

    private fun removeEnemy(element: Element){
        val enemyElements = enemyDrawer.enemies.map{it.element} //TODO optimization
        val enemyIndex = enemyElements.indexOf(element)
        enemyDrawer.removeEnemy(enemyIndex)
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
            || intersectElement.material == Material.EIFEL)
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
            objFromBullet.top + CELL_SIZE/2,
            objFromBullet.left + CELL_SIZE/2
        )
    }
}