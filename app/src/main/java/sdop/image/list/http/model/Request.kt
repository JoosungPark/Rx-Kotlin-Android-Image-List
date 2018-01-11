package sdop.image.list.http.model

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import sdop.image.list.http.ImageServerError
import sdop.image.list.http.ImageThrowable
import sdop.image.list.http.model.server.ErrorCode
import sdop.image.list.rx.MaybeVariable
import sdop.image.list.rx.Variable
import java.lang.reflect.Type

/**
 *
 * Created by jei.park on 2017. 12. 21..
 */
enum class HTTPMethod {
    get,
    post,
    ;
}

sealed class RequestState {
    companion object {
        val Initializing = RequestInitializing
        val Ready = RequestReady
    }
}

object RequestInitializing : RequestState()
object RequestReady : RequestState()
data class RequestError(val error: Throwable) : RequestState()

interface ImageRequest<ResponseType: ImageResponse> {
    val responseType: Type get() = object : TypeToken<ResponseType>() {}.type
    val method: HTTPMethod
    val url: String
    val uniqueToken: String?
    val response: MaybeVariable<ResponseType>
    val isNetworking: Variable<Boolean>
    val requestState: Variable<RequestState>
    val header: HashMap<String, String>

    fun processResult(data: ResponseType)
    fun getParams(): Any?
}

abstract class ImageCommonRequest<ResponseType : ImageResponse> : ImageRequest<ResponseType> {
    override val response = MaybeVariable<ResponseType>(null)
    override val isNetworking = Variable(false)
    override val requestState = Variable<RequestState>(RequestState.Ready)

    override val header = hashMapOf<String, String>()

    override fun getParams(): Any? = null
}