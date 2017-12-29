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
        fun Error(error: Throwable): RequestError = RequestError(error)
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
    val error: MaybeVariable<ImageThrowable>
    val isNetworking: Variable<Boolean>
    val requestState: Variable<RequestState>
    val header: HashMap<String, String>

    fun processResult(data: ResponseType)
    fun getParams(): Any?
    fun isSuccess(): Boolean
    fun getErrorMessage(): String
    fun getError(): ImageThrowable?
}

abstract class ImageCommonRequest<ResponseType : ImageResponse> : ImageRequest<ResponseType> {
    override val response = MaybeVariable<ResponseType>(null)
    override val error = MaybeVariable<ImageThrowable>(null)
    override val isNetworking = Variable(false)
    override val requestState = Variable<RequestState>(RequestState.Ready)

    override val header = hashMapOf<String, String>()

    override fun getParams(): Any? = null

    override fun isSuccess(): Boolean = !isNetworking.value && error.value == null && response.value != null && response.value!!.errorMessage == null && response.value!!.errorCode == null

    override fun getErrorMessage(): String {
        error.value?.let { return it.localizedMessage }
        response.value?.let { return it.errorMessage ?: "" }
        return ""
    }

    override fun getError(): ImageThrowable? {
        error.value?.let { return it }
        response.value?.let {
            if (it.errorMessage != null ) {
                return ImageServerError(ErrorCode.from(it.errorCode), it.errorMessage!!)
            }
        }
        return null
    }
}