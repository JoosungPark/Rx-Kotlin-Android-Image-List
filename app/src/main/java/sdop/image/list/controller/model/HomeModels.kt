package sdop.image.list.controller.model

import android.databinding.BaseObservable
import android.databinding.ObservableField
import java.security.InvalidParameterException

/**
 *
 * Created by jei.park on 2017. 12. 28..
 */
interface ImageModelPresentable {
    val url: String
    val width: Int
    val height: Int
}

class ImageModel : BaseObservable, ImageModelPresentable {
    val imageUrl = ObservableField<String>()
    override val url: String
    override val width: Int
    override val height: Int


    constructor(url: String?, width: Int?, height: Int?) : super() {
        if (url == null || width == null || height == null ) throw InvalidParameterException("arguments should be not null")

        this.url = url
        this.width = width
        this.height = height

        this.imageUrl.set(url)
    }
}

