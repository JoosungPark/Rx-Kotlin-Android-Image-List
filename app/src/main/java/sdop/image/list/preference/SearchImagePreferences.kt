package sdop.image.list.preference

/**
 *
 * Created by jei.park on 2018. 1. 2..
 */
class SearchImagePreferences(val name: String) : BasePreferences(name) {
    companion object {
        val kImageThreshold = "kImageThreshold"
    }
}