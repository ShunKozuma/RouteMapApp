package kozuma.shun.techacademy.routemapapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_friends_list.*

class FriendsPermissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_permission)

/*
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
        */

    }
}
