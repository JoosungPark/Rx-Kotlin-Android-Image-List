package sdop.image.list.rx

import android.databinding.ObservableField
import io.reactivex.subjects.BehaviorSubject

/**
 *
 * Created by jei.park on 2017. 12. 21..
 */
class Nullable<T>(_value: T?) {

    var value = _value

    fun <R> map(mapper: (T?) -> R?) = Nullable(mapper(value))

    companion object {
        fun <T> from(value: T?): Nullable<T> {
            return Nullable(value)
        }
    }
}

// https://gist.github.com/operando/2add7bbad535bc30f7340bf8c04660d7
class Variable<T>(private var _value: T) : ObservableField<T>() {

    private val serializedSubject = BehaviorSubject.createDefault(_value)

    override fun get() = _value

    override fun set(value: T) {
        this._value = value
        super.notifyChange()
        serializedSubject.onNext(this._value)
    }

    var value: T
        @Synchronized get() = this._value
        @Synchronized set(value) {
            this._value = value
            super.notifyChange()
            serializedSubject.onNext(this._value)
        }

    fun asObservable() = serializedSubject

    fun rx(): (T) -> Unit {
        return fun(value: T) {
            this.set(value)
        }
    }

    override fun notifyChange() {
        super.notifyChange()
        serializedSubject.onNext(this._value)
    }
}

class MaybeVariable<T>(_value: T? = null) : ObservableField<T>() {

    private var nullableValue = Nullable(_value)

    private val serializedSubject = BehaviorSubject.createDefault(nullableValue)

    override fun get() = nullableValue.value

    override fun set(value: T?) {
        this.nullableValue = Nullable(value)
        super.notifyChange()
        serializedSubject.onNext(nullableValue)
    }

    var value: T?
        @Synchronized get() = nullableValue.value
        @Synchronized set(value) {
            this.nullableValue = Nullable(value)
            super.notifyChange()
            serializedSubject.onNext(this.nullableValue)
        }

    fun asObservable() = serializedSubject

    fun rx(): (T) -> Unit {
        return fun(value: T) {
            this.set(value)
        }
    }

    fun rxNullable(): (Nullable<T>) -> Unit {
        return fun(value: Nullable<T>) {
            this.set(value.value)
        }
    }

    override fun notifyChange() {
        super.notifyChange()
        serializedSubject.onNext(nullableValue)
    }
}