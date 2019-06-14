package kozuma.shun.techacademy.routemapapp

import android.content.Context

import android.content.DialogInterface
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_friends_list.*
import android.content.Intent


class FriendsListAdapter(context: Context) : BaseAdapter() {
    private var mLayoutInflater: LayoutInflater
    private var mFriendArrayList = ArrayList<Friends>()
    var context: Context? = null
    private var buttonId: Int = 0

    val user = FirebaseAuth.getInstance().currentUser!!.uid

    var friendRecieve: String? = null
    var friendSend: String? = null

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    fun getbuttonId(id: Int) {
        buttonId = id
    }

    fun receiveUserId(receiveId: String){
        friendRecieve = receiveId
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
        var convertView = convertView


        if (buttonId == 0) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_friends, parent, false)
            }

            val nameText = convertView!!.findViewById<View>(R.id.nameTextView) as TextView
            nameText.text = mFriendArrayList[position].name

            val shareText = convertView!!.findViewById<View>(R.id.ShareData) as TextView

            println(""+friendSend+"=="+mFriendArrayList[position].friend_uid)
            if(friendRecieve == mFriendArrayList[position].friend_uid){
                shareText.text = "現在地受信中"
            }else if(friendSend == mFriendArrayList[position].friend_uid){
                shareText.text = "現在地送信中"
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