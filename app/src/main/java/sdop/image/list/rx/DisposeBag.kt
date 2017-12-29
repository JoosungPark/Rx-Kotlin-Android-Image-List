package sdop.image.list.rx

import io.reactivex.disposables.Disposable
import rx.Subscription

/**
 *
 * Created by jei.park on 2017. 12. 20..
 */
class DisposeBag {
    private var disposeBags = mutableListOf<DisposeBag>()
    private var disposables = mutableListOf<Disposable>()
    private var rx1Subscriptions = mutableListOf<Subscription>()

    fun dispose() {
        disposeBags.forEach(DisposeBag::dispose)
        disposeBags.clear()

        disposables.forEach(Disposable::dispose)
        disposables.clear()

        rx1Subscriptions.forEach(Subscription::unsubscribe)
    }

    fun add(disposable: Disposable) = disposables.add(disposable)

    fun add(disposeBag: DisposeBag) = disposeBags.add(disposeBag)

    fun add(sub: Subscription) = rx1Subscriptions.add(sub)
}

fun Disposable.addTo(disposeBag: DisposeBag) = disposeBag.add(this)

fun Subscription.addTo(disposeBag: DisposeBag) = disposeBag.add(this)