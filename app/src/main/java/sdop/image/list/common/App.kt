package sdop.image.list.common

import android.app.Application
import sdop.image.list.BuildConfig
import sdop.image.list.http.ImageServer
import sdop.image.list.model.Persist
import sdop.image.list.preference.SearchImagePreferences

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class App : Application() {
    val server = ImageServer()
    lateinit var preferences: SearchImagePreferences
    lateinit var persist: Persist

    override fun onCreate() {
        super.onCreate()
        app = this
        preferences = SearchImagePreferences(BuildConfig.APPLICATION_ID)
        persist = Persist(preferences)

        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)
    }

    private val uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, exception ->
        (exception as? OutOfMemoryError)?.let {
            val value = persist.read<Int>(Persist.Key.ImageThreshold) ?: 0
            if (value > SearchImagePreferences.imageThresholdMinimum) {
                persist.write(Persist.Key.ImageThreshold, value - SearchImagePreferences.imageThresholdDelta)
            }
        }
    }

    companion object {
        lateinit var app: App
    }
}