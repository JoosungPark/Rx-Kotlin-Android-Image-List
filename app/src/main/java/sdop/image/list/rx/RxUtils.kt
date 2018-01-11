package sdop.image.list.rx

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Looper
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
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
object RxUtils {
    fun <T1, T2, R> combineLatest(source1: ObservableSource<out T1>,
                                  source2: ObservableSource<out T2>,
                                  resultSelector: (T1, T2) -> R): Observable<R>
            = Observable.combineLatest<T1, T2, R>(source1, source2, BiFunction { t1, t2 -> resultSelector(t1, t2) })
}

fun <T> Observable<Nullable<T>>.filterNotNull(): Observable<T> {
    @Suppress("UNCHECKED_CAST")
    return this.filter { it.value != null }.map { it.value!! }
}

/**
 * An alias to [Observable.withLatestFrom], but allowing for cleaner lambda syntax.
 */
inline fun <T, U, R> Observable<T>.withLatestFrom(other: ObservableSource<U>, crossinline combiner: (T, U) -> R): Observable<R>
        = withLatestFrom(other, BiFunction<T, U, R> { t, u -> combiner.invoke(t, u)  })