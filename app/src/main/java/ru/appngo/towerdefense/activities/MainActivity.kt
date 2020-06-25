package ru.appngo.towerdefense.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*
import ru.appngo.tankstutorial.R
import ru.appngo.towerdefense.GameCore
import ru.appngo.towerdefense.LevelStore
import ru.appngo.towerdefense.ProgressIndicator
import ru.appngo.towerdefense.drawers.BulletDrawer
import ru.appngo.towerdefense.drawers.ElementsDrawer
import ru.appngo.towerdefense.drawers.EnemyDrawer
import ru.appngo.towerdefense.drawers.GridDrawer
import ru.appngo.towerdefense.enums.Material
import ru.appngo.towerdefense.models.Coordinate
import ru.appngo.towerdefense.models.Element

const val CELL_SIZE = 50
const val VERTICAL_CELL_AMOUNT = 48
const val HORIZONTAL_CELL_AMOUNT = 17
const val VERTICAL_MAX_SIZE = CELL_SIZE * VERTICAL_CELL_AMOUNT
const val HORIZONTAL_MAX_SIZE = CELL_SIZE * HORIZONTAL_CELL_AMOUNT
const val KEY_LEVEL_ONE = "level_one"
const val KEY_LEVEL_TWO = "level_two"
const val KEY_LEVEL_Three = "level_three"

class MainActivity : AppCompatActivity(), ProgressIndicator {
    private var editMode = false
    private var choseTowerMode = false
    private var levelSaveMode = false
    private var levelChoseMode = false
    private var isPlaying = false
    private var gameStarted = false
    private lateinit  var item: MenuItem

    private val gameCore by lazy{
    GameCore(this)
    }

    private val pon4ik by lazy {
        Element(
            material = Material.PON4IK,
            coordinate = Coordinate(
                300, 1500 //Change later
            )
        )
    }

    private val gridDrawer by lazy {
        GridDrawer(container)
    }

    private val elementsDrawer by lazy {
        ElementsDrawer(container)
    }

    override fun onBackPressed() {
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
        EnemyDrawer(container, elementsDrawer.elementsOnContainer, Coordinate(200,200), gameCore)
    }

    private val bulletDrawer by lazy {
        BulletDrawer(container, elementsDrawer.elementsOnContainer, enemyDrawer, gameCore)
    }

    private fun loadUnsaveLevel(){
        if (elementsDrawer.elementsOnContainer.isEmpty())
            elementsDrawer.drawListElem(listOf(pon4ik))
    }

    @SuppressLint("SourceLockedOrientationActivity", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        container.layoutParams = FrameLayout.LayoutParams(
            VERTICAL_MAX_SIZE,
            HORIZONTAL_MAX_SIZE
        )

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        editor_clear.setOnClickListener { elementsDrawer.currentMaterial = Material.EMPTY }
        editor_brick.setOnClickListener { elementsDrawer.currentMaterial = Material.BRICK }
        editor_concrete.setOnClickListener { elementsDrawer.currentMaterial = Material.CONCRETE }
        editor_grass.setOnClickListener { elementsDrawer.currentMaterial = Material.GRASS }
        eifel.setOnClickListener { elementsDrawer.currentMaterial = Material.EIFEL }
        lava.setOnClickListener { elementsDrawer.currentMaterial = Material.LAVA }
        level_1.setOnClickListener{
            showSaveLevel(1)
            levelStore.saveLevel(elementsDrawer.elementsOnContainer, KEY_LEVEL_ONE)
        }
        level_2.setOnClickListener{
            showSaveLevel(2)
            levelStore.saveLevel(elementsDrawer.elementsOnContainer, KEY_LEVEL_TWO)
        }
        level_3.setOnClickListener{
            showSaveLevel(3)
            levelStore.saveLevel(elementsDrawer.elementsOnContainer, KEY_LEVEL_Three)
        }
        chose_level_1.setOnClickListener{
            elementsDrawer.removeAll()
            elementsDrawer.drawListElem(levelStore.loadLevel(KEY_LEVEL_ONE))
            loadUnsaveLevel()
        }
        chose_level_2.setOnClickListener{
            elementsDrawer.removeAll()
            elementsDrawer.drawListElem(levelStore.loadLevel(KEY_LEVEL_TWO))
            loadUnsaveLevel()
        }
        chose_level_3.setOnClickListener{
            elementsDrawer.removeAll()
            elementsDrawer.drawListElem(levelStore.loadLevel(KEY_LEVEL_Three))
            loadUnsaveLevel()
        }
        container.setOnTouchListener { _, event ->
            if (!editMode && !choseTowerMode)
                return@setOnTouchListener true
            elementsDrawer.onTouchContainer(event.x, event.y)
            return@setOnTouchListener true
        }
        elementsDrawer.drawListElem(listOf(pon4ik))
        showProgress()

        /* personage for player
        editor_pon4ik.setOnClickListener { elementsDrawer.currentMaterial = Material.PON4IK}
        ivArrowUp.setOnClickListener {move(UP) }
        ivArrowBottom.setOnClickListener {move(BOTTOM) }
        ivArrowLeft.setOnClickListener {move(LEFT) }
        ivArrowRight.setOnClickListener {move(RIGHT) }
        ivArrowRight.setOnClickListener {assasinDrawer.move(assasin   RIGHT) }
        ivArrowRight.setOnClickListener {move(RIGHT) }
        ivArrowRight.setOnClickListener {move(RIGHT) }
        ivArrowRight.setOnClickListener {move(RIGHT) }
        fire.setOnClickListener {bulletDrawer.addNewBullete(/*player.element.coordinate*/)}//.moveBullet(
        Coordinate(
            player.element.coordinate.top,
            player.element.coordinate.left
        )player.element, Coordinate(200, 200),  debug)}
         */

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        item = menu.findItem(R.id.menu_play)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        removeProgress()
        return when (item.itemId) {
            R.id.menu_settings -> {
                switchEditMode()
                true
            }
            R.id.menu_save -> {
                saveLevel()
                true
            }
            R.id.menu_chose_level -> {
                loadLevel()
                true
            }
            R.id.menu_play -> {
                play()
                true
            }
            R.id.menu_tower -> {
                choseTower()
                true
            }
            R.id.menu_refresh -> {
                val i = Intent(this, this.javaClass)
                finish()
                this.startActivity(i)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun play(){
        if(editMode || choseTowerMode)
            return
        gameCore.startOrPause()
        if(gameCore.isPlay){
            startGame()
        }else{
            pauseGame()
        }
    }

    private fun loadLevel() {
        if (editMode || choseTowerMode || levelSaveMode)
            return
        if (levelChoseMode){
            chose_level_container.visibility = GONE
        }else{
            chose_level_container.visibility = VISIBLE
        }
        levelChoseMode= !levelChoseMode
    }

    private fun saveLevel() {
        if (editMode || choseTowerMode || levelChoseMode)
            return
        if (levelSaveMode){
            save_level_container.visibility = GONE
        }else{
            save_level_container.visibility = VISIBLE
        }
        if (levelSaveMode)
            level_save.visibility = GONE
        levelSaveMode = !levelSaveMode
    }


    private fun startGame(){
        item.icon = ContextCompat.getDrawable(this, R.drawable.ic_pause)
        if (gameStarted)
            return
        gameStarted = true
        enemyDrawer.startEnemy()
        enemyDrawer.moveEnemy()
        bulletDrawer.moveAllBullets()
        isPlaying = true
    }

    private fun pauseGame(){
        item.icon = ContextCompat.getDrawable(this, R.drawable.ic_play)
        gameCore.pause()
    }
//    exception ?
//    override fun onPause() {
//        super.onPause()
//        pauseGame()
//    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            recreate()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("ResourceAsColor")
    override fun showProgress() {
        container.visibility = GONE
        main_container.setBackgroundColor(R.color.gray)
        name_level.visibility = VISIBLE
    }

    private fun showSaveLevel(number_level: Int) {
        level_save.text = "Level " + number_level.toString() + " was saved!"
        level_save.visibility = VISIBLE
//        sleep(10000)
//        level_save.visibility = GONE
    }

    @SuppressLint("ResourceAsColor")
    override fun removeProgress() {
        container.visibility = VISIBLE
        main_container.setBackgroundColor(R.color.blue)
        name_level.visibility = GONE

    }
}
