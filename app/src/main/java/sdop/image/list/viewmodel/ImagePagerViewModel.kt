package sdop.image.list.viewmodel

import android.app.Application
import io.reactivex.Observable
import sdop.image.list.common.FragmentBundle
import sdop.image.list.common.SingleLiveEvent
import sdop.image.list.data.SearchImageRepo
import sdop.image.list.model.ImageModel
import sdop.image.list.rx.Variable

class ImagePagerViewModel(
        context: Application,
        index: Int,
        private var searchImageModel: Variable<List<ImageModel>>,
        private val repo: SearchImageRepo
) : AppViewModel(context) {
    val dataSource: Observable<List<FragmentBundle>>
    val viewCreated = Variable(false)
    val expectedPosition = Variable(index)
    val isLoading = Variable(false)
    val isDragging = Variable(false)
    private val hasMoreModel = Variable(false)

    val errorEventStream = SingleLiveEvent<Throwable>()
    val closeStream = SingleLiveEvent<Int>()

    init {
        dataSource = searchImageModel
                .asObservable()
                .map { it.map { FragmentBundle.Image(it) } }
    }

    fun onViewCreated() {
        viewCreated.value = true
    }

    fun getMoreImage() {
        if (!isLoading.value) {
            isLoading.value = true
            repo.getMoreImages(searchImageModel.value.size).subscribe({
                hasMoreModel.value = it.hasMore
                if (it.images.isNotEmpty()) expectedPosition.value = searchImageModel.value.size
                searchImageModel.value = searchImageModel.value + it.images
            }, { errorEventStream.value = it }, { isLoading.value = false })
        }
    }

    fun close(current: Int) {
        closeStream.value = current
    }

    companion object {
        const val kLatestIndex = "kLatestIndex"
        const val codeLatestIndex = 1004
    }
}