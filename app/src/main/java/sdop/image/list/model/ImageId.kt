package sdop.image.list.model

import android.databinding.BaseObservable
import android.databinding.BindingAdapter
import android.databinding.ObservableField
import android.widget.ImageView
import com.squareup.picasso.Picasso
import sdop.image.list.R

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
typealias ImageId = String


@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    url?.let {
        Picasso.with(view.context)
                .load(it)
                .fit()
                .into(view)
    }
}