package kozuma.shun.techacademy.routemapapp

import android.content.Context
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class TagAdapter(fm: FragmentManager, private val context: Context) : FragmentStatePagerAdapter(fm) {
    private val pageTitle = arrayOf("友達一覧", "友達許可")
    //
    override fun getItem(position: Int): Fragment {
        // 要求時 新しい Fragment を生成して返す
        return FragmentPage.newInstance(position + 1)
    }
    // タブの名前
    override fun getPageTitle(position: Int): CharSequence? {
        return pageTitle[position]
    }
    // タブの個数
    override fun getCount(): Int {
        return pageTitle.size
    }
    // タブの変更
    fun getTabView(tabLayout: TabLayout, position: Int): View {
        // tab_item.xml を複数
        val view = LayoutInflater.from(this.context).inflate(R.layout.tab_item, tabLayout, false)
        // タイトル
        val tab = view.findViewById<TextView>(R.id.teb_item_text)
        tab.text = pageTitle[position]
        // アイコン
        var image = view.findViewById<ImageView>(R.id.teb_item_image)
        image.setImageResource(R.drawable.friend)
        return view
    }
    // タブの変更
    fun getTabView1(tabLayout: TabLayout, position: Int): View {
        // tab_item.xml を複数
        val view = LayoutInflater.from(this.context).inflate(R.layout.tab_item, tabLayout, false)
        // タイトル
        val tab = view.findViewById<TextView>(R.id.teb_item_text)
        tab.text = pageTitle[position]
        // アイコン
        var image = view.findViewById<ImageView>(R.id.teb_item_image)
        image.setImageResource(R.drawable.friendplus)
        return view
    }



}