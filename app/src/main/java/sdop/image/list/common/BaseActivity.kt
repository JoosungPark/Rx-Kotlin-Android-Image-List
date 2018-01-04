package sdop.image.list.common

import android.support.v7.app.AppCompatActivity
import sdop.image.list.R
import sdop.image.list.rx.DisposeBag
import java.util.ArrayList

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
open class BaseActivity : AppCompatActivity() {
    protected val DEBUG_TAG = this.javaClass.simpleName
    protected var disposeBag = DisposeBag()

    protected fun loadFragment(fragment: BaseFragment, animations: ArrayList<Int>? = null, replace: Boolean = false, containerId: Int = R.id.container, allowStateLoss: Boolean = false) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        if (animations != null) {
            ft.setCustomAnimations(animations[0], animations[1], animations[2], animations[3])
        }
        if (replace) {
            ft.replace(containerId, fragment)
            ft.addToBackStack(null)
        } else {
            ft.add(containerId, fragment)
        }


        if (allowStateLoss) {
            ft.commitAllowingStateLoss()
        } else {
            ft.commit()
        }
    }

    protected fun replaceFragment(fragment: BaseFragment, containerId: Int = R.id.container) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(containerId, fragment)
        ft.commit()
    }

    protected fun loadFragment(fragment: BaseFragment, replace: Boolean = false, containerId: Int = R.id.container) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        if (replace) {
            ft.replace(containerId, fragment)
            ft.addToBackStack(null)
        } else {
            ft.add(containerId, fragment)
        }
        ft.commit()
    }

    protected fun loadFragment(type: FragmentFactory.FragmentType, containerId: Int, arg: String? = null) {
        loadFragment(FragmentFactory.createFragment(type, arg), false, containerId)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeBag.dispose()
    }

    override fun onBackPressed() {
        val fm = supportFragmentManager

        if (fm.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            fm.popBackStack()
            fm.beginTransaction().commit()
        }
    }

}