package sdop.image.list.http.model

import com.google.gson.annotations.SerializedName

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class NaverImage : CommonImageResponse() {
    @SerializedName("title")
    val title: String? = null

    @SerializedName("link")
    val link: String? = null

    @SerializedName("thumbnail")
    val thumbnail: String? = null

    @SerializedName("sizeheight")
    val sizeheight: Int? = null

    @SerializedName("sizewidth")
    val sizewidth: Int? = null
}