package sdop.image.list.common

import sdop.image.list.common.FragmentBundle.ImagePager
import sdop.image.list.controller.home.HomeFragment
import sdop.image.list.controller.image.ImagePagerFragment
import sdop.image.list.controller.image.cell.ImageFragment
import sdop.image.list.data.SearchImageRepo
import sdop.image.list.model.ImageModel
import sdop.image.list.rx.Variable

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */

sealed class FragmentBundle {
    object Home : FragmentBundle()
    data class ImagePager(val currentImage: ImageModel, val images: Variable<List<ImageModel>>, val repo: SearchImageRepo) : FragmentBundle()
    data class Image(val image: ImageModel) : FragmentBundle()
}

class FragmentFactory {

    companion object {
        fun createFragment(bundle: FragmentBundle): BaseFragment = when (bundle) {
                is FragmentBundle.Home -> HomeFragment.newInstance()
                is ImagePager -> ImagePagerFragment.newInstance(bundle.currentImage, bundle.images, bundle.repo)
                is FragmentBundle.Image -> ImageFragment.newInstance(bundle.image)
        }
    }
}