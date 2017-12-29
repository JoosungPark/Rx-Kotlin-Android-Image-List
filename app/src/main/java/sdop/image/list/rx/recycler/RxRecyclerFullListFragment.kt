package sdop.image.list.rx.recycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.full_list.*
import sdop.image.list.databinding.FullListBinding

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
abstract class RxRecyclerFullListFragment() : RxRecyclerFragment() {
    override fun adapter() = RxRecyclerViewBinder.createLinearLayout(recycler_view, disposeBag)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater?.let { FullListBinding.inflate(it, container, false) }?.root
    }
}