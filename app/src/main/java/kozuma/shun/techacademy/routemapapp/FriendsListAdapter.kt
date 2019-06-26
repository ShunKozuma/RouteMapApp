package kozuma.shun.techacademy.routemapapp

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth


class FriendsListAdapter(context: Context) : BaseAdapter() {
    private var mLayoutInflater: LayoutInflater
    private var mFriendArrayList = ArrayList<Friends>()
    var context: Context? = null
    private var buttonId: Int = 0

    val user = FirebaseAuth.getInstance().currentUser!!.uid

    var friendRecieve: String? = null
    var friendSend: String? = null

    var friendPosition: Int? = null

    var juju: Boolean = false

    var kaposi: Int? = null
    private var mHandler = Handler()


    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    fun getbuttonId(id: Int) {
        buttonId = id
    }

//    fun receiveUserId(receiveId: String){
//        friendRecieve = receiveId
//    }

    fun sendUserId(ju: Boolean, count: Int, sendId: String) {
        println("受け取った$ju$count: $sendId")
        juju = ju
        friendPosition = count
        friendSend = sendId

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


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {


        //println("getViewだぜ！！")
        var convertView = convertView


        if (buttonId == 0) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_friends, parent, false)
            }


            val nameText = convertView!!.findViewById<View>(R.id.nameTextView) as TextView
            nameText.text = mFriendArrayList[position].name

            //val shareText = convertView!!.findViewById<View>(R.id.ShareData) as TextView

            //val sText = convertView!!.findViewById<View>(R.id.SData) as TextView

            //val shareView = convertView!!.findViewById<View>(R.id.ShareView) as ImageView
            val shareupView = convertView!!.findViewById<View>(R.id.ShareUpView) as ImageView
            val sharedownView = convertView!!.findViewById<View>(R.id.ShareDownView) as ImageView

            println("どっち" + mFriendArrayList[position].sendBoolean)
            println("できて"+mFriendArrayList[position].recieveBoolean)

            if(mFriendArrayList[position].recieveBoolean == true && mFriendArrayList[position].sendBoolean == true){
                //shareText.text = "現在地送受信中"
                //shareText.text = "↓"
                //shareView.setImageResource(R.mipmap.now)
                shareupView.setImageResource(R.mipmap.arrowup)
                sharedownView.setImageResource(R.mipmap.arrowdown)

            }else if (mFriendArrayList[position].sendBoolean == true) {
                //shareText.text = "現在地送信中"
                //shareText.text = "↑"
                //shareView.setImageResource(R.mipmap.now)
                shareupView.setImageResource(R.mipmap.arrowup)
            }else if (mFriendArrayList[position].recieveBoolean == true) {
                //shareText.text = "現在地受信中"
                //shareText.text = "↑↓"
                //shareView.setImageResource(R.mipmap.now)
                sharedownView.setImageResource(R.mipmap.arrowdown)
            }

        } else {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_addfriends, parent, false)
            }

            val nameText = convertView!!.findViewById<View>(R.id.nameTextView) as TextView
            nameText.text = mFriendArrayList[position].name

        }


        /*

        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.list_addfriends, parent, false)
        }


        val nameText = convertView!!.findViewById<View>(R.id.nameTextView) as TextView
        nameText.text = mFriendArrayList[position].name
        */

        return convertView


    }

    fun setFriendArrayList(friendArrayList: ArrayList<Friends>) {
        mFriendArrayList = friendArrayList
    }


}