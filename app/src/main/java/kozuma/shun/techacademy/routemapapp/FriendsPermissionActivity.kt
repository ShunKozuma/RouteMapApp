package kozuma.shun.techacademy.routemapapp

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_friends_list.*

class FriendsPermissionActivity : AppCompatActivity() {

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mListView: ListView
    private lateinit var mFriendArrayList: ArrayList<Friends>
    private lateinit var mAdapter: FriendsListAdapter

    //ログイン中のユーザID
    val user = FirebaseAuth.getInstance().currentUser!!.uid

    private var mFriendRef: DatabaseReference? = null

    //addボタン選択ユーザのIDとPassを所得
    private lateinit var addname: String
    private lateinit var addid: String



    private val mEventListner = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>
            val name = map["name"] ?: ""
            val friend_uid = dataSnapshot.key.toString()
            val friend = Friends(friend_uid ,name)
            mFriendArrayList.add(friend)
            mAdapter.notifyDataSetChanged()

        }

        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {

        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_permission)


        //ユーザー検索画面に遷移
        userFindButton.setOnClickListener {
            val intent = Intent(applicationContext, UserFindActivity::class.java)
            startActivity(intent)
        }

        //友達一覧画面に遷移
        friendListButton.setOnClickListener{
            val intent = Intent(applicationContext, FriendsListActivity::class.java)
            startActivity(intent)

        }

        //友達許可画面に遷移
        permissionButton.setOnClickListener {
            val intent = Intent(applicationContext, FriendsPermissionActivity::class.java)
            startActivity(intent)

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


        permissionButton.setBackgroundColor(Color.YELLOW)
        friendListButton.setBackgroundColor(Color.GRAY)
        //友達のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        //mAdapter.getbuttonId(1)
        //友達のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mFriendArrayList.clear()
        mAdapter.setFriendArrayList(mFriendArrayList)
        mAdapter.context = this
        mListView.adapter = mAdapter
        mFriendRef = mDatabaseReference.child(UsersPATH).child(user).child("addfriend")
        mFriendRef!!.addChildEventListener(mEventListner)


        //ListViewを長押ししたときの処理
        mListView.setOnItemLongClickListener { parent, _, position, _ ->
            //選択ユーザのIDとPassを所得
            addid = mFriendArrayList[position].friend_uid
            addname = mFriendArrayList[position].name
            println(addid)
            println(addname)

            // ダイアログを表示する
            val builder = AlertDialog.Builder(this@FriendsPermissionActivity)

            builder.setTitle("友達申請")
            builder.setMessage(addname+"の\n友達申請を許可しますか？")

            builder.setPositiveButton("OK") { _, _ ->
                // OKをタップしたときの処理
                FriendListDialog()
                Toast.makeText(this, "申請を許可しました！", Toast.LENGTH_LONG).show()

            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }


    }



    fun FriendListDialog(){

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


    }



}
