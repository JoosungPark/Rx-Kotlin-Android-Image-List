package sdop.image.list

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import sdop.image.list.common.FragmentFactory

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(FragmentFactory.FragmentType.Home, R.id.container)
    }
}
