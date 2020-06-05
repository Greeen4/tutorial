package ru.appngo.towerdefense

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*
import ru.appngo.tankstutorial.R
import ru.appngo.towerdefense.drawers.*
import ru.appngo.towerdefense.enums.Direction
import ru.appngo.towerdefense.enums.Direction.*
import ru.appngo.towerdefense.enums.Material
import ru.appngo.towerdefense.models.Coordinate
import ru.appngo.towerdefense.models.Element
import ru.appngo.towerdefense.models.NPC
import ru.appngo.towerdefense.models.test

const val CELL_SIZE = 50
const val VERTICAL_CELL_AMOUNT = 48
const val HORIZONTAL_CELL_AMOUNT = 17
const val VERTICAL_MAX_SIZE = CELL_SIZE * VERTICAL_CELL_AMOUNT
const val HORIZONTAL_MAX_SIZE = CELL_SIZE * HORIZONTAL_CELL_AMOUNT

class MainActivity : AppCompatActivity() {
    private var editMode = false
    private var choseTowerMode = false
    private var isPlaying = false

//    private val player = test(
//        Element(
//            R.id.assasin,
//            Material.PLAYER,
//            Coordinate(0,0)
//        ), UP
//    )

    private val pon4ik by lazy {
        Element(
            material = Material.PON4IK,
            coordinate = Coordinate(
                300, 1500 //TODO !!!!!
//                container.layoutParams.height - Material.PON4IK.height * CELL_SIZE,
//                container.layoutParams.width - Material.PON4IK.width * CELL_SIZE / 2
            )
        )
    }

    private val gridDrawer by lazy {
        GridDrawer(container)
    }

    private val elementsDrawer by lazy {
        ElementsDrawer(container)
    }

//    private val bulletDrawer by lazy {
//        BulletDrawer(container, elementsDrawer.elementsOnContainer, enemyDrawer)
//    }

//    private val assasinDrawer by lazy {
//        AssasinDrawer(container)
//    }aaaaa



    private val levelStore by lazy {
        LevelStore(this)
    }


    private val enemyDrawer by lazy {
        EnemyDrawer(container, elementsDrawer.elementsOnContainer, Coordinate(200,200))
    }

    private val bulletDrawer by lazy {
        BulletDrawer(container, elementsDrawer.elementsOnContainer, enemyDrawer)
    }

    @SuppressLint("SourceLockedOrientationActivity", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        container.layoutParams = FrameLayout.LayoutParams(
            VERTICAL_MAX_SIZE,
            HORIZONTAL_MAX_SIZE
        )
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        editor_clear.setOnClickListener { elementsDrawer.currentMaterial = Material.EMPTY }
        editor_brick.setOnClickListener { elementsDrawer.currentMaterial = Material.BRICK }
        editor_concrete.setOnClickListener { elementsDrawer.currentMaterial = Material.CONCRETE }
        editor_grass.setOnClickListener { elementsDrawer.currentMaterial = Material.GRASS }
        eifel.setOnClickListener { elementsDrawer.currentMaterial = Material.EIFEL }
//        editor_pon4ik.setOnClickListener { elementsDrawer.currentMaterial = Material.PON4IK}
//        ivArrowUp.setOnClickListener {move(UP) }
//        ivArrowBottom.setOnClickListener {move(BOTTOM) }
//        ivArrowLeft.setOnClickListener {move(LEFT) }
//        ivArrowRight.setOnClickListener {move(RIGHT) }
//        ivArrowRight.setOnClickListener {assasinDrawer.move(assasin   RIGHT) }
//        ivArrowRight.setOnClickListener {move(RIGHT) }
//        ivArrowRight.setOnClickListener {move(RIGHT) }
//        ivArrowRight.setOnClickListener {move(RIGHT) }
//    fire.setOnClickListener {bulletDrawer.addNewBullete(/*player.element.coordinate*/)}//.moveBullet(
            /*Coordinate(
                player.element.coordinate.top,
                player.element.coordinate.left
            //)*///player.element, Coordinate(200, 200),  debug)}

        container.setOnTouchListener { _, event ->
            if (!editMode && !choseTowerMode)
                return@setOnTouchListener true
            elementsDrawer.onTouchContainer(event.x, event.y)
            return@setOnTouchListener true
        }
        elementsDrawer.drawListElem(levelStore.loadLevel())
        elementsDrawer.drawListElem(listOf(pon4ik))
//        elementsDrawer.elementsOnContainer.add(player.element)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }

    private fun startGame(){
        if(editMode || choseTowerMode || isPlaying)
            return
        enemyDrawer.startEnemy()
        enemyDrawer.moveEnemy()
        bulletDrawer.moveAllBullets()
        isPlaying = true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                switchEditMode()
                true
            }
            R.id.menu_save -> {
                levelStore.saveLevel(elementsDrawer.elementsOnContainer)
                true
            }
            R.id.menu_play -> {
                startGame()
                true
            }
            R.id.menu_tower ->{
                choseTower()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun choseTower(){
        if (editMode)
            return
        if (choseTowerMode) {
            gridDrawer.removeGrid()
            tower_container.visibility = GONE
        } else {
            gridDrawer.drawGrid()
            tower_container.visibility = VISIBLE
        }
        choseTowerMode = !choseTowerMode
    }

    private fun switchEditMode() {
        if (choseTowerMode)
            return
        if (editMode) {
            gridDrawer.removeGrid()
            materials_container.visibility = GONE
        } else {
            gridDrawer.drawGrid()
            materials_container.visibility = VISIBLE
        }
        editMode = !editMode
    }

//    private fun move(direction: Direction){
//        player.move(direction, container, elementsDrawer.elementsOnContainer)
//    }
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        when (keyCode) {
//            KEYCODE_DPAD_UP -> elementsDrawer.move(myTank, UP)
//            KEYCODE_DPAD_LEFT -> elementsDrawer.move(myTank, LEFT)
//            KEYCODE_DPAD_DOWN -> elementsDrawer.move(myTank, BOTTOM)
//            KEYCODE_DPAD_RIGHT -> elementsDrawer.move(myTank, RIGHT)
//        }
//        return super.onKeyDown(keyCode, event)
//    }
}
