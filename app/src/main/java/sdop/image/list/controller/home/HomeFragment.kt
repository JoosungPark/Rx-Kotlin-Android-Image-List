package sdop.image.list.controller.home

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_home.*
import sdop.image.list.common.EndlessRecyclerViewScrollListener
import sdop.image.list.common.StaggeredGridGalleryItemDecoration
import sdop.image.list.controller.home.cell.FinderCell
import sdop.image.list.controller.home.cell.ImageCell
import sdop.image.list.data.SearchImageServer
import sdop.image.list.databinding.FragmentHomeBinding
import sdop.image.list.rx.*
import sdop.image.list.rx.recycler.*

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class HomeFragment : RxRecyclerFragment(), HomeContract.View {

    private val viewModel = HomeViewModel(this, SearchImageServer(server))

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener

    override fun sourceObservable(): Observable<List<RxRecyclerCell>> = viewModel.dataSource

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

            viewModel.isLoading.asObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(RxView.visibility(progressBar))
                    .addTo(disposeBag)
        }
    }

    private fun initScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(RxRecyclerViewBinder.latestLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                viewModel.getMoreImage()
            }
        }
    }

    override fun reload() {
        scrollListener.resetState()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}

