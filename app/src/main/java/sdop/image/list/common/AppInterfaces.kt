package sdop.image.list.common

import io.reactivex.Observable
import sdop.image.list.rx.recycler.RxRecyclerCell
import sdop.image.list.rx.recycler.RxRecyclerCellStyle

interface AppDataSource {
    val dataSource: Observable<List<RxRecyclerCell>>
    fun cellStyles(): List<RxRecyclerCellStyle>
}