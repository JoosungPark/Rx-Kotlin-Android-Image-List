package sdop.image.list.controller.image

import io.reactivex.Observable
import sdop.image.list.BaseView
import sdop.image.list.BaseViewModel
import sdop.image.list.ErrorHandlerPresentable
import sdop.image.list.common.FragmentBundle
import sdop.image.list.rx.Variable

/**
 *
 * Created by jei.park on 2018. 1. 5..
 */
interface ImagePagerContract {
    interface View : BaseView<ViewModel>, ErrorHandlerPresentable

    interface ViewModel : BaseViewModel {
        val dataSource: Observable<List<FragmentBundle>>
        val viewCreated: Variable<Boolean>
        val expectedPosition: Variable<Int>
        val isLoading: Variable<Boolean>
        val isDragging: Variable<Boolean>

        fun onViewCreated()
        fun getMoreImage()
        fun close(current: Int)
    }
}