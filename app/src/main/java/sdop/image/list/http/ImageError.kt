package sdop.image.list.http

import sdop.image.list.http.model.server.ErrorCode

/**
 *
 * Created by jei.park on 2017. 12. 21..
 */
open class ImageThrowable(override val message: String? = "", override val cause: Throwable? = null) : Throwable(message, cause)

class ImageJavaError(private val error: Throwable) : ImageThrowable(error.localizedMessage, error)

class ImageServerError(val errorCode: ErrorCode = ErrorCode.INVALID, val errorMessage: String) : ImageThrowable(errorMessage, Throwable(errorMessage)) {
}