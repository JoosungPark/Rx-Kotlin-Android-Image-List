package sdop.image.list.util

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.StringRes
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import sdop.image.list.App
import sdop.image.list.R
import sdop.image.list.rx.delay

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class Notifier {
    companion object {
        var toast: Toast? = null

        fun toast(resId: Int, gravity: Int) {
            delay {
                val toast = Toast.makeText(App.app, App.app.getString(resId), Toast.LENGTH_SHORT)
                toast.setGravity(gravity, 0, App.app.resources.getDimensionPixelSize(R.dimen.Toast_Bottom_Margin))
                toast.show()
            }
        }

        fun toast(@StringRes resId: Int) {
            toast(App.app.getString(resId))
        }

        fun toast(msg: String?) {
            if (msg == null) {
                LogUtil.e("Toast Error msg is null")
                return
            }
            delay {
                if (toast == null) {
                    @SuppressLint("ShowToast")
                    toast = Toast.makeText(App.app, msg, Toast.LENGTH_SHORT)
                } else {
                    toast?.setText(msg)
                }
                toast?.setGravity(Gravity.CENTER, 0, 0)
                toast?.view?.findViewById<TextView>(android.R.id.message)?.gravity = Gravity.CENTER
                toast?.show()

            }
        }
    }
}