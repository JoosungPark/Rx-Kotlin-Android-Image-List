package sdop.image.list.common

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import sdop.image.list.viewmodel.AppViewModel

inline fun <reified T : AppViewModel> BaseActivity.withViewModel(
        crossinline factory: () -> T,
        body: T.() -> Unit
): T {
    val vm = getViewModel(factory)
    vm.body()
    return vm
}

inline fun <reified T : AppViewModel> BaseActivity.getViewModel(crossinline factory: () -> T): T {
    val vmFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
    }

    return android.arch.lifecycle.ViewModelProviders.of(this, vmFactory)[T::class.java]
}

inline fun <reified T : AppViewModel> BaseFragment.withViewModel(crossinline factory: () -> T, body: T.() -> Unit): T {
    val vm = getViewModel(factory)
    vm.body()
    return vm
}

inline fun <reified T : AppViewModel> BaseFragment.getViewModel(crossinline factory: () -> T): T {
    val vmFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
    }

    return android.arch.lifecycle.ViewModelProviders.of(this, vmFactory)[T::class.java]
}

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) {
    liveData.observe(this, Observer(body))
}