package sdop.image.list.controller.image.cell

import sdop.image.list.model.ImageModel

/**
 *
 * Created by jei.park on 2018. 1. 5..
 */
data class ImageViewModel(private val view: ImageContract.View, override val model: ImageModel) : ImageContract.ViewModel