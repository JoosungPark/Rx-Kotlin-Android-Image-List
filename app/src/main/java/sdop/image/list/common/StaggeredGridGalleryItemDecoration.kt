package sdop.image.list.common

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import sdop.image.list.R

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class StaggeredGridGalleryItemDecoration constructor(context: Context) : RecyclerView.ItemDecoration() {

    val pxLeftPadding: Int = context.resources.getDimensionPixelSize(R.dimen.Gallery_Left_Padding)
    val pxRightPadding: Int = context.resources.getDimensionPixelSize(R.dimen.Gallery_Right_Padding)
    val pxColumnSpacingHalf: Int = context.resources.getDimensionPixelSize(R.dimen.Gallery_Column_Spacing_Half)
    val pxRowSpacing: Int = context.resources.getDimensionPixelSize(R.dimen.Gallery_Row_Spacing)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        (view.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.let { param ->
            if (param.isFullSpan) {
                return
            }

            (parent.layoutManager as? StaggeredGridLayoutManager)?.let {
                outRect.bottom = pxRowSpacing
                when (param.spanIndex) {
                    0 -> {
                        outRect.left = pxLeftPadding
                        outRect.right = pxColumnSpacingHalf
                    }
                    (it.spanCount - 1) -> {
                        outRect.left = pxColumnSpacingHalf
                        outRect.right = pxRightPadding
                    }
                    else -> {
                        outRect.left = pxColumnSpacingHalf
                        outRect.right = pxColumnSpacingHalf
                    }
                }
            }
        }
    }
}