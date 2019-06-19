package kozuma.shun.techacademy.routemapapp

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity

class ListFriendActivity : AppCompatActivity() {

    private val tabTitle = arrayOf<CharSequence>("友達一覧", "友達申請")

    val adapter = object : FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return ListFriendFragment()
                1 -> return ListFriendAddFragment()
                else ->
                    return null
            }

        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tabTitle[position]
        }

        override fun getCount(): Int {
            return tabTitle.size
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_friend)

//        var res = intent.getStringExtra("TAB_DATA")
//        if(res == "3"){
//            adapter.getItem(1)
//        }


        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.setOffscreenPageLimit(tabTitle.size)
        viewPager.setAdapter(adapter)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)



//        //ユーザー検索画面に遷移
//        userFindButton.setOnClickListener {
//            val intent = Intent(applicationContext, UserFindActivity::class.java)
//            startActivity(intent)
//        }
    }
}
