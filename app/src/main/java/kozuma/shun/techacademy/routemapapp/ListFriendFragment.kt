package kozuma.shun.techacademy.routemapapp

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
import jp.co.yahoo.android.maps.GeoPoint
import jp.co.yahoo.android.maps.MapView
import jp.co.yahoo.android.maps.MyLocationOverlay


class ListFriendFragment : Fragment() {

    lateinit var mapView: MapView

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mListViews: ListView
    private lateinit var mListView: ListView
    private lateinit var mFriendArrayList: ArrayList<Friends>
    private lateinit var mAdapter: FriendsListAdapter

    //ログイン中のユーザID
    val user = FirebaseAuth.getInstance().currentUser!!.uid

    private var mFriendRef: DatabaseReference? = null
    private var mFriendRecieveRef: DatabaseReference? = null
    private var mFriendSendRef: DatabaseReference? = null

    //addボタン選択ユーザのIDとPassを所得
    private lateinit var addname: String
    private lateinit var addid: String

    private var mbuttonId: Boolean = false

    //this contextの代入
    var thisis: Context? = null

    //現在地取得
    private var _overlay: MyLocationOverlay? = null
    private var keido = 0.0 //現在地　経度
    private var ido = 0.0 //現在地　緯度
    private var p: GeoPoint = GeoPoint(0, 0)//現在地の取得

    var text = mutableListOf<String>()

    var count = -1

    var counts = -1

    lateinit var friend: Friends


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(kozuma.shun.techacademy.routemapapp.R.layout.list_friend_fragment, container, false)
        mListViews = view.findViewById(R.id.listView1)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_friends_list)
        println("Createした" + count)

        mapView = MapView(context as Activity?, "dj0zaiZpPWowWHRab050ODJyTyZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-")

        thisis = context

        //Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        //ListViewの準備
        mListView = mListViews
        mAdapter = FriendsListAdapter(thisis!!)
        mFriendArrayList = ArrayList<Friends>()
        //mAdapter.notifyDataSetChanged()

        FriendListclick()

        mListView.setOnItemClickListener { parent, view, position, id ->
            addid = mFriendArrayList[position].friend_uid
            addname = mFriendArrayList[position].name

            // アラートダイアログ
            alertCheck(addid, addname)

        }

        mListView.choiceMode = ListView.CHOICE_MODE_NONE


    }

    private fun alertCheck(getid: String, getname: String) {
        val alert_menu = arrayOf("現在地送信", "友達取消", "キャンセル")

        val alert = AlertDialog.Builder(thisis!!)
        alert.setTitle("")
        alert.setItems(alert_menu) { dialog, idx ->
            // リストアイテムを選択したときの処理
            // 上に移動
            if (idx == 0) {
                AlertDialog.Builder(thisis!!).apply {
                    setTitle("現在地送信")
                    setMessage(addname + "に現在地を送信しますか？")
                    setPositiveButton("送信", DialogInterface.OnClickListener { _, _ ->
                        location()
                        activity!!.finish()
                        val intent = Intent(activity, ListFriendActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(context, "現在地を共有しました！", Toast.LENGTH_LONG).show()
                    })
                    setNegativeButton("Cancel", null)
                    show()
                }

            } else if (idx == 1) {
                // 友達取り消しダイアログを作成して表示
                AlertDialog.Builder(thisis!!).apply {
                    setTitle("友達取り消し")
                    setMessage(addname + "の\n友達を取り消しますか？")
                    setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                        // OKをタップしたときの処理
                        NotFriendDialog()
                        activity!!.finish()
                        val intent = Intent(activity, ListFriendActivity::class.java)
                        //intent.putExtra("TAB_DATA", "2")
                        startActivity(intent)
                        //mAdapter.notifyDataSetChanged()
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


    private val mEventRecieveListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            counts++
            println("カウント"+counts)
            if (dataSnapshot.value != null  && counts <= mFriendArrayList.size - 1) {
                val map = dataSnapshot.value as Map<String, String>
                //val user_id = map["user_id"] ?: ""
                val user_id = ""
                for (user_id in map.keys) {
                    println("nums" + user_id + counts)
                    //mAdapter.friendRecieve = numm
                    if(mFriendArrayList[counts].friend_uid == user_id){
                        mFriendArrayList[counts].recieveBoolean = true
                        mAdapter.notifyDataSetChanged()
                    }

                }

            }

        }

    }

    private val mEventSendListener = object : ValueEventListener {

        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {

            count++
//            println("配列の行数" + (mFriendArrayList.size - 1))
//                    && count <= mFriendArrayList.size - 1
            if (dataSnapshot.value != null) {
                val map = dataSnapshot.value as Map<String, String>
                //val user_id = map["user_id"] ?: ""
                //val frienduser_id = dataSnapshot.key?: ""


                if (dataSnapshot.value.toString().contains(user)  && count <= mFriendArrayList.size - 1) {
                    mFriendArrayList[count].sendBoolean = true
                    println("送信：${mFriendArrayList[count].name}${mFriendArrayList[count].sendBoolean}")
                    mAdapter.notifyDataSetChanged()
                }

            }


            //mAdapter.notifyDataSetChanged()

        }

    }


    private val mEventListener = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

            val map = dataSnapshot.value as Map<String, String>
            val name = map["name"] ?: ""
            val friend_uid = dataSnapshot.key ?: ""
            val friend = Friends(friend_uid, name, null, null)
            text.add(friend_uid)

            mFriendArrayList.add(friend)

            mAdapter.notifyDataSetChanged()

            mFriendSendRef = mDatabaseReference.child(UsersPATH).child(friend_uid).child("location")
            mFriendSendRef!!.addValueEventListener(mEventSendListener)

            mFriendRecieveRef = mDatabaseReference.child(UsersPATH).child(user).child("location")
            mFriendRecieveRef!!.addValueEventListener(mEventRecieveListener)

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
        mAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        println("Destroyした" + count)
    }

    override fun onPause() {
        super.onPause()

    }


    fun FriendListclick() {
        //友達リスト

        mbuttonId = false
        mAdapter.getbuttonId(0)
        //友達のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mFriendArrayList.clear()
        mAdapter.setFriendArrayList(mFriendArrayList)
        mListView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        mFriendRef = mDatabaseReference.child(UsersPATH).child(user).child("friend")
        mFriendRef!!.addChildEventListener(mEventListener)



//        mFriendRecieveRef = mDatabaseReference.child(UsersPATH).child(user).child("location")
//        mFriendRecieveRef!!.addValueEventListener(mEventRecieveListener)


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

    }

    fun location() {


        //MyLocationOverlayインスタンス作成
        _overlay = MyLocationOverlay(thisis!!, mapView)

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

                val locationdataRef = mDatabaseReference.child(UsersPATH).child(addid).child("location").child(user)

                val datadouble = HashMap<String, String>()
                //現在地情報をFirebaseに保存
                datadouble["latitude"] = keido.toString()
                datadouble["longitude"] = ido.toString()
                //datadouble["user_id"] = user

                locationdataRef.setValue(datadouble)

            }
        })


    }
}

