package kozuma.shun.techacademy.routemapapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_user_find.*

class UserFindActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabaseReference: DatabaseReference

    private var mFriendRef: DatabaseReference? = null

    private val mEventListner = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
//            val map = dataSnapshot.value as Map<String, String>
//            val name = map["name"] ?: ""
//            val friend_uid = dataSnapshot.key.toString()
//
//            //val friend = User(name, friendsArrayList, latitude, longitude, uid, senduser_id)
//            val friend = Friends(friend_uid ,name)
            if(dataSnapshot != null){
                FoundUserText.text = "います"
            }else{
                FoundUserText.text = "お探しのユーザーは見つかりません。"
            }

        }

        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_find)


        //Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        FindButton.setOnClickListener {
            mFriendRef = mDatabaseReference.child(UsersPATH)
            mFriendRef!!.addChildEventListener(mEventListner)
        }

    }
}
