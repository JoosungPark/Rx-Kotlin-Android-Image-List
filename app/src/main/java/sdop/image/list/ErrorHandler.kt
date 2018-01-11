package sdop.image.list

import sdop.image.list.http.ImageJavaError
import sdop.image.list.http.ImageServerError
import sdop.image.list.util.Notifier

/**
 *
 * Created by jei.park on 2018. 1. 11..
 */
interface ErrorHandlerPresentable {
    fun onError(error: Throwable)
}

fun handleError(error: Throwable) {
    when (error) {
        is ImageServerError -> Notifier.toast("server error occurred.\n error code : ${error.errorCode} \n${error.errorMessage}")
        is ImageJavaError -> Notifier.toast(error.localizedMessage)
        else -> Notifier.toast(error.localizedMessage)
    }
}