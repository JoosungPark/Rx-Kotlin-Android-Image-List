package sdop.image.list.viewmodel

import android.app.Application
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import sdop.image.list.common.AppDataSource
import sdop.image.list.common.SingleLiveEvent
import sdop.image.list.controller.home.cell.FinderCell
import sdop.image.list.controller.home.cell.ImageCell
import sdop.image.list.data.SearchImageRepo
import sdop.image.list.model.ImageModel
import sdop.image.list.rx.Variable
import sdop.image.list.rx.recycler.RxRecyclerCell
import sdop.image.list.rx.recycler.RxRecyclerCellStyle

class HomeViewModel(context: Application, val repo: SearchImageRepo) : AppViewModel(context), AppDataSource {

    private var isLoaded = false
    private var hasMore = false

    val images = Variable<List<ImageModel>>(mutableListOf())
    val isLoading = Variable(false)

    override val dataSource: Observable<List<RxRecyclerCell>>

    override fun cellStyles(): List<RxRecyclerCellStyle> = listOf(FinderCell.style(), ImageCell.style())

    var scrollToIndex: Int? = null
    val errorEventStream = SingleLiveEvent<Throwable>()
    val reloadStream = SingleLiveEvent<Void>()
    val tapImageStream = SingleLiveEvent<ImageModel>()

    init {
        dataSource = images.asObservable().distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    val list = mutableListOf<RxRecyclerCell>()
                    list.add(FinderCell(this))
                    list.addAll(it.map { ImageCell(it, this) })

                    list
                }
    }

    fun getMoreImage() {
        if (!isLoading.value && isLoaded) {
            scrollToIndex = null
            isLoading.value = true
            repo.getMoreImages(images.value.size).subscribe({
                hasMore = it.hasMore
                val list = mutableListOf<ImageModel>()
                list.addAll(images.value)
                list.addAll(it.images)
                images.value = list
            }, { errorEventStream.value = it }, { isLoading.value = false })
        }
    }

    fun getImages(keyword: String) {
        scrollToIndex = null
        isLoading.value = true
        reloadStream.call()
        images.value = mutableListOf()

        repo.getImages(keyword)
                .subscribe({
                    images.value = it.images
                    hasMore = it.hasMore
                    isLoaded = true
                }, { errorEventStream.value = it }, { isLoading.value = false })
    }

    fun tapImage(image: ImageModel) {
        tapImageStream.value = image
    }

    fun scrollTo(position: Int) {
        scrollToIndex = position
    }
}