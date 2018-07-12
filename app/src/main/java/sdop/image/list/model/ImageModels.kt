package sdop.image.list.model

import android.databinding.BaseObservable
import android.databinding.ObservableField
import java.io.Serializable
import java.security.InvalidParameterException

/**
 *
 * Created by jei.park on 2017. 12. 28..
 */
interface ImageModelPresentable {
    val url: String
    val thumbnail: String
    val width: Int
    val height: Int
}

@Suppress("ConvertSecondaryConstructorToPrimary")
class ImageModel : BaseObservable, ImageModelPresentable, Serializable {
    val imageUrl = ObservableField<String>()

    override val url: String

    val imageWidth: String get() = width.toString()
    val imageHeight: String get() = height.toString()

    override val thumbnail: String
    override val width: Int
    override val height: Int

    constructor(url: String?, thumbnail: String?, width: Int?, height: Int?) : super() {
        if (url == null || thumbnail == null || width == null || height == null ) throw InvalidParameterException("arguments should be not null")

        this.url = url
        this.thumbnail = thumbnail
        this.width = width
        this.height = height

        this.imageUrl.set(url)
    }
}

interface SearchImageModelPresentable {
    var hasMore: Boolean
    var images: List<ImageModel>
}

data class SearchImageModel(override var hasMore: Boolean, override var images: List<ImageModel>) : SearchImageModelPresentable