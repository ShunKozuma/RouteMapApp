package kozuma.shun.techacademy.routemapapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.ListView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_friends_list.*

class FriendsListActivity : AppCompatActivity() {

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mListView: ListView
    private lateinit var mFriendArrayList: ArrayList<Friends>
    private lateinit var mAdapter: FriendsListAdapter

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
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        //Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        //ListViewの準備
        mListView = findViewById(R.id.listView1)
        mAdapter = FriendsListAdapter(this)
        mFriendArrayList = ArrayList<Friends>()
        mAdapter.notifyDataSetChanged()

        //友達のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mFriendArrayList.clear()
        mAdapter.setFriendArrayList(mFriendArrayList)
        mListView.adapter = mAdapter

        mFriendRef = mDatabaseReference.child(UsersPATH).child("JpCvSGd48AOCkXeVYImSmFdQM3t2").child("friend")
        mFriendRef!!.addChildEventListener(mEventListner)
    }
}
