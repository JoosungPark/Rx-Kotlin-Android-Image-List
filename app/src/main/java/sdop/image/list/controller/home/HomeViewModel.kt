package sdop.image.list.controller.home

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import sdop.image.list.controller.home.cell.FinderCell
import sdop.image.list.controller.home.cell.ImageCell
import sdop.image.list.data.SearchImageRepo
import sdop.image.list.model.ImageId
import sdop.image.list.rx.Variable
import sdop.image.list.rx.recycler.RxRecyclerCell
import sdop.image.list.util.Notifier

/**
 *
 * Created by jei.park on 2018. 1. 4..
 */
data class HomeViewModel(private val view: HomeContract.View, private val repo: SearchImageRepo) : HomeContract.ViewModel {
    private var isLoaded = false
    private val hasMoreModel = Variable(false)
    private val homeImageModel = Variable<List<ImageModel>>(mutableListOf())
    override val isLoading = Variable(false)

    override val dataSource: Observable<List<RxRecyclerCell>> get() {
        return homeImageModel.asObservable().distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    val list = mutableListOf<RxRecyclerCell>()
                    list.add(FinderCell(this))
                    list.addAll(it.map { ImageCell(it, this) })

                    list
                }
    }

    override fun getMoreImage() {
        if (!isLoading.value && isLoaded) {
            isLoading.value = true
            repo.getMoreImages(homeImageModel.value.size).subscribe({
                hasMoreModel.value = it.hasMore
                homeImageModel.value = homeImageModel.value + it.images
            }, { Notifier.toast(it.localizedMessage) }, { isLoading.value = false })
        }
    }

    override fun getImages(keyword: String) {
        isLoading.value = true
        view.reload()
        homeImageModel.value = mutableListOf()

        repo.getImages(keyword).subscribe({
            homeImageModel.value = it.images
            hasMoreModel.value = it.hasMore
            isLoaded = true
        }, { Notifier.toast(it.localizedMessage) }, { isLoading.value = false })
    }

    override fun tapImage(imageId: ImageId) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
