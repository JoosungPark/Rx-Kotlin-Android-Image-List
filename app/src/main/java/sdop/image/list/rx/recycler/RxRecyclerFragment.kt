package sdop.image.list.rx.recycler

import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import sdop.image.list.common.BaseFragment
import sdop.image.list.rx.addTo
import sdop.image.list.util.LogUtil

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */

abstract class RxRecyclerFragment(open val alwaysScroll: Boolean = false, open val alwaysReload: Boolean = false) : BaseFragment() {
    var binding: ViewDataBinding? = null
    var adapter: RxRecyclerViewBinder? = null

    abstract fun sourceObservable(): Observable<List<RxRecyclerCell>>

    abstract fun cellStyles(): List<RxRecyclerCellStyle>

    abstract fun adapter(): RxRecyclerViewBinder

    protected open fun scrollToIfNeed() { }

    protected open fun bindFragment() { }

    private fun bindCell() {
        adapter = adapter()

        cellStyles().map { adapter!!.map(it) }

        sourceObservable()
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext { scrollToIfNeed() }
                .subscribe(adapter!!.rx(alwaysReload, alwaysScroll))
                .addTo(disposeBag)
    }

    protected open fun runOnce() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindFragment()
        bindCell()
        runOnce()
    }
}