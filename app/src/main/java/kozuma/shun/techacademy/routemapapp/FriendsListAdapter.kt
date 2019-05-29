package kozuma.shun.techacademy.routemapapp

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_friends_list.*

class FriendsListAdapter(context: Context): BaseAdapter() {
    private var mLayoutInflater: LayoutInflater
    private var mFriendArrayList = ArrayList<Friends>()
    var context: Context? = null
    private var buttonId: Int = 0

    //ダイアログFirebase
    private lateinit var mDatabaseReference: DatabaseReference
    private var mAddFriendRef: DatabaseReference? = null

    //addボタン選択ユーザのIDとPassを所得
    private lateinit var addname: String
    private lateinit var addid: String

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    fun getbuttonId(id: Int){
        buttonId = id
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

        if ( buttonId == 0) {
            if(convertView == null){
                convertView = mLayoutInflater.inflate(R.layout.list_friends, parent, false)
            }

            val deleteButton = convertView!!.findViewById<View>(R.id.deleteButton) as Button
            deleteButton.setOnClickListener {
                //選択ユーザのIDとPassを所得
                addid = mFriendArrayList[position].friend_uid
                addname = mFriendArrayList[position].name
                println(addid)
                println(addname)
                // ダイアログを作成して表示
                AlertDialog.Builder(context!!).apply {
                    setTitle("友達取消")
                    setMessage(addname+"との\n友達を取り消しますか？")
                    setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                        // OKをタップしたときの処理
                        deleteFriendDialog()
                        Toast.makeText(context, "友達を取り消しました！", Toast.LENGTH_LONG).show()

                    })
                    setNegativeButton("Cancel", null)
                    show()
                }
            }

            val nameText = convertView!!.findViewById<View>(R.id.nameTextView) as TextView
            nameText.text = mFriendArrayList[position].name

        }else{
            if(convertView == null){
                convertView = mLayoutInflater.inflate(R.layout.list_addfriends, parent, false)
            }

            val addButton = convertView!!.findViewById<View>(R.id.addButton) as Button
            addButton.setOnClickListener {
                //選択ユーザのIDとPassを所得
                addid = mFriendArrayList[position].friend_uid
                addname = mFriendArrayList[position].name
                println(addid)
                println(addname)
                // ダイアログを作成して表示
                AlertDialog.Builder(context!!).apply {
                    setTitle("友達申請")
                    setMessage(addname+"の\n友達申請を許可しますか？")
                    setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                        // OKをタップしたときの処理
                        addFriendDialog()
                        Toast.makeText(context, "申請を許可しました！", Toast.LENGTH_LONG).show()

                    })
                    setNegativeButton("Cancel", null)
                    show()
                }
            }

            val nameText = convertView!!.findViewById<View>(R.id.nameTextView) as TextView
            nameText.text = mFriendArrayList[position].name

        }

        return convertView
    }

    fun setFriendArrayList(friendArrayList: ArrayList<Friends>){
        mFriendArrayList = friendArrayList
    }


    fun addFriendDialog(){

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

    fun deleteFriendDialog(){

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