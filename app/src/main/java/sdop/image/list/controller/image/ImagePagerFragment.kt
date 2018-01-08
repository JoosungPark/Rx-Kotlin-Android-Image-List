package sdop.image.list.controller.image

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import sdop.image.list.common.BaseFragment
import sdop.image.list.data.SearchImageRepo
import sdop.image.list.databinding.FragmentImagePagerBinding
import sdop.image.list.model.ImageModel
import sdop.image.list.rx.RxUtils
import sdop.image.list.rx.Variable
import sdop.image.list.rx.addTo
import sdop.image.list.util.Notifier

/**
 *
 * Created by jei.park on 2018. 1. 5..
 */
class ImagePagerFragment : BaseFragment(), ImagePagerContract.View {
    private lateinit var binding: FragmentImagePagerBinding
    private lateinit var viewModel: ImagePagerViewModel

    override fun onError(error: Throwable) {
        Notifier.toast(error.localizedMessage)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        inflater?.let { binding = FragmentImagePagerBinding.inflate(it, container, false) }
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ImagePagerAdapter(childFragmentManager)
        binding.viewPager.adapter = adapter

        viewModel.dataSource
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext { binding.viewPager.setCurrentItem(viewModel.expectedPosition.value, false) }
                .subscribe(adapter.rx())
                .addTo(disposeBag)

        initialize(adapter)

        viewModel.onViewCreated()
    }

    private fun initialize(adapter: ImagePagerAdapter) {
        RxUtils.combineLatest(viewModel.viewCreated.asObservable(), viewModel.expectedPosition.asObservable()) { c, p -> Pair(c, p) }
                .filter { it.first }
                .map { it.second }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { binding.viewPager.currentItem = it }
                .addTo(disposeBag)

        var currentIndex = viewModel.expectedPosition.value
        var isDraggingAtaLast = false

        RxViewPager.pageSelections(binding.viewPager)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { currentIndex = it }
                .addTo(disposeBag)

        RxViewPager.pageScrollStateChanges(binding.viewPager)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val lastIndex = adapter.count - 1
                    viewModel.isDragging.value = it == ViewPager.SCROLL_STATE_DRAGGING

                    if (currentIndex == lastIndex && it == ViewPager.SCROLL_STATE_DRAGGING) {
                        isDraggingAtaLast = true
                    } else if (isDraggingAtaLast && it == ViewPager.SCROLL_STATE_IDLE) {
                        viewModel.getMoreImage()
                    } else {
                        isDraggingAtaLast = false
                    }
                }
                .addTo(disposeBag)

        binding.close.bringToFront()
        RxView.clicks(binding.close)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel.close(currentIndex) }
                .addTo(disposeBag)

        viewModel.isDragging.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { binding.close.visibility = if (it) View.GONE else View.VISIBLE }
                .addTo(disposeBag)

        viewModel.isLoading.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(RxView.visibility(binding.progressBar))
                .addTo(disposeBag)
    }

    companion object {
        fun newInstance(current: ImageModel, searchImageModel: Variable<List<ImageModel>>, repo: SearchImageRepo): ImagePagerFragment {
            val fragment = ImagePagerFragment()
            fragment.viewModel = ImagePagerViewModel(fragment, current, searchImageModel, repo)
            return fragment
        }
    }
}