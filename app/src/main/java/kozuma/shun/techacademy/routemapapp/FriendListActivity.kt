package kozuma.shun.techacademy.routemapapp

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Base64
import android.widget.ListView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_friend_list.*

class FriendListActivity : AppCompatActivity(), ViewPager.OnPageChangeListener,FragmentPage.OnFragmentInteractionListener {




    override fun onFragmentInteraction(uri: Uri) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        // 割り込み
        pager.addOnPageChangeListener(this)
        setTabLayout()


    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}
    // Fragmentからのコールバックメソッド

    // タブの設定
    private fun setTabLayout() {
        val adapter = TagAdapter(supportFragmentManager, this)
        pager.adapter = adapter
        tabs.setupWithViewPager(pager)

        val tab: TabLayout.Tab = tabs.getTabAt(0)!!
        tab.customView = adapter.getTabView(tabs, 0)

        //友達許可のタブ
        val tab1: TabLayout.Tab = tabs.getTabAt(1)!!
        tab1.customView = adapter.getTabView1(tabs, 1)

//        for (i in 0 until adapter.count) {
//            val tab: TabLayout.Tab = tabs.getTabAt(i)!!
//            tab.customView = adapter.getTabView(tabs, i)
//        }
    }
}
