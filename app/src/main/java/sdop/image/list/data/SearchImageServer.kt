package sdop.image.list.data

import io.reactivex.Observable
import sdop.image.list.model.SearchImageModel
import sdop.image.list.model.SearchImageModelPresentable
import sdop.image.list.model.ImageModel
import sdop.image.list.http.ImageServer
import sdop.image.list.http.model.server.SearchImageRequest
import sdop.image.list.rx.RxUtils
import sdop.image.list.rx.debug
import java.util.concurrent.TimeUnit

/**
 *
 * Created by jei.park on 2018. 1. 4..
 */
class SearchImageServer(private val server: ImageServer) : SearchImageRepo {
    private var recentKeyword = ""

    override fun getImages(keyword: String): Observable<SearchImageModelPresentable> {
        val timerStream = Observable.timer(1, TimeUnit.SECONDS)
        recentKeyword = keyword
        val request = SearchImageRequest(keyword, "0")

        return RxUtils.combineLatest(timerStream, internalGetImages(request)) { _, r -> r }
    }

    override fun getMoreImages(start: Int): Observable<SearchImageModelPresentable> {
        val timerStream = Observable.timer(1, TimeUnit.SECONDS)
        val request = SearchImageRequest(recentKeyword, start.toString())

        return RxUtils.combineLatest(timerStream, internalGetImages(request)) { _, r -> r }
    }

    private fun internalGetImages(request: SearchImageRequest): Observable<SearchImageModelPresentable> {
        return server.request(request).map {
            it.response.value?.let {
                val hasMore = if (it.total != null && it.start != null && it.display != null) it.total < it.start + it.display else false
                val items: List<ImageModel> = it.items?.map { ImageModel(it.link, it.thumbnail, it.sizewidth, it.sizeheight) } ?: listOf()
                SearchImageModel(hasMore, items)
            }
        }

    }
}