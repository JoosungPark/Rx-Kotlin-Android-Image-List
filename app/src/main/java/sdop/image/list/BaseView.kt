package sdop.image.list

import sdop.image.list.common.BaseActivity
import sdop.image.list.common.BaseFragment

/**
 *
 * Created by jei.park on 2018. 1. 4..
 */
interface BaseView<in T> {
    val baseActivity: BaseActivity
    val baseFragment: BaseFragment
}