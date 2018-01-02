package sdop.image.list.model

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import sdop.image.list.R
import sdop.image.list.util.LogUtil

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
typealias ImageId = String

private val imagePlaceHolder = R.color.basic_divider

@BindingAdapter("imageUrl", "imageWidth", "imageHeight")
fun loadImage(view: ImageView, url: String?, width: String?, height: String?) {
    if (width == null || height == null) throw IllegalArgumentException("width or height contain null value. width : $width height : $height")
    val scaledSize = recommendSize(width.toInt(), height.toInt(), 700, 100)

    url?.let {
        Picasso.with(view.context)
                .load(it)
                .resize(scaledSize.first, scaledSize.second)
                .placeholder(imagePlaceHolder)
                .error(imagePlaceHolder)
                .into(view)
    } ?: run {
        Picasso.with(view.context)
                .load(imagePlaceHolder)
                .placeholder(imagePlaceHolder)
                .error(imagePlaceHolder)
                .into(view)
    }
}

private fun recommendSize(originWidth: Int, originHeight: Int, threshold: Int, minHeight: Int): Pair<Int, Int> {
    return if (originWidth < threshold && originHeight < threshold) Pair(originWidth, originHeight)
    else {
        var scaledSize = Pair(originWidth, originHeight)
        val delta = 0.1
        var index = 9

        while (scaledSize.second > minHeight && scaledSize.first > threshold || scaledSize.second > threshold) {
            val scaledWidth = (originWidth * index * delta).toInt()
            val scaledHeight = (originHeight * index * delta).toInt()
            scaledSize = Pair(scaledWidth, scaledHeight)
            index -= 1
        }

        scaledSize
    }
}
