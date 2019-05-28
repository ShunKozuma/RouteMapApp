package kozuma.shun.techacademy.routemapapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class FriendsListAdapter(context: Context): BaseAdapter() {
    private var mLayoutInflater: LayoutInflater
    private var mFriendArrayList = ArrayList<Friends>()

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return mFriendArrayList.size
    }

    override fun getItem(position: Int): Any {
        return mFriendArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View{
        var convertView = convertView

        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.list_friends, parent, false)
        }

        val nameText = convertView!!.findViewById<View>(R.id.nameTextView) as TextView
        nameText.text = mFriendArrayList[position].name

        return convertView
    }

    fun setFriendArrayList(friendArrayList: ArrayList<Friends>){
        mFriendArrayList = friendArrayList
    }


}