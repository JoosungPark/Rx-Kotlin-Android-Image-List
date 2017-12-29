package sdop.image.list.util

/**
 *
 * Created by jei.park on 2017. 12. 21..
 */
class StringUtil {
    companion object {
        fun concat(vararg objects: Any) = concatenates(*objects)

        fun concat(widthBlank: Boolean, vararg objects: Any) = concatenates(widthBlank, *objects)

        private fun concatenates(vararg objects: Any) = concatenates(false, *objects)

        private fun concatenates(widthBlank: Boolean, vararg objects: Any): String {
            if (objects.isEmpty()) {
                return ""
            }

            val stringBuilder = StringBuilder()
            objects.forEach {
                stringBuilder.append(it)
                if (widthBlank) stringBuilder.append(" ")
            }

            return stringBuilder.toString()
        }
    }
}