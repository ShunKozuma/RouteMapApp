package kozuma.shun.techacademy.routemapapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.PagerAdapter
import kotlinx.android.synthetic.main.activity_friend.*
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager



class FriendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

        pager.adapter = TabAdapter(supportFragmentManager, this)
        tab_layout.setupWithViewPager(pager)

        fabs.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}
