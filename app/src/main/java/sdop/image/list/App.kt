package sdop.image.list

import android.app.Application
import sdop.image.list.http.ImageServer

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class App : Application() {
    val server = ImageServer()

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object {
        lateinit var app: App
    }
}