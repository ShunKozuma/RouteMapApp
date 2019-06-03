package kozuma.shun.techacademy.routemapapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_user_find.*

class UserFindActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabaseReference: DatabaseReference

    private var mFriendRef: DatabaseReference? = null

    private var id: String? = null

    private val mEventListner = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            //            val map = dataSnapshot.value as Map<String, String>
            id = dataSnapshot.key
            var name = dataSnapshot.child("name").getValue()

//            val friend_uid = dataSnapshot.key.toString()

//            val friend = User(name, friendsArrayList, latitude, longitude, uid, senduser_id)
//            val friend = Friends(friend_uid ,name)
            if(name != null){
                AddFindButton.visibility = View.VISIBLE
                FoundUserText.text = name.toString()

            }else{
                AddFindButton.visibility = View.INVISIBLE
                FoundUserText.text = "お探しのユーザーは\n見つかりません。"
            }

        }

        override fun onCancelled(p0: DatabaseError) {
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_find)

        val user = FirebaseAuth.getInstance().currentUser!!.uid

        AddFindButton.setOnClickListener {

            var loginname = ""
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val map = dataSnapshot.value as Map<String, String>
                    loginname = map["name"] ?: ""

                    //friend申請のパス
                    val addfriendRef = mDatabaseReference.child(UsersPATH).child(id.toString()).child("addfriend").child(user)
                    //フレンド追加のデータ
                    val data = HashMap<String, String>()
                    data["name"] = loginname
                    addfriendRef.setValue(data)

                    AddFindButton.visibility = View.INVISIBLE
                    FoundUserText.text = "登録致しました。"

                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }

            var mLoginRef: DatabaseReference = mDatabaseReference.child(UsersPATH).child(user)

            mLoginRef.addValueEventListener(postListener)



        }


        //Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().reference


        FindButton.setOnClickListener {
            if (FindEditText.text.toString().equals("")){
                FoundUserText.text = "ユーザーIDを入力してください。"
            }else{
                mFriendRef = mDatabaseReference.child(UsersPATH).child(FindEditText.text.toString())
                println(mFriendRef)
                mFriendRef!!.addListenerForSingleValueEvent(mEventListner)
            }
        }

    }
}
