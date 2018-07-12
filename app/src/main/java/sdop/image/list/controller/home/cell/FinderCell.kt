package sdop.image.list.controller.home.cell

import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import sdop.image.list.R
import sdop.image.list.databinding.ItemFinderBinding
import sdop.image.list.rx.DisposeBag
import sdop.image.list.rx.recycler.RxRecyclerCell
import sdop.image.list.rx.recycler.RxRecyclerCellStyle
import sdop.image.list.rx.recycler.RxRecyclerViewBinder
import sdop.image.list.util.KeyboardUtils
import sdop.image.list.util.Notifier
import sdop.image.list.viewmodel.HomeViewModel

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
data class FinderCell(private val viewModel: HomeViewModel) : RxRecyclerCell(layoutResId, "finder") {

    override fun bindItem(item: RxRecyclerViewBinder.CellItem, disposeBag: DisposeBag) {
        val binder = item.binding as ItemFinderBinding
        item.setFullSpanInStaggeredGridLayout()

        val searchStream = Observable.merge(RxView.clicks(binder.imageSearch), RxTextView.editorActionEvents(binder.imageFinder))
                .map { binder.imageFinder.text.toString() }
                .share()

        searchStream
                .filter { it.isEmpty() }
                .subscribe { Notifier.toast(R.string.No_Keyword) }
                .addTo(viewModel.disposeBag)

        searchStream
                .filter { it.isNotEmpty() }
                .subscribe {
                    KeyboardUtils.hideKeyboard(binder.imageFinder)
                    viewModel.getImages(it)
                }
                .addTo(viewModel.disposeBag)
    }

    companion object {
        const val layoutResId = R.layout.item_finder

        fun style() = RxRecyclerCellStyle(layoutResId)
    }
}
