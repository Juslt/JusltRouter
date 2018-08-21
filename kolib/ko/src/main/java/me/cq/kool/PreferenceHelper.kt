package me.cq.kool

import android.content.Context
import org.jetbrains.annotations.NotNull
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by phelps on 2018/1/25 0025.
 */
class PreferenceHelper<T>(private val context: Context, val name: String, @NotNull private val default: T) : ReadWriteProperty<Any?,T> {

    private val preference by lazy { context.getSharedPreferences("",Context.MODE_PRIVATE) }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getPreferenceValue()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, @NotNull value: T) {
        putPreferenceValue(value)
    }

    private fun getPreferenceValue() : T{
        with(preference){
            val res: Any = when(default) {
                is Long -> getLong(name, default)
                is String -> getString(name, default)
                is Int -> getInt(name, default)
                is Boolean -> getBoolean(name, default)
                is Float -> getFloat(name, default)
                else -> throw IllegalAccessException("This type can be saved into Preferences")
            }
            return res as T
        }
    }

    private fun putPreferenceValue(value: T){
        val editor = preference.edit()
        with(editor){
            when(value) {
                is Long -> putLong(name, value)
                is String -> putString(name, value)
                is Int -> putInt(name, value)
                is Boolean -> putBoolean(name, value)
                is Float -> putFloat(name, value)
                else -> throw IllegalAccessException("This type can be saved into Preferences")
            }
        }
        editor.apply()
    }
}