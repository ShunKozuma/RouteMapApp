package kozuma.shun.techacademy.routemapapp

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class TabAdapter(fm: FragmentManager, private val context: Context): FragmentPagerAdapter(fm){

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> { return Tab01Fragment() }
            else ->  { return Tab02Fragment() }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> { return "友達一覧" }
            else ->  { return "友達許可" }
        }
    }

    override fun getCount(): Int {
        return 2
    }
}