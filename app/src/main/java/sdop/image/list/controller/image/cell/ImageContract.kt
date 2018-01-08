package sdop.image.list.controller.image.cell

import sdop.image.list.BaseView
import sdop.image.list.BaseViewModel
import sdop.image.list.model.ImageModel

/**
 *
 * Created by jei.park on 2018. 1. 5..
 */
interface ImageContract {
    interface View : BaseView<ViewModel> { }

    interface ViewModel : BaseViewModel {
        val model: ImageModel
    }
}