package sdop.image.list.common

import sdop.image.list.controller.home.HomeFragment
import sdop.image.list.controller.ImageFragment

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
data class FragmentBundle(val type: FragmentFactory.FragmentType, val arg: String)

class FragmentFactory {

    enum class FragmentType {
        Home,
        Images
    }

    companion object {
        fun createFragment(bundle: FragmentBundle) = createFragment(bundle.type, bundle.arg)

        fun createFragment(type: FragmentType, arg: String? = null): BaseFragment = when (type) {
            FragmentType.Home -> HomeFragment.newInstance()
            FragmentType.Images -> ImageFragment.newInstance()
        }
    }
}