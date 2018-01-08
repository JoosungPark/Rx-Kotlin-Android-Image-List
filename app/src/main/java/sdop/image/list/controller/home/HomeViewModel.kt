package sdop.image.list.controller.home

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import sdop.image.list.common.FragmentBundle
import sdop.image.list.common.FragmentFactory
import sdop.image.list.controller.home.cell.FinderCell
import sdop.image.list.controller.home.cell.ImageCell
import sdop.image.list.controller.image.ImagePagerViewModel
import sdop.image.list.data.SearchImageRepo
import sdop.image.list.model.ImageId
import sdop.image.list.model.ImageModel
import sdop.image.list.rx.MaybeVariable
import sdop.image.list.rx.Variable
import sdop.image.list.rx.recycler.RxRecyclerCell
import sdop.image.list.util.LogUtil
import sdop.image.list.util.Notifier

/**
 *
 * Created by jei.park on 2018. 1. 4..
 */
data class HomeViewModel(private val view: HomeContract.View, private val repo: SearchImageRepo) : HomeContract.ViewModel {
    private var isLoaded = false
    private val hasMoreModel = Variable(false)
    private val searchImageModel = Variable<List<ImageModel>>(mutableListOf())
    override val isLoading = Variable(false)

    override val dataSource: Observable<List<RxRecyclerCell>>
        get() {
            return searchImageModel.asObservable().distinctUntilChanged()
                    .observeOn(AndroidSchedulers.mainThread())
                    .map {
                        val list = mutableListOf<RxRecyclerCell>()
                        list.add(FinderCell(this))
                        list.addAll(it.map { ImageCell(it, this) })

                        list
                    }
        }

    override var scrollToIndex: Int? = null

    override fun getMoreImage() {
        if (!isLoading.value && isLoaded) {
            scrollToIndex = null
            isLoading.value = true
            repo.getMoreImages(searchImageModel.value.size).subscribe({
                hasMoreModel.value = it.hasMore
                searchImageModel.value = searchImageModel.value + it.images
            }, { view.onError(it) }, { isLoading.value = false })
        }
    }

    override fun getImages(keyword: String) {
        scrollToIndex = null
        isLoading.value = true
        view.reload()
        searchImageModel.value = mutableListOf()

        repo.getImages(keyword).subscribe({
            searchImageModel.value = it.images
            hasMoreModel.value = it.hasMore
            isLoaded = true
        }, { view.onError(it) }, { isLoading.value = false })
    }

    override fun tapImage(image: ImageModel) {
        val fragment = FragmentFactory.createFragment(FragmentBundle.ImagePager(image, searchImageModel, repo))
        fragment.setTargetFragment(view.baseFragment, ImagePagerViewModel.codeLatestIndex)
        view.baseActivity.loadFragment(fragment, true)
    }

    override fun scrollTo(position: Int) {
        scrollToIndex = position
    }
}
