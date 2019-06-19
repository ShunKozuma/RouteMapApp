package kozuma.shun.techacademy.routemapapp

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color.GRAY
import android.graphics.Color.YELLOW
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.co.yahoo.android.maps.GeoPoint
import jp.co.yahoo.android.maps.MapView
import jp.co.yahoo.android.maps.MyLocationOverlay
import kotlinx.android.synthetic.main.activity_friends_list.*


class FriendsListActivity : AppCompatActivity() {

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mListView: ListView
    private lateinit var mFriendArrayList: ArrayList<Friends>
    private lateinit var mAdapter: FriendsListAdapter

    //ログイン中のユーザID
    val user = FirebaseAuth.getInstance().currentUser!!.uid

    private var mFriendRef: DatabaseReference? = null
    private var mFriendSendRef: DatabaseReference? = null

    //addボタン選択ユーザのIDとPassを所得
    private lateinit var addname: String
    private lateinit var addid: String

    private var mbuttonId: Boolean = false

    private lateinit var button: String

    var text = mutableListOf<String>()

    var count = -1


    var context: Context? = null

    //現在地取得
    private var _overlay: MyLocationOverlay? = null
    private var keido = 0.0 //現在地　経度
    private var ido = 0.0 //現在地　緯度
    private var p: GeoPoint = GeoPoint(0, 0)//現在地の取得

    lateinit var intentis: Intent

    private var mHandler = Handler()

    private val mEventRecieveListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            //count++
            if (dataSnapshot.value != null) {
                val map = dataSnapshot.value as Map<String, String>
                val user_id = map["user_id"] ?: ""
                mAdapter.friendRecieve = user_id
            }
            mAdapter.notifyDataSetChanged()
        }

    }

    private val mEventSendListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            println("count" + count)
            count++
            println("配列の行数" + (mFriendArrayList.size - 1))
            if (dataSnapshot.value != null && count <= mFriendArrayList.size - 1) {
                val map = dataSnapshot.value as Map<String, String>
                val user_id = map["user_id"] ?: ""
                val latitude = map["latitude"] ?: ""
                val longitude = map["longitude"] ?: ""

                println(latitude)
                println(longitude)
                println(user_id)

                if (user_id.equals(user)) {
                    println("Listの" + count)
                    println("送信中の" + text[count])
                    //mAdapter.friendPosition = count
                    //mAdapter.friendSend = text[count]
                    mAdapter.sendUserId(true, count, text[count])

                }
            }

            mAdapter.notifyDataSetChanged()

            //mFriendRef = mDatabaseReference.child(UsersPATH).child(user).child("location")
            //mFriendRef!!.addValueEventListener(mEventRecieveListener)

        }

    }
    /*
    private val mEventSendListener = object : ChildEventListener {
        override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
            println("count"+count)
            count++
            println("配列の行数" + (mFriendArrayList.size - 1))
            if (dataSnapshot.value != null && count <= mFriendArrayList.size - 1) {
                val map = dataSnapshot.value as Map<String, String>
                val user_id = map["user_id"] ?: ""
                val latitude = map["latitude"] ?: ""
                val longitude = map["longitude"] ?: ""


                println(latitude)
                println(longitude)
                println(user_id)


                if (user_id.equals(user)) {
                    println("Listの" + count)
                    println("送信中の" + text[count])
//                    mAdapter.friendPosition = count
//                    mAdapter.friendSend = text[count]
                    mAdapter.sendUserId(true,count,text[count])
                    mAdapter.notifyDataSetChanged()

                }
            }
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {

        }

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(p0: DataSnapshot) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onCancelled(p0: DatabaseError) {

        }


    }
*/

    private val mEventListener = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>
            val name = map["name"] ?: ""
            val friend_uid = dataSnapshot.key ?: ""
            val friend = Friends(friend_uid, name,false)

            text.add(friend_uid)
            mFriendArrayList.add(friend)

            mFriendSendRef = mDatabaseReference.child(UsersPATH).child(friend_uid).child("location")
            mFriendSendRef!!.addListenerForSingleValueEvent(mEventSendListener)

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)

        context = this

        //ユーザー検索画面に遷移
        userFindButton.setOnClickListener {
            val intent = Intent(applicationContext, UserFindActivity::class.java)
            startActivity(intent)
        }

        //友達一覧画面に遷移
        friendListButton.setOnClickListener {
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
        mFriendArrayList = ArrayList<Friends>()
        //mAdapter.notifyDataSetChanged()


        val intent = intent
        button = intent.getStringExtra("button")
        if (button.equals("0")) {
            //友達一覧表示
            FriendListclick()
        } else {
            NotFriendclick()
        }

        intentis = Intent(this, FriendsListActivity::class.java)

        mListView.setOnItemClickListener { parent, view, position, id ->
            addid = mFriendArrayList[position].friend_uid
            addname = mFriendArrayList[position].name

            if (mbuttonId == true) {


                // 友達許可ダイアログを作成して表示
                AlertDialog.Builder(this).apply {
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

            } else {
                // アラートダイアログ
                alertCheck(addid, addname)
            }
            /*
            //友達に位置情報の共有ダイアログ
            AlertDialog.Builder(this).apply {
                setTitle("現在地共有")
                setMessage(addname + "に現在地を共有しますか？")
                setPositiveButton("共有", DialogInterface.OnClickListener { _, _ ->
                    location()
//                    mAdapter.friendSend = addid
//                    mAdapter.friendPosition = position
//                    mAdapter.notifyDataSetChanged()
                    Toast.makeText(context, "現在地を共有しました！", Toast.LENGTH_LONG).show()
                })

                setNegativeButton("Cancel", null)
                show()
            }*/

        }

        /*
        //ListViewを長押ししたときの処理
        mListView.setOnItemLongClickListener { parent, _, position, _ ->
            //選択ユーザのIDとPassを所得
            addid = mFriendArrayList[position].friend_uid
            addname = mFriendArrayList[position].name

            if (mbuttonId == true) {


                // 友達許可ダイアログを作成して表示
                AlertDialog.Builder(this).apply {
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

            } else {

                // 友達取り消しダイアログを作成して表示
                AlertDialog.Builder(this).apply {
                    setTitle("友達取り消し")
                    setMessage(addname + "の\n友達を取り消しますか？")
                    setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                        // OKをタップしたときの処理
                        NotFriendDialog()
                        Toast.makeText(context, "友達を取り消しました！", Toast.LENGTH_LONG).show()
                    })
                    setNegativeButton("Cancel", null)
                    show()
                }

                true

            }

        }*/

    }


    private fun alertCheck(getid: String, getname: String) {
        val alert_menu = arrayOf("現在地送信", "友達取消", "cancel")

        val alert = AlertDialog.Builder(this)
        alert.setTitle("")
        alert.setItems(alert_menu) { dialog, idx ->
            // リストアイテムを選択したときの処理
            // 上に移動
            if (idx == 0) {
                AlertDialog.Builder(this).apply {
                    setTitle("現在地送信")
                    setMessage(addname + "に現在地を送信しますか？")
                    setPositiveButton("送信", DialogInterface.OnClickListener { _, _ ->
                        location()
//                    mAdapter.friendSend = addid
//                    mAdapter.friendPosition = position
//                    mAdapter.notifyDataSetChanged()
                        Toast.makeText(context, "現在地を共有しました！", Toast.LENGTH_LONG).show()
                    })
                    setNegativeButton("Cancel", null)
                    show()
                }

            } else if (idx == 1) {
                // 友達取り消しダイアログを作成して表示
                AlertDialog.Builder(this).apply {
                    setTitle("友達取り消し")
                    setMessage(addname + "の\n友達を取り消しますか？")
                    setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                        // OKをタップしたときの処理
                        NotFriendDialog()
                        Toast.makeText(context, "友達を取り消しました！", Toast.LENGTH_LONG).show()
                    })
                    setNegativeButton("Cancel", null)
                    show()
                }

            } else {

            }
        }
        alert.show()
    }


    fun FriendListclick() {
        //友達リスト
        count = -1
        mbuttonId = false
        friendListButton.setBackgroundColor(YELLOW)
        permissionButton.setBackgroundColor(GRAY)
        mAdapter.getbuttonId(0)
        //友達のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mFriendArrayList.clear()
        mAdapter.setFriendArrayList(mFriendArrayList)
        mListView.adapter = mAdapter
        mFriendRef = mDatabaseReference.child(UsersPATH).child(user).child("friend")
        mFriendRef!!.addChildEventListener(mEventListener)

        //mFriendRef = mDatabaseReference.child(UsersPATH).child(user).child("location")
        //mFriendRef!!.addValueEventListener(mEventRecieveListener)

//        mFriendRef = mDatabaseReference.child(UsersPATH)
//        mFriendRef!!.addValueEventListener(mEventSendListener)


    }


    fun NotFriendclick() {
        count = -1
        //友達かもりすと
        mbuttonId = true
        permissionButton.setBackgroundColor(YELLOW)
        friendListButton.setBackgroundColor(GRAY)
        //友達のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mAdapter.getbuttonId(1)
        mFriendArrayList.clear()
        mAdapter.setFriendArrayList(mFriendArrayList)
        mListView.adapter = mAdapter
        mFriendRef = mDatabaseReference.child(UsersPATH).child(user).child("addfriend")
        mFriendRef!!.addChildEventListener(mEventListener)
        println(mbuttonId)
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
        mListView = this.findViewById(R.id.listView1)
        mAdapter = FriendsListAdapter(this)
        mFriendArrayList = ArrayList<Friends>()
        //mAdapter.notifyDataSetChanged()

        finish()
        val intent = Intent(applicationContext, FriendsListActivity::class.java)
        intent.putExtra("button", "1")
        startActivity(intent)

    }

    fun NotFriendDialog() {

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

        //ListViewの準備
        mListView = this.findViewById(R.id.listView1)
        mAdapter = FriendsListAdapter(this)
        mFriendArrayList = ArrayList<Friends>()
        //mAdapter.notifyDataSetChanged()


        finish()
        val intent = Intent(applicationContext, FriendsListActivity::class.java)
        intent.putExtra("button", "0")
        startActivity(intent)

    }

    fun location() {

//        val senduserRef = mDatabaseReference.child(UsersPATH).child(user).child("senduser_id")
//
//        //フレンド追加のデータ
//        val data = HashMap<String, String>()
//
//        //送信相手
//        data["user_id"] = addid
//        senduserRef.setValue(data)


        var mapView = MapView(this, "dj0zaiZpPWowWHRab050ODJyTyZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-")

        //MyLocationOverlayインスタンス作成
        _overlay = MyLocationOverlay(applicationContext, mapView)

        //現在位置取得開始
        _overlay!!.enableMyLocation()

        //位置が更新されると、地図の位置も変わるよう設定
        _overlay!!.runOnFirstFix(Runnable {
            if (mapView.mapController != null) {
                //現在位置を取得
                p = _overlay!!.myLocation

                keido = p.latitude
                ido = p.longitude
                println(keido)
                println(ido)

                val locationdataRef = mDatabaseReference.child(UsersPATH).child(addid).child("location")

                val datadouble = HashMap<String, String>()
                //現在地情報をFirebaseに保存
                datadouble["latitude"] = keido.toString()
                datadouble["longitude"] = ido.toString()
                datadouble["user_id"] = user

                locationdataRef.setValue(datadouble)

            }
        })

        //MapViewにMyLocationOverlayを追加。
        mapView.getOverlays().add(_overlay)

    }

}

