package sdop.image.list.controller.cell.home

import com.jakewharton.rxbinding2.view.RxView
import sdop.image.list.BaseFragment
import sdop.image.list.R
import sdop.image.list.controller.model.ImageModel
import sdop.image.list.databinding.ItemImageBinding
import sdop.image.list.model.ImageId
import sdop.image.list.model.ImageUIEvent
import sdop.image.list.model.UIEventPublisher
import sdop.image.list.rx.DisposeBag
import sdop.image.list.rx.addTo
import sdop.image.list.rx.recycler.RxRecyclerCell
import sdop.image.list.rx.recycler.RxRecyclerCellStyle
import sdop.image.list.rx.recycler.RxRecyclerViewBinder

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
data class ImageCell(private val fragment: BaseFragment, private val emitter: UIEventPublisher? = null, private val image: ImageModel) : RxRecyclerCell(layoutResId, image.imageUrl.get()) {
    data class UITabImageEvent(val image: ImageId) : ImageUIEvent

    override fun bindItem(item: RxRecyclerViewBinder.CellItem, disposeBag: DisposeBag) {
        val binding = item.binding as ItemImageBinding
        binding.image = image

        RxView.clicks(binding.searchedImage)
                .filter { binding.image?.imageUrl?.get()?.isNotEmpty() ?: false }
                .subscribe { emitter?.takeIf { !it.hasComplete() }?.onNext(UITabImageEvent(image.imageUrl.get())) }
                .addTo(disposeBag)
    }

    companion object {
        val layoutResId = R.layout.item_image

        fun style() = RxRecyclerCellStyle(layoutResId)
    }
}