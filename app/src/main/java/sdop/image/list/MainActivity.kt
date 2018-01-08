package sdop.image.list

import android.os.Bundle
import sdop.image.list.common.BaseActivity
import sdop.image.list.common.FragmentBundle
import sdop.image.list.common.FragmentFactory

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(FragmentBundle.Home, containerId = R.id.container)
    }
}
