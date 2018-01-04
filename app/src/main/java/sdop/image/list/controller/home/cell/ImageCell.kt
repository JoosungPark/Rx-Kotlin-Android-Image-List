package sdop.image.list.controller.home.cell

import com.jakewharton.rxbinding2.view.RxView
import sdop.image.list.R
import sdop.image.list.controller.home.HomeViewModel
import sdop.image.list.controller.home.ImageModel
import sdop.image.list.databinding.ItemImageBinding
import sdop.image.list.rx.DisposeBag
import sdop.image.list.rx.addTo
import sdop.image.list.rx.recycler.RxRecyclerCell
import sdop.image.list.rx.recycler.RxRecyclerCellStyle
import sdop.image.list.rx.recycler.RxRecyclerViewBinder

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
data class ImageCell(private val image: ImageModel, private val viewModel: HomeViewModel) : RxRecyclerCell(layoutResId, image.imageUrl.get()) {
    override val spanSize: Int = 1

    override fun bindItem(item: RxRecyclerViewBinder.CellItem, disposeBag: DisposeBag) {
        val binding = item.binding as ItemImageBinding
        binding.image = image
        RxView.clicks(binding.searchedImage)
                .filter { binding.image?.imageUrl?.get()?.isNotEmpty() ?: false }
                .subscribe { viewModel.tapImage(image.url) }
                .addTo(disposeBag)
    }

    companion object {
        val layoutResId = R.layout.item_image

        fun style() = RxRecyclerCellStyle(layoutResId)
    }
}