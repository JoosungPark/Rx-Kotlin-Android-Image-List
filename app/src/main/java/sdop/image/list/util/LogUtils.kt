package sdop.image.list.util

import android.util.Log
import sdop.image.list.BuildConfig

/**
 *
 * Created by jei.park on 2017. 12. 21..
 */
enum class Tag(private val value: Boolean) {
    ImageServer(true),
    DEBUG(BuildConfig.DEBUG);

    fun getValue(): Boolean = value
}

class LogUtil {

    companion object {
        fun v(tag: Tag, vararg log: Any) {
            if (tag.getValue() && BuildConfig.DEBUG) {
                Log.v(getTag(tag), StringUtil.concat(true, *log))
            }
        }

        fun v(vararg log: Any) {
            if (Tag.DEBUG.getValue()) {
                Log.v(getTag(Tag.DEBUG), StringUtil.concat(true, *log))
            }
        }

        fun d(tag: Tag, vararg log: Any) {
            if (tag.getValue() && BuildConfig.DEBUG) {
                Log.d(getTag(tag), StringUtil.concat(true, *log))
            }
        }

        fun d(vararg log: Any) {
            if (Tag.DEBUG.getValue()) {
                Log.d(getTag(Tag.DEBUG), StringUtil.concat(true, *log))
            }
        }

        fun i(tag: Tag, vararg log: Any) {
            if (tag.getValue() && BuildConfig.DEBUG) {
                Log.i(getTag(tag), StringUtil.concat(true, *log))
            }
        }

        fun i(vararg log: Any) {
            if (Tag.DEBUG.getValue()) {
                Log.i(getTag(Tag.DEBUG), StringUtil.concat(true, *log))
            }
        }

        fun e(tag: Tag, vararg log: Any) {
            if (tag.getValue() && BuildConfig.DEBUG) {
                Log.e(getTag(tag), StringUtil.concat(true, *log) + "\n")
            }
        }

        fun e(vararg log: Any) {
            if (Tag.DEBUG.getValue()) {
                Log.e(getTag(Tag.DEBUG), StringUtil.concat(true, *log) + "\n")
            }
        }

        private fun getTag(tag: Tag): String = StringUtil.concat("TP ------- [[ ", tag.name, " ]] ------")
    }
}