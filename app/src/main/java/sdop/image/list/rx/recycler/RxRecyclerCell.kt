package sdop.image.list.rx.recycler

import android.support.annotation.LayoutRes
import sdop.image.list.model.ImageId
import sdop.image.list.rx.DisposeBag
import java.io.Serializable

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
data class RxRecyclerCellStyle(@LayoutRes val layoutResId: Int)

abstract class RxRecyclerCell(@LayoutRes open val layoutResId: Int, val id: ImageId, open val viewCategory: Int = 0) : Serializable {
    protected val DEBUG_TAG = this.javaClass.simpleName

    abstract fun bindItem(item: RxRecyclerViewBinder.CellItem, disposeBag: DisposeBag)

    // 0 is full span
    open val spanSize: Int = 0

    override fun toString(): String = "(${this.javaClass.name} $layoutResId, ${id}, $viewCategory)"

    override fun hashCode(): Int = toString().hashCode()

    override fun equals(other: Any?): Boolean = other?.hashCode() == hashCode()
}