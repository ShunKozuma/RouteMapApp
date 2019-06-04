package kozuma.shun.techacademy.routemapapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.co.yahoo.android.maps.MapActivity
import jp.co.yahoo.android.maps.MapView
import jp.co.yahoo.android.maps.MyLocationOverlay
import kotlinx.android.synthetic.main.activity_main.*
import jp.co.yahoo.android.maps.GeoPoint
import jp.co.yahoo.android.maps.PinOverlay





class MainActivity : MapActivity() {

    private var _overlay: MyLocationOverlay? = null //現在地
    private lateinit var mDatabaseReference: DatabaseReference
    private var mLocationRef: DatabaseReference? = null

    //ログイン中のユーザID
    val user = FirebaseAuth.getInstance().currentUser!!.uid

    var context: Context? = null

    var longitude: String? = null
    var latitude: String? = null


    private val mEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            val map = dataSnapshot.value as Map<String, String>
            val user_id = map["user_id"] ?: ""
            longitude = map["longitude"] ?: ""
            latitude = map["latitude"] ?: ""

            println(user_id)
            println(longitude)
            println(latitude)

        }


        override fun onCancelled(p0: DatabaseError) {
        }
    }


    override fun isRouteDisplayed(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        context = this

       map()
    }

    fun locationdata(){

        //Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        mLocationRef = mDatabaseReference.child(UsersPATH).child(user).child("location")
        mLocationRef!!.addValueEventListener(mEventListener)


        var keido = latitude!!.toInt()*1000000
        println(keido)
        var ido = longitude!!.toInt()*1000000

        var mapView = MapView(context as Activity?,"dj0zaiZpPWowWHRab050ODJyTyZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-")
        setContentView(mapView)

        //val mid = GeoPoint(latitude.toInt()*1000000,longitude.toInt()*1000000)
        val mid = GeoPoint(keido*1000000,ido*1000000)
        val pinOverlay = PinOverlay(PinOverlay.PIN_VIOLET)
        map()
        mapView.getOverlays().add(pinOverlay)
        pinOverlay.addPoint(mid, null)
    }


    fun map(){

        //地図を表示
        var mapView = MapView(this,"dj0zaiZpPWowWHRab050ODJyTyZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-")
        setContentView(mapView)

        val layout = LinearLayout(this)
        layout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        // 右部に配置
        layout.gravity = Gravity.RIGHT or Gravity.BOTTOM
        // Verticalに設定する
        layout.orientation = LinearLayout.VERTICAL

        layout.setPadding(0,0,20,30)


        //AR表示ボタンの追加
        val ArButton = Button(this)
        ArButton.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        ArButton.text = "AR表示"
        layout.addView(ArButton)


        //現在地を表示するボタン
        val currentButton = FloatingActionButton(this)
        currentButton.setOnClickListener {

            /*
            //MyLocationOverlayインスタンス作成
            _overlay = MyLocationOverlay(applicationContext, mapView)

            //現在位置取得開始
            _overlay!!.enableMyLocation()

            //位置が更新されると、地図の位置も変わるよう設定
            _overlay!!.runOnFirstFix(Runnable {
                if (mapView.mapController != null) {
                    //現在位置を取得
                    var p = _overlay!!.myLocation

                    //地図移動
                    mapView.mapController.animateTo(p)

                }
            })

            //MapViewにMyLocationOverlayを追加。
            mapView.overlays.add(_overlay)
            */

            locationdata()

        }

        //現在地画像
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.now)
        currentButton.setImageBitmap(bitmap)
        //ボタン追加
        layout.addView(currentButton)



        //友達リストボタンの追加
        val fab = FloatingActionButton(this)
        fab.setOnClickListener {
            val intent = Intent(applicationContext, FriendsListActivity::class.java)
            intent.putExtra( "button", "0" )
            startActivity(intent)
        }

        val fabimage = BitmapFactory.decodeResource(resources, R.drawable.plus)
        fab.setImageBitmap(fabimage)
        layout.addView(fab)

        mapView.addView(layout)

    }


}

