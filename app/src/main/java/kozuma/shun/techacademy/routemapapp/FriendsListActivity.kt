package kozuma.shun.techacademy.routemapapp

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_friend.*
import kotlinx.android.synthetic.main.activity_friend_list.*
import kotlinx.android.synthetic.main.activity_friends_list.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.list_addfriends.*
import kotlinx.android.synthetic.main.list_friends.*

class FriendsListActivity : AppCompatActivity() {

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mListView: ListView
    private lateinit var mFriendArrayList: ArrayList<Friends>
    private lateinit var mAdapter: FriendsListAdapter

    //ログイン中のユーザID
    val user = FirebaseAuth.getInstance().currentUser!!.uid


    private var mFriendRef: DatabaseReference? = null

    private val mEventListner = object : ChildEventListener{

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>
            val name = map["name"] ?: ""
            val friend_uid = dataSnapshot.key.toString()
//            //val friends  = map["friends"] ?: ""
//            val latitude  = map["latitude"] ?: ""
//            val longitude  = map["longitude"] ?: ""
//            val uid  = map["uid"] ?: ""
//            val senduser_id = map["senduser_id"] ?: ""

//            val friendsArrayList = ArrayList<Friends>()
//            val friendMap = map["friends"] as Map<String, String>?
//            if (friendMap != null) {
//                for (key in friendMap.keys) {
//                    val temp = friendMap[key] as Map<String, String>
//                    val answerName = temp["name"] ?: ""
//                    val answerUid = temp["uid"] ?: ""
//                    val answer = Friends(answerName, answerUid)
//                    friendsArrayList.add(answer)
//                }
//            }

            //val friend = User(name, friendsArrayList, latitude, longitude, uid, senduser_id)
            val friend = Friends(friend_uid ,name)
            mFriendArrayList.add(friend)
            mAdapter.notifyDataSetChanged()

        }

        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            mAdapter.notifyDataSetChanged()

        }

        override fun onChildRemoved(p0: DataSnapshot) {
            mAdapter.notifyDataSetChanged()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)






        //ユーザー検索画面に遷移
        userFindButton.setOnClickListener {
            val intent = Intent(applicationContext, UserFindActivity::class.java)
            startActivity(intent)
        }

        //友達一覧画面に遷移
        friendListButton.setOnClickListener{
            friendlistclick()

        }

        //友達許可画面に遷移
        permissionButton.setOnClickListener {

            permissionclick()

        }
        //pagers.adapter = TabAdapter(supportFragmentManager, this)
        //tab_layouts.setupWithViewPager(pagers)


        //Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        //ListViewの準備
        mListView = this.findViewById(R.id.listView1)
        mAdapter = FriendsListAdapter(this)
        mAdapter.context = this
        mAdapter.getItemViewType(0)
        mFriendArrayList = ArrayList<Friends>()
        mAdapter.notifyDataSetChanged()

        //友達のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す

        mFriendArrayList.clear()
        mAdapter.setFriendArrayList(mFriendArrayList)
        mListView.adapter = mAdapter
        mFriendRef = mDatabaseReference.child(UsersPATH).child(user).child("friend")
        mFriendRef!!.addChildEventListener(mEventListner)


        mAdapter.boolean = mAdapter.notifyDataSetChanged()


    }



    fun friendlistclick(){

        //友達のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mFriendArrayList.clear()
        mAdapter.setFriendArrayList(mFriendArrayList)
        mAdapter.context = this
        mListView.adapter = mAdapter
        mAdapter.getbuttonId(0)
        mFriendRef = mDatabaseReference.child(UsersPATH).child(user).child("friend")
        mFriendRef!!.addChildEventListener(mEventListner)

    }

    fun permissionclick(){

        //友達のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mFriendArrayList.clear()
        mAdapter.setFriendArrayList(mFriendArrayList)
        mAdapter.context = this
        mListView.adapter = mAdapter
        mAdapter.getbuttonId(1)
        mFriendRef = mDatabaseReference.child(UsersPATH).child(user).child("addfriend")
        mFriendRef!!.addChildEventListener(mEventListner)
    }

}
