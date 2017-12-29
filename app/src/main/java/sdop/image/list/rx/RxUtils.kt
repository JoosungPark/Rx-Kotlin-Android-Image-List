package sdop.image.list.rx

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Looper
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import sdop.image.list.http.ImageJavaError
import sdop.image.list.http.ImageThrowable
import sdop.image.list.http.model.ImageRequest
import sdop.image.list.http.model.ImageResponse
import sdop.image.list.http.model.server.ErrorCode
import sdop.image.list.util.Notifier
import java.util.HashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 *
 * Created by jei.park on 2017. 12. 21..
 */
fun delay(delay: Long = 0, block: Boolean = false, runner: (() -> Unit)) {
    val isMainThread = Looper.myLooper() == Looper.getMainLooper()
    if (isMainThread || !block) {
        Observable.just(0)
                .delay(delay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .take(1)
                .subscribe { runner() }
    } else {
        val latch = CountDownLatch(1)
        Observable.just(0)
                .delay(delay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .take(1)
                .subscribe {
                    runner()
                    latch.countDown()
                }
        latch.await()
    }
}

fun <T> Observable<T>.debug(name: String = "", obj: Any? = null): Observable<T> {
    return this.doOnEach {
        if (obj == null) {
            println(" [$name] : $it")
        } else {
            println(" [${obj.javaClass.name}::$name] : $it")
        }
    }
}

class ImageResponseHandler<T : ImageResponse>(val doNotShowError: Boolean = false) : Observer<ImageRequest<T>> {
    var errorCodeMap = HashMap<ErrorCode, (T) -> Unit>()
    var onSuccess: ((T) -> Unit)? = null
    var onErrorRunner: ((ImageThrowable) -> Unit)? = null
    var onFinally: (() -> Unit)? = null

    fun runOnSuccess(runner: (T) -> Unit): ImageResponseHandler<T> {
        onSuccess = runner
        return this
    }

    fun runOn(errorCode: ErrorCode, runner: (T) -> Unit): ImageResponseHandler<T> {
        errorCodeMap[errorCode] = runner
        return this
    }

    fun runOn(errorCodes: Iterable<ErrorCode>, runner: (T) -> Unit): ImageResponseHandler<T> {
        errorCodes.forEach { errorCodeMap[it] = runner }
        return this
    }

    fun finally(runner: () -> Unit): ImageResponseHandler<T> {
        onFinally = runner
        return this
    }

    fun runOnError(runner: (ImageThrowable) -> Unit): ImageResponseHandler<T> {
        onErrorRunner = runner
        return this
    }

    fun showError(msg: String) {
        if (!doNotShowError) {
            Notifier.toast(msg)
        }
    }

    override fun onNext(r: ImageRequest<T>) {
        if (!r.isSuccess()) {
            var didHandleError = false
            val response = r.response.value
            if (response != null) {
                val code = ErrorCode.from(response.errorCode)

                val runner = errorCodeMap[code]
                if (runner != null) {
                    runner(response)
                    didHandleError = true
                } else if (onErrorRunner != null) {
                    r.getError()?.let {
                        onErrorRunner!!(it)
                        didHandleError
                    }
                }
            }

            if (!didHandleError) {
                showError(r.getErrorMessage())
            }
        } else {
            onSuccess?.let { it(r.response.value!!) }
        }
    }

    override fun onError(e: Throwable) {
        if (onErrorRunner != null) {
            onErrorRunner!!(ImageJavaError(e))
        } else {
            showError(e.toString())
        }
    }

    override fun onSubscribe(d: Disposable) {
    }

    override fun onComplete() {
        onFinally?.let { it() }
    }

    companion object {
        fun <T : ImageResponse> fromRequest(req: ImageRequest<T>) =  ImageResponseHandler<T>()
    }
}
