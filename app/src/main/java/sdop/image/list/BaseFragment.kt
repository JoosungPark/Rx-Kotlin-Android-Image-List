package sdop.image.list

import android.content.Context
import android.support.v4.app.Fragment
import sdop.image.list.util.KeyboardUtils
import sdop.image.list.rx.DisposeBag

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
open class BaseFragment : Fragment() {
    protected val DEBUG_TAG = this.javaClass.simpleName
    protected val server = App.app.server

    var disposeBag = DisposeBag()
    private var baseActivityContext: BaseActivity? = null

    val baseActivity: BaseActivity
        get() {
        return (activity as? BaseActivity)?.let { it }
        ?: baseActivityContext ?: activity as BaseActivity
    }

    fun goToFragment(fragment: BaseFragment, containerId: Int = R.id.container) {
        goToFragment(fragment, null, containerId)
    }

    fun goToFragment(fragment: BaseFragment, animations: ArrayList<Int>? = null, containerId: Int = R.id.container) {
        activity?.let {
            val fm = it.supportFragmentManager
            val ft = fm.beginTransaction()
            animations?.let { ft.setCustomAnimations(it[0], it[1], it[2], it[3]) }
            ft.replace(containerId, fragment)
            ft.addToBackStack(null)

            ft.commitAllowingStateLoss()
        }
    }

    fun pushFragment(fragment: BaseFragment, animations: ArrayList<Int>? = null, containerId: Int = R.id.container, addBackStack: Boolean = true) {
        activity?.let {
            val fm = it.supportFragmentManager
            val ft = fm.beginTransaction()
            if (animations != null) {
                ft.setCustomAnimations(animations[0], animations[1], animations[2], animations[3])
            }
            ft.add(containerId, fragment)
            if (addBackStack) {
                ft.addToBackStack(null)
            }
            ft.commitAllowingStateLoss()
        }
    }

    fun pushFragment(fragment: BaseFragment, containerId: Int = R.id.container) {
        pushFragment(fragment, null, containerId)
    }

    fun goBackFragment() {
        KeyboardUtils.hideKeyboard(this)

        activity?.let {
            val fm = it.supportFragmentManager
            fm.popBackStack()
            fm.beginTransaction().commitAllowingStateLoss()
        }
    }

    fun popFragment() {
        goBackFragment()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        (context as? BaseActivity)?.let { baseActivityContext = it }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        disposeBag.dispose()
    }
}