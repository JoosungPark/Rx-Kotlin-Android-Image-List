package sdop.image.list.common

import android.app.Application
import sdop.image.list.BuildConfig
import sdop.image.list.http.ImageServer
import sdop.image.list.preference.SearchImagePreferences

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class App : Application() {
    val server = ImageServer()
    lateinit var preferences: SearchImagePreferences

    override fun onCreate() {
        super.onCreate()
        app = this
        preferences = SearchImagePreferences(BuildConfig.APPLICATION_ID)
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)
    }

    private val uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, exception ->
        (exception as? OutOfMemoryError)?.let {

        }
    }

    companion object {
        lateinit var app: App
    }
}