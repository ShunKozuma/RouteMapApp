package kozuma.shun.techacademy.routemapapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.GRAY
import android.graphics.Color.YELLOW
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
    private lateinit var mFriendArrayListNot: ArrayList<Friends>
    private lateinit var mAdapter: FriendsListAdapter

    //ログイン中のユーザID
    val user = FirebaseAuth.getInstance().currentUser!!.uid

    private var mFriendRef: DatabaseReference? = null
    private var mFriendNotRef: DatabaseReference? = null

    //addボタン選択ユーザのIDとPassを所得
    private lateinit var addname: String
    private lateinit var addid: String

    private var mbuttonId = 0

    private val mEventListner = object : ChildEventListener{

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>
            val name = map["name"] ?: ""
            val friend_uid = dataSnapshot.key.toString()
            val friend = Friends(friend_uid ,name)
            if(mbuttonId == 0){
                mFriendArrayList.add(friend)
            }else{
                mFriendArrayListNot.add(friend)
            }

            mAdapter.notifyDataSetChanged()


        }

        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
//            val map = dataSnapshot.value as Map<String, String>
//            val name = map["name"] ?: ""
//            val friend_uid = dataSnapshot.key ?:""
//            val friend = Friends(friend_uid ,name)
//            mFriendArrayList.remove(friend)
//            mAdapter.notifyDataSetChanged()
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
            FriendListclick()

        }

        //友達許可画面に遷移
        permissionButton.setOnClickListener {

            NotFriendclick()

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
        mFriendArrayListNot = ArrayList<Friends>()
        mAdapter.notifyDataSetChanged()


        //友達一覧表示
        FriendListclick()

        //ListViewを長押ししたときの処理
        mListView.setOnItemLongClickListener { parent, _, position, _ ->
            //選択ユーザのIDとPassを所得
            addid = mFriendArrayListNot[position].friend_uid
            addname = mFriendArrayListNot[position].name


            // ダイアログを表示する
            val builder = AlertDialog.Builder(this@FriendsListActivity)

            builder.setTitle("友達申請")
            builder.setMessage(addname+"の\n友達申請を許可しますか？")

            println(addid)
            println(addname)

            builder.setPositiveButton("OK") { _, _ ->

                if(mFriendRef != null){
                    mFriendRef = mDatabaseReference.child(UsersPATH).child(user).child("addfriend")
                    mFriendRef!!.removeEventListener(mEventListner)
                }

                // OKをタップしたときの処理
                FriendListDialog()
                Toast.makeText(this, "申請を許可しました！", Toast.LENGTH_LONG).show()


            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }


        //ListViewを長押ししたときの処理
        mListView.setOnItemClickListener { parent, _, position, _ ->
            //選択ユーザのIDとPassを所得

            addid = mFriendArrayList[position].friend_uid
            addname = mFriendArrayList[position].name
            println(addid)
            println(addname)

            // ダイアログを表示する
            val builder = AlertDialog.Builder(this@FriendsListActivity)

            builder.setTitle("友達取り消し")
            builder.setMessage(addname+"の\n友達を取り消しますか？")

            builder.setPositiveButton("OK") { _, _ ->
                if(mFriendNotRef != null){
                    mFriendNotRef = mDatabaseReference.child(UsersPATH).child(user).child("friend")
                    mFriendNotRef!!.removeEventListener(mEventListner)

                }
                // OKをタップしたときの処理
                NotFriendDialog()
                Toast.makeText(this, "友達を取り消しました！", Toast.LENGTH_LONG).show()
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }

    }



    fun FriendListclick(){
        //友達リスト

        mbuttonId = 0
        friendListButton.setBackgroundColor(YELLOW)
        permissionButton.setBackgroundColor(GRAY)
        mAdapter.getbuttonId(0)
        //友達のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mFriendArrayList.clear()
        mAdapter.setFriendArrayList(mFriendArrayList)
        mAdapter.context = this
        mListView.adapter = mAdapter
        mFriendRef = mDatabaseReference.child(UsersPATH).child(user).child("friend")
        mFriendRef!!.addChildEventListener(mEventListner)

    }

    fun NotFriendclick(){

        //友達かもりすと
        mbuttonId = 1
        permissionButton.setBackgroundColor(YELLOW)
        friendListButton.setBackgroundColor(GRAY)
        //友達のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mAdapter.getbuttonId(1)
        mFriendArrayListNot.clear()
        mAdapter.setFriendArrayList(mFriendArrayListNot)
        mAdapter.context = this
        mListView.adapter = mAdapter
        mFriendNotRef = mDatabaseReference.child(UsersPATH).child(user).child("addfriend")
        mFriendNotRef!!.addChildEventListener(mEventListner)
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

    fun NotFriendDialog(){


        //友達取り消し
        //ログインのユーザID
        val user = FirebaseAuth.getInstance().currentUser!!.uid

        //リファレンス
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        //addfriendから削除
        val addfriendRef = mDatabaseReference.child(UsersPATH).child(user).child("friend").child(addid)

        //println(favoriteRef)
        addfriendRef.removeValue()


        //friend追加のパス
        val deladdRef = mDatabaseReference.child(UsersPATH).child(user).child("addfriend").child(addid)
        //フレンド追加のデータ
        val data = HashMap<String, String>()
        data["name"] = addname
        deladdRef.setValue(data)


    }

}

