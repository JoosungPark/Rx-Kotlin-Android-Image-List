package sdop.image.list.controller.cell.home

import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import sdop.image.list.R
import sdop.image.list.databinding.ItemFinderBinding
import sdop.image.list.model.ImageUIEvent
import sdop.image.list.model.UIEventPublisher
import sdop.image.list.rx.DisposeBag
import sdop.image.list.rx.addTo
import sdop.image.list.rx.debug
import sdop.image.list.rx.recycler.RxRecyclerCell
import sdop.image.list.rx.recycler.RxRecyclerCellStyle
import sdop.image.list.rx.recycler.RxRecyclerViewBinder
import sdop.image.list.util.Notifier

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
data class FinderCell(private val emitter: UIEventPublisher? = null) : RxRecyclerCell(layoutResId, "finder") {
    data class UISearchEvent(val keyword: String) : ImageUIEvent

    override fun bindItem(item: RxRecyclerViewBinder.CellItem, disposeBag: DisposeBag) {
        val binder = item.binding as ItemFinderBinding

        val searchStream = Observable.merge(RxView.clicks(binder.imageSearch), RxTextView.editorActionEvents(binder.imageFinder))
                .map { binder.imageFinder.text.toString() }
                .share()

        searchStream
                .filter { it.isEmpty() }
                .subscribe { Notifier.toast(R.string.No_Keyword) }
                .addTo(disposeBag)

        searchStream
                .filter { it.isNotEmpty() }
                .debug("jei emit search event")
                .subscribe {
                    emitter?.takeIf { !it.hasComplete() }?.onNext(UISearchEvent(it))
                }
                .addTo(disposeBag)

        item.setFullSpanInStaggeredGridLayout()
    }

    companion object {
        val layoutResId = R.layout.item_finder

        fun style() = RxRecyclerCellStyle(layoutResId)
    }
}
