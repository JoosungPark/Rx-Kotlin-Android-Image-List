package sdop.image.list.http.model

import com.google.gson.annotations.SerializedName

/**
 *
 * Created by jei.park on 2017. 12. 21..
 */
interface ImageResponse {
    val errorCode: String?
    val errorMessage: String?
}

abstract class CommonImageResponse : ImageResponse {
    protected val DEBUG_TAG = this.javaClass.simpleName

    @SerializedName("errorCode")
    override val errorCode: String? = null

    @SerializedName("errorMessage")
    override val errorMessage: String? = null

    open fun processResult() {

    }
}