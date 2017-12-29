package sdop.image.list.http.model.server

import com.google.gson.annotations.SerializedName
import sdop.image.list.http.model.CommonImageResponse
import sdop.image.list.http.model.NaverImage

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class SearchImageResponse : CommonImageResponse() {
    @SerializedName("total")
    val total: Int? = null

    @SerializedName("start")
    val start: Int? = null

    @SerializedName("display")
    val display: Int? = null

    @SerializedName("items")
    val items: ArrayList<NaverImage>? = null
}