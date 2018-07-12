package sdop.image.list.controller.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_home.*
import sdop.image.list.ErrorHandler
import sdop.image.list.common.*
import sdop.image.list.data.SearchImageServer
import sdop.image.list.databinding.FragmentHomeBinding
import sdop.image.list.model.ImageModel
import sdop.image.list.rx.recycler.*
import sdop.image.list.util.LogUtil
import sdop.image.list.viewmodel.HomeViewModel
import sdop.image.list.viewmodel.ImagePagerViewModel
import java.util.concurrent.TimeUnit

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class HomeFragment : RxRecyclerFragment(), ErrorHandler {

    private val viewModel: HomeViewModel by lazy {
        HomeViewModel(App.app, SearchImageServer(server))
    }

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private var dataBinding: FragmentHomeBinding? = null

    override fun sourceObservable(): Observable<List<RxRecyclerCell>> = viewModel.dataSource

    override fun cellStyles(): List<RxRecyclerCellStyle> = viewModel.cellStyles()

    override fun adapter(): RxRecyclerViewBinder = RxRecyclerViewBinder.createStaggeredGridLayout(recycler_view, disposeBag, 2)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = inflater.let { FragmentHomeBinding.inflate(it, container, false) }
        (binding as? FragmentHomeBinding)?.let { dataBinding = it }

        withViewModel({ viewModel }) {
            dataBinding?.viewModel = this

            observe(errorEventStream) { handleError(it) }
            observe(reloadStream) { scrollListener.resetState() }
            observe(tapImageStream) { it?.let { openImage(it) } }
        }

        viewModel.isLoading.asObservable()
                .subscribe()
                .addTo(viewModel.disposeBag)

        return binding?.root
    }

    override fun scrollToIfNeed() {
        super.scrollToIfNeed()

        dataBinding?.let { binding ->
            viewModel.scrollToIndex?.let {
                Observable.just(it)
                        .delay(100, TimeUnit.MILLISECONDS)
                        .take(1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { binding.recyclerView.layoutManager.scrollToPosition(it) }
                        .addTo(viewModel.disposeBag)

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding?.let { binding ->
            binding.recyclerView.addItemDecoration(StaggeredGridGalleryItemDecoration(view.context))

            // 스크롤 리스너에서 사용하는 layoutmanager는 rxrecyclerview가 생성된 후 사용 가능하므로
            // scrollListener의 초기화는 여기에서 한다
            initScrollListener()
            binding.recyclerView.addOnScrollListener(scrollListener)
        }
    }

    private fun initScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(RxRecyclerViewBinder.latestLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                viewModel.getMoreImage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ImagePagerViewModel.codeLatestIndex && resultCode == Activity.RESULT_OK) {
            val latestIndex = data?.getIntExtra(ImagePagerViewModel.kLatestIndex, 0) ?: return
            viewModel.scrollTo(latestIndex)
            scrollToIfNeed()
        }
    }

    private fun openImage(image: ImageModel) {
        val fragment = FragmentFactory.createFragment(FragmentBundle.ImagePager(image, viewModel.images, viewModel.repo))
        pushFragment(fragment)
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}