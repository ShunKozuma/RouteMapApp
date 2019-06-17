package kozuma.shun.techacademy.routemapapp

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.co.yahoo.android.maps.MapView

class ListFriendAddFragment : Fragment() {

    lateinit var mapView: MapView
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mListViews: ListView
    private lateinit var mListView: ListView
    private lateinit var mFriendArrayList: ArrayList<Friends>
    private lateinit var mAdapter: FriendsListAdapter

    //ログイン中のユーザID
    val user = FirebaseAuth.getInstance().currentUser!!.uid

    private var mFriendRef: DatabaseReference? = null

    //addボタン選択ユーザのIDとPassを所得
    private lateinit var addname: String
    private lateinit var addid: String

    private var mbuttonId: Boolean = false


    var thisis: Context? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(kozuma.shun.techacademy.routemapapp.R.layout.list_friend_add_fragment, container, false)
        mListViews = view.findViewById(R.id.listView1)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_friends_list)


        mapView = MapView(context as Activity?, "dj0zaiZpPWowWHRab050ODJyTyZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-")

        thisis = context



        //Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        //ListViewの準備
        mListView = mListViews
        mAdapter = FriendsListAdapter(thisis!!)
        mFriendArrayList = ArrayList<Friends>()
        mAdapter.notifyDataSetChanged()



        NotFriendclick()

        //ListViewを長押ししたときの処理
        mListView.setOnItemLongClickListener { parent, _, position, _ ->
            //選択ユーザのIDとPassを所得
            addid = mFriendArrayList[position].friend_uid
            addname = mFriendArrayList[position].name


                // 友達許可ダイアログを作成して表示
                AlertDialog.Builder(thisis!!).apply {
                    setTitle("友達申請")
                    setMessage(addname + "の\n友達申請を許可しますか？")
                    setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                        // OKをタップしたときの処理
                        FriendListDialog()
                        Toast.makeText(context, "申請を許可しました！", Toast.LENGTH_LONG).show()
                    })
                    setNegativeButton("Cancel", null)
                    show()
                }

                true


        }
    }

    private val mEventListener = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            if(dataSnapshot.value == null){
                mFriendArrayList.clear()
            }

            val map = dataSnapshot.value as Map<String, String>
            val name = map["name"] ?: ""
            val friend_uid = dataSnapshot.key ?: ""
            val friend = Friends(friend_uid, name)
            mFriendArrayList.add(friend)
            mAdapter.notifyDataSetChanged()



        }

        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
        }
    }

    override fun onResume() {
        super.onResume()
        println("Resumeした")
    }
    override fun onDestroy() {
        super.onDestroy()
        println("Destroyした")
    }

    override fun onPause() {
        super.onPause()

    }



    fun NotFriendclick() {

        //友達かもりすと
        mbuttonId = true
        //友達のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mAdapter.getbuttonId(1)
        mFriendArrayList.clear()
        mAdapter.setFriendArrayList(mFriendArrayList)
        mListView.adapter = mAdapter
        mFriendRef = mDatabaseReference.child(UsersPATH).child(user).child("addfriend")
        mFriendRef!!.addChildEventListener(mEventListener)

    }

    fun FriendListDialog() {


        //友達追加
        //ログインのユーザID
        val user = FirebaseAuth.getInstance().currentUser!!.uid

        //リファレンス
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        //addfriendから削除
        val deladdRef = mDatabaseReference.child(UsersPATH).child(user).child("addfriend").child(addid)

        //println(favoriteRef)
        deladdRef.removeValue()

        //friend追加のパス
        val addfriendRef = mDatabaseReference.child(UsersPATH).child(user).child("friend").child(addid)
        //フレンド追加のデータ
        val data = HashMap<String, String>()
        data["name"] = addname
        addfriendRef.setValue(data)

        //ListViewの準備
//        mListView = mListViews
//        mAdapter = FriendsListAdapter(thisis!!)
//        mFriendArrayList = ArrayList<Friends>()
//        mAdapter.notifyDataSetChanged()

        mFriendArrayList.clear()
        mFriendRef = mDatabaseReference.child(UsersPATH).child(user).child("addfriend")
        mFriendRef!!.addChildEventListener(mEventListener)


    }

}
