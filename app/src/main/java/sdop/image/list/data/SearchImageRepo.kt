package sdop.image.list.data

import io.reactivex.Observable
import sdop.image.list.model.SearchImageModelPresentable
import java.io.Serializable

/**
 *
 * Created by jei.park on 2018. 1. 4..
 */
interface SearchImageRepo : Serializable {
    fun getImages(keyword: String): Observable<SearchImageModelPresentable>
    fun getMoreImages(start: Int): Observable<SearchImageModelPresentable>
}