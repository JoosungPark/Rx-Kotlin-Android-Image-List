package sdop.image.list.data

import io.reactivex.Observable
import sdop.image.list.controller.home.HomeImageModelPresentable

/**
 *
 * Created by jei.park on 2018. 1. 4..
 */
interface SearchImageRepo {
    fun getImages(keyword: String): Observable<HomeImageModelPresentable>
    fun getMoreImages(start: Int): Observable<HomeImageModelPresentable>
}