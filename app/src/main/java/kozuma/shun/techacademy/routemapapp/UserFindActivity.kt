package kozuma.shun.techacademy.routemapapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_user_find.*

class UserFindActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabaseReference: DatabaseReference

    private var mFriendRef: DatabaseReference? = null

    private var id: String? = null

    private var name: Any? = null

    private lateinit var animation: Animation

    private val mEventListner = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            //            val map = dataSnapshot.value as Map<String, String>
            id = dataSnapshot.key
            name = dataSnapshot.child("name").getValue()

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


                        //フレンド申請を相手にだす
                        val data = HashMap<String, String>()
                        data["name"] = loginname
                        addfriendRef.setValue(data)

                        AddFindButton.visibility = View.INVISIBLE
                        FoundUserText.text = "登録致しました。"


                        //自身の友達として追加する
                        //ログインのユーザID
                        val user = FirebaseAuth.getInstance().currentUser!!.uid
                        //リファレンス
                        mDatabaseReference = FirebaseDatabase.getInstance().reference
                        //friend追加のパス
                        val addRef = mDatabaseReference.child(UsersPATH).child(user).child("friend").child(FindEditText.text.toString())
                        //フレンド追加のデータ
                        val adddata = HashMap<String, String>()
                        adddata["name"] = name.toString()
                        addRef.setValue(adddata)

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

        animation = AnimationUtils.loadAnimation(this, R.anim.translate_animation)

    }

}
