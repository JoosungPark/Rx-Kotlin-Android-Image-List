package sdop.image.list.data

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import sdop.image.list.controller.home.HomeImageModel
import sdop.image.list.controller.home.HomeImageModelPresentable
import sdop.image.list.controller.home.ImageModel
import sdop.image.list.http.ImageServer
import sdop.image.list.http.model.server.SearchImageRequest
import sdop.image.list.http.model.server.SearchImageResponse
import sdop.image.list.rx.ImageResponseHandler
import sdop.image.list.rx.RxUtils
import sdop.image.list.util.Notifier
import java.util.concurrent.TimeUnit

/**
 *
 * Created by jei.park on 2018. 1. 4..
 */
class SearchImageServer(private val server: ImageServer) : SearchImageRepo {
    private var recentKeyword = ""

    override fun getImages(keyword: String): Observable<HomeImageModelPresentable> {
        val timerStream = Observable.timer(1, TimeUnit.SECONDS)
        val serverStream: Observable<HomeImageModelPresentable> = Observable.create { emitter ->
            recentKeyword = keyword
            val request = SearchImageRequest(keyword, "0")
            internalGetImages(request, emitter)
        }

        return RxUtils.combineLatest(timerStream, serverStream) { _, r -> r }
    }

    override fun getMoreImages(start: Int): Observable<HomeImageModelPresentable> {
        val timerStream = Observable.timer(1, TimeUnit.SECONDS)
        val serverStream: Observable<HomeImageModelPresentable> = Observable.create { emitter ->
            val request = SearchImageRequest(recentKeyword, start.toString())
            internalGetImages(request, emitter)
        }

        return RxUtils.combineLatest(timerStream, serverStream) { _, r -> r }
    }

    private fun internalGetImages(request: SearchImageRequest, emitter: ObservableEmitter<HomeImageModelPresentable>) {
        val handler = ImageResponseHandler.fromRequest(request)
                .runOnSuccess {
                    (it as? SearchImageResponse)?.let {
                        val hasMore = if (it.total != null && it.start != null && it.display != null) it.total < it.start + it.display else false
                        val items: List<ImageModel> = it.items?.map { ImageModel(it.link, it.thumbnail, it.sizewidth, it.sizeheight) } ?: listOf()
                        val result = HomeImageModel(hasMore, items)

                        emitter.onNext(result)
                        emitter.onComplete()
                    }
                }
                .runOnError {
                    Notifier.toast(it.localizedMessage)
                    emitter.onError(it)
                }

        server.request(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler)
    }
}