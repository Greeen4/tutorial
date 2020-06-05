package ru.appngo.towerdefense

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.appngo.towerdefense.models.Element

const val KEY_LEVEL = "key_level"

class LevelStore(context: Context) {
    private val prefs = (context as Activity).getPreferences(MODE_PRIVATE)
    private val gson = Gson()

    fun saveLevel(elementsOnContainer: List<Element>) {
        prefs.edit()
                .putString(KEY_LEVEL, gson.toJson(elementsOnContainer))
                .apply()
    }

    fun loadLevel(): List<Element>? {
        val level = prefs.getString(KEY_LEVEL, null) ?: return null
        val type = object : TypeToken<List<Element>>() {}.type
        val elementsFromStorage:MutableList<Element> =  gson.fromJson(level, type)
        val elementsNewId = mutableListOf<Element>()
        elementsFromStorage.forEach{
            elementsNewId.add(
                Element(material = it.material, coordinate = it.coordinate)
            )
        }
        return elementsNewId
    }
}
//
//
