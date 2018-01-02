package sdop.image.list.controller

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_home.*
import sdop.image.list.common.EndlessRecyclerViewScrollListener
import sdop.image.list.common.StaggeredGridGalleryItemDecoration
import sdop.image.list.controller.cell.home.FinderCell
import sdop.image.list.controller.cell.home.ImageCell
import sdop.image.list.controller.model.HomeImageModel
import sdop.image.list.controller.model.HomeImageModelPresentable
import sdop.image.list.controller.model.ImageModel
import sdop.image.list.databinding.FragmentHomeBinding
import sdop.image.list.http.ImageServer
import sdop.image.list.http.model.server.SearchImageRequest
import sdop.image.list.http.model.server.SearchImageResponse
import sdop.image.list.model.ImageUIEvent
import sdop.image.list.model.UIEventPublisher
import sdop.image.list.rx.*
import sdop.image.list.rx.recycler.*
import sdop.image.list.util.LogUtil
import sdop.image.list.util.Notifier
import java.util.concurrent.TimeUnit

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class HomeFragment : RxRecyclerFragment() {
    data class UIMoreImageEvent(val start: Int) : ImageUIEvent

    private val event: UIEventPublisher = UIEventPublisher.create()
    private val hasMoreModel = Variable(false)
    private val homeImageModel = Variable<List<ImageModel>>(mutableListOf())
    private val isLoading = Variable(false)
    private val searchImageServer: SearchImageServerType = SearchImageServer(server)

    private var isLoaded = false

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener

    override fun sourceObservable(): Observable<List<RxRecyclerCell>> =
            homeImageModel.asObservable().distinctUntilChanged()
                    .observeOn(AndroidSchedulers.mainThread())
                    .map {
                        val list = mutableListOf<RxRecyclerCell>()
                        list.add(FinderCell(event))
                        list.addAll(it.map { ImageCell(this, event, it) })

                        list
                    }

    override fun cellStyles(): List<RxRecyclerCellStyle> = listOf(FinderCell.style(), ImageCell.style())

    override fun adapter(): RxRecyclerViewBinder = RxRecyclerViewBinder.createStaggeredGridLayout(recycler_view, disposeBag, 2)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = inflater?.let { FragmentHomeBinding.inflate(it, container, false) }
        return binding?.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (binding as? FragmentHomeBinding)?.apply {
            recyclerView.addItemDecoration(StaggeredGridGalleryItemDecoration(context))

            // 스크롤 리스너에서 사용하는 layoutmanager는 rxrecyclerview가 생성된 후 사용 가능하므로
            // scrollListener의 초기화는 여기에서 한다
            initScrollListener()
            recyclerView.addOnScrollListener(scrollListener)

            isLoading.asObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(RxView.visibility(progressBar))
                    .addTo(disposeBag)


            event.filter { !isLoading.value }.subscribe {
                isLoading.value = true
                when (it) {
                    is FinderCell.UISearchEvent -> {
                        scrollListener.resetState()
                        homeImageModel.value = mutableListOf()

                        searchImageServer.getImages(it.keyword).subscribe({
                            homeImageModel.value = it.images
                            hasMoreModel.value = it.hasMore
                            isLoaded = true
                        }, { Notifier.toast(it.localizedMessage) }, { isLoading.value = false })
                    }

                    is UIMoreImageEvent ->
                        searchImageServer.getMoreImages(it.start).subscribe({
                            hasMoreModel.value = it.hasMore
                            homeImageModel.value = homeImageModel.value + it.images
                        }, { Notifier.toast(it.localizedMessage) }, { isLoading.value = false })

                    is ImageCell.UITabImageEvent -> {
                        // open photo
                        isLoading.value = false
                    }
                }
            }.addTo(disposeBag)
        }
    }

    private fun initScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(RxRecyclerViewBinder.latestLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                LogUtil.d(DEBUG_TAG, "onLoadMore !!!")
                if (!isLoading.value && isLoaded) event.onNext(UIMoreImageEvent(homeImageModel.value.size))
            }
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}

interface SearchImageServerType {
    fun getImages(keyword: String): Observable<HomeImageModelPresentable>
    fun getMoreImages(start: Int): Observable<HomeImageModelPresentable>
}

class SearchImageServer(private val server: ImageServer) : SearchImageServerType {
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