package sdop.image.list.http.model.server

import com.google.gson.reflect.TypeToken
import sdop.image.list.common.ImageConfig
import sdop.image.list.http.model.HTTPMethod
import sdop.image.list.http.model.ImageCommonRequest
import java.lang.reflect.Type

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class SearchImageRequest(private val query: String, private val start: String) : ImageCommonRequest<SearchImageResponse>() {
    override val responseType: Type get() = object : TypeToken<SearchImageResponse>() {}.type
    override val method: HTTPMethod get() = HTTPMethod.get
    override var url: String = "https://openapi.naver.com/v1/search/image?query=$query"
    override val uniqueToken: String? get() = "${this.javaClass.simpleName}_${query}_${start ?: ""}"

    init {
        header[ImageConfig.kClientId] = ImageConfig.clientID
        header[ImageConfig.kClientSecret] = ImageConfig.clientSecret
        if (start.toInt() > 0) {
            url += "&start=$start"
        }
    }

    override fun processResult(data: SearchImageResponse) {
        data.processResult()
    }

}