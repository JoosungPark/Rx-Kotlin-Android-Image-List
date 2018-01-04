package sdop.image.list.controller.home

import io.reactivex.Observable
import sdop.image.list.BaseView
import sdop.image.list.BaseViewModel
import sdop.image.list.model.ImageId
import sdop.image.list.rx.Variable
import sdop.image.list.rx.recycler.RxRecyclerCell

/**
 *
 * Created by jei.park on 2018. 1. 4..
 */
interface HomeContract {
    interface View : BaseView<ViewModel> {
        fun reload()
    }

    interface ViewModel : BaseViewModel {
        val dataSource: Observable<List<RxRecyclerCell>>
        val isLoading: Variable<Boolean>

        fun getMoreImage()
        fun getImages(keyword: String)
        fun tapImage(imageId: ImageId)
    }
}