package sdop.image.list.controller.image

import android.content.Intent
import io.reactivex.Observable
import sdop.image.list.common.FragmentBundle
import sdop.image.list.controller.home.HomeViewModel
import sdop.image.list.data.SearchImageRepo
import sdop.image.list.model.ImageModel
import sdop.image.list.rx.MaybeVariable
import sdop.image.list.rx.Variable
import sdop.image.list.rx.addTo

/**
 *
 * Created by jei.park on 2018. 1. 5..
 */
data class ImagePagerViewModel(
        private val view: ImagePagerContract.View,
        private val current: ImageModel,
        private var searchImageModel: Variable<List<ImageModel>>,
        private val repo: SearchImageRepo)
    : ImagePagerContract.ViewModel {

    override val dataSource: Observable<List<FragmentBundle>> get() {
        return searchImageModel
                .asObservable()
                .map { it.map { FragmentBundle.Image(it) } }

    }

    override val viewCreated = Variable(false)
    override val expectedPosition = Variable(searchImageModel.value.indexOf(current))

    override val isLoading = Variable(false)
    override val isDragging = Variable(false)
    private val hasMoreModel = Variable(false)

    override fun onViewCreated() {
        viewCreated.value = true
    }

    override fun getMoreImage() {
        if (!isLoading.value) {
            isLoading.value = true
            repo.getMoreImages(searchImageModel.value.size).subscribe({
                hasMoreModel.value = it.hasMore
                if (it.images.size > 0) expectedPosition.value = searchImageModel.value.size
                searchImageModel.value = searchImageModel.value + it.images
            }, { view.onError(it) }, { isLoading.value = false })
        }
    }

    override fun close(current: Int) {
        view.baseFragment.setFragmentResult(Intent().putExtra(kLatestIndex, current))
        view.baseFragment.popFragment()
    }

    companion object {
        val kLatestIndex = "kLatestIndex"
        val codeLatestIndex = 1004
    }
}