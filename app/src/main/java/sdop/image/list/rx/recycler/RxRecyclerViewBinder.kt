package sdop.image.list.rx.recycler

import android.support.v7.widget.*
import com.minimize.android.rxrecycleradapter.OnGetItemViewType
import com.minimize.android.rxrecycleradapter.RxDataSource
import com.minimize.android.rxrecycleradapter.TypesViewHolder
import com.minimize.android.rxrecycleradapter.ViewHolderInfo
import sdop.image.list.App
import sdop.image.list.util.LogUtil
import sdop.image.list.rx.DisposeBag
import sdop.image.list.rx.addTo

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class RxRecyclerViewBinder private constructor(private val recyclerView: RecyclerView,
                                               private val disposeBag: DisposeBag,
                                               private val layoutManager: RecyclerView.LayoutManager) {
    class CellItem(val viewHolder: TypesViewHolder<RxRecyclerCell>) {
        val cell = viewHolder.item
        val binding = viewHolder.viewDataBinding!!

        fun setFullSpanInStaggeredGridLayout() = (viewHolder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.apply { isFullSpan = true }
    }

    private val disposeBagPerViewBinding = hashMapOf<Int, DisposeBag>()

    private val cellList = mutableListOf<RxRecyclerCellStyle>()
    private var cache = mutableListOf<RecyclerView>()

    private val sourceList = mutableListOf<RxRecyclerCell>()

    private var latestListCell: RxRecyclerCellStyle? = null
    private var gridColumnSize = 0

    private lateinit var dataSource: RxDataSource<RxRecyclerCell>

    fun map(cell: RxRecyclerCellStyle): RxRecyclerViewBinder {
        cellList.add(cell)
        latestListCell = cell
        return this
    }

    private fun clear() {
        cache.map { it.clearOnScrollListeners() }
        cache.clear()
    }

    fun rx(alwaysReload: Boolean = false, alwaysScroll: Boolean = false): (List<RxRecyclerCell>) -> Unit {
        recyclerView.layoutManager = layoutManager
        val animator = recyclerView.itemAnimator
        (animator as? SimpleItemAnimator)?.apply { supportsChangeAnimations = false }

        when (layoutManager) {
            is GridLayoutManager -> {
                layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        val cell = sourceList[position]
                        return if (cell.spanSize <= 0) gridColumnSize else cell.spanSize
                    }
                }
            }
            is StaggeredGridLayoutManager -> {
                recyclerView.itemAnimator = null
            }
        }

        dataSource = RxDataSource<RxRecyclerCell>(sourceList)
        val getItemViewType = object : OnGetItemViewType() {
            override fun getItemViewType(position: Int): Int = sourceList[position].layoutResId
        }

        val vi = mutableListOf<ViewHolderInfo>()
        cellList.map { vi.add(ViewHolderInfo(it.layoutResId, it.layoutResId)) }

        dataSource.bindRecyclerView(recyclerView, vi, getItemViewType)
                .doOnUnsubscribe { clear() }
                .subscribe {
                    try {
                        val viewBinding = it.viewDataBinding
                        var disposeBagForViewBinding = disposeBagPerViewBinding.get(viewBinding.hashCode())
                        if (disposeBagForViewBinding == null) {
                            disposeBagPerViewBinding[viewBinding.hashCode()] = DisposeBag()
                            disposeBagForViewBinding = disposeBagPerViewBinding[viewBinding.hashCode()]!!
                            disposeBag.add(disposeBagPerViewBinding[viewBinding.hashCode()]!!)
                        } else {
                            disposeBagForViewBinding.dispose()
                        }

                        val item = CellItem(it)
                        item.cell.bindItem(item, disposeBagForViewBinding)
                    } catch (ex: Exception) {
                        LogUtil.e("Error occurred while in RxRecyclerViewBinder subscription.")
                        ex.printStackTrace()
                        assert(false)
                    }
                }
                .addTo(disposeBag)

        return fun(list: List<RxRecyclerCell>) {
            if (sourceList.isEmpty() || alwaysReload) {
                sourceList.clear()
                sourceList.addAll(list)
                dataSource.rxAdapterForTypes?.notifyDataSetChanged()
            } else {
                val adapter = dataSource.rxAdapterForTypes!!

                for (i in 0..(list.count()-1)) {
                    val cell = list[i]
                    val indexOld = sourceList.indexOf(cell)

                    if (indexOld == -1) {
                        sourceList.add(i, cell)
                        adapter.notifyItemInserted(i)
                        if (alwaysScroll) {
                            recyclerView.scrollToPosition(list.count() - 1)
                        }
                    } else if ( indexOld != i ) {
                        if (i>=sourceList.size-1) {
                            sourceList.add(i, cell)
                            adapter.notifyItemInserted(i)
                        } else {
                            sourceList.removeAt(indexOld)
                            sourceList.add(i, cell)
                            adapter.notifyItemMoved(indexOld, i)
                        }
                    } else {
                        if (alwaysScroll) {
                            adapter.notifyItemChanged(i)
                        }
                    }
                }

                sourceList.subtract(list)
                        .map { sourceList.indexOf(it) }
                        .sorted()
                        .reversed()
                        .forEach {
                            sourceList.removeAt(it)
                            adapter.notifyItemRemoved(it)
                        }
            }
        }
    }

    companion object {
        fun createLinearLayout(recyclerView: RecyclerView, disposeBag: DisposeBag): RxRecyclerViewBinder =
                RxRecyclerViewBinder(recyclerView, disposeBag, LinearLayoutManager(App.app))

        fun createHorizontalLinearLayout(recyclerView: RecyclerView, disposeBag: DisposeBag, reverse: Boolean = false): RxRecyclerViewBinder =
                RxRecyclerViewBinder(recyclerView, disposeBag, LinearLayoutManager(App.app, LinearLayoutManager.HORIZONTAL, reverse))

        fun createGridLayout(recyclerView: RecyclerView, disposeBag: DisposeBag, column: Int): RxRecyclerViewBinder {
            val b = RxRecyclerViewBinder(recyclerView, disposeBag, GridLayoutManager(App.app, column))
            b.gridColumnSize = column
            return b
        }

        fun createStaggeredGridLayout(recyclerView: RecyclerView, disposeBag: DisposeBag, column: Int): RxRecyclerViewBinder {
            val manager = StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.VERTICAL)
            manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            val b = RxRecyclerViewBinder(recyclerView, disposeBag, manager)
            b.gridColumnSize = column
            return b
        }
    }

}