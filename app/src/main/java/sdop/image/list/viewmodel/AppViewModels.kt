package sdop.image.list.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable
import sdop.image.list.util.LogUtil

open class AppViewModel(context: Application) : AndroidViewModel(context) {
    val disposeBag = CompositeDisposable()

    override fun onCleared() {
        disposeBag.dispose()
        super.onCleared()
    }
}