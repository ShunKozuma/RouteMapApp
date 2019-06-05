package kozuma.shun.techacademy.routemapapp

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.co.yahoo.android.maps.MapActivity
import jp.co.yahoo.android.maps.MapView
import jp.co.yahoo.android.maps.MyLocationOverlay
import kotlinx.android.synthetic.main.activity_main.*
import jp.co.yahoo.android.maps.GeoPoint
import jp.co.yahoo.android.maps.PinOverlay
import jp.co.yahoo.android.maps.OverlayItem
import jp.co.yahoo.android.maps.PopupOverlay
import jp.co.yahoo.android.maps.routing.RouteOverlay


class MainActivity : MapActivity(), RouteOverlay.RouteOverlayListener {
    override fun errorRouteSearch(p0: RouteOverlay?, p1: Int): Boolean {
        return false
    }

    override fun finishRouteSearch(p0: RouteOverlay?): Boolean {
        return false
    }

    private var _overlay: MyLocationOverlay? = null //現在地
    private lateinit var mDatabaseReference: DatabaseReference
    private var mLocationRef: DatabaseReference? = null

    //ログイン中のユーザID
    val user = FirebaseAuth.getInstance().currentUser!!.uid

    var context: Context? = null

    //var longitude: String? = null
    //var latitude: String? = null

    lateinit var Map: MapView


    //現在地のデータ
    private lateinit var p: GeoPoint

    //受信相手のデータ
    var keido: Int = 0
    var ido: Int = 0



    private val mEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            val map = dataSnapshot.value as Map<String, String>
            val user_id = map["user_id"] ?: ""
            val latitude = map["latitude"] ?: ""
            val longitude = map["longitude"] ?: ""

            println(user_id)
            println(latitude)
            println(longitude)

            //経度緯度情報に小数点がふくまれるGeoPointはInt型の引数のため変更
            //小数点を削除（置換）し、Int型にして格納
            keido = latitude.replace(".", "").toInt()
            println(keido)
            ido = longitude.replace(".", "").toInt()

            //相手の現在地をピンで表示
            val mid = GeoPoint(keido, ido)
            val pinOverlay = PinOverlay(PinOverlay.PIN_VIOLET)
            map()
            Map.getOverlays().add(pinOverlay)

            //地図移動
            Map.mapController.animateTo(mid)


            val popupOverlay = object : PopupOverlay() {
                override fun onTap(item: OverlayItem?) {
                    //ポップアップをタッチした際の処理
                    //友達に位置情報の共有ダイアログ
                    AlertDialog.Builder(context as Activity).apply {
                        setTitle("ルート探索")
                        setMessage("OOさんへの" + "ルートを探索しますか？")
                        setPositiveButton("探索", DialogInterface.OnClickListener { _, _ ->
                            RouteFind()
                            Toast.makeText(context, "ルートを表示致しました！", Toast.LENGTH_LONG).show()
                        })

                        setNegativeButton("Cancel", null)
                        show()
                    }

                }
            }
            Map.getOverlays().add(popupOverlay);
            pinOverlay.setOnFocusChangeListener(popupOverlay);
            pinOverlay.addPoint(mid,"OOさん","東京ミッドタウンについて");


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

    }


    fun map(){

        //地図を表示
        Map = MapView(this,"dj0zaiZpPWowWHRab050ODJyTyZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-")
        setContentView(Map)

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
            MyLocationData()
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

        //共有中のユーザー表示
        val shareUser = TextView(this)
        shareUser.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        shareUser.text = "現在地を受信中"
        shareUser.gravity = Gravity.TOP
        shareUser.setBackgroundColor(Color.GRAY)
        shareUser.setOnClickListener {
            locationdata()
        }
        Map.addView(shareUser)

        Map.addView(layout)

    }

    fun RouteFind(){

        //RouteOverlay作成
        val routeOverlay = RouteOverlay(this, "アプリケーションID")

        //出発地ピンの吹き出し設定
        routeOverlay.setStartTitle("現在地")

        //目的地ピンの吹き出し設定
        routeOverlay.setGoalTitle("OOさん")



        var mylat = p.latitude.toString()
        var mylon = p.longitude.toString()
        //出発地、目的地、移動手段を設定
        routeOverlay.setRoutePos(
            GeoPoint(mylat.replace(".", "").toInt(), mylon.replace(".", "").toInt()),
            GeoPoint(keido, ido),
            RouteOverlay.TRAFFIC_WALK
        )

        //RouteOverlayListenerの設定
        routeOverlay.setRouteOverlayListener(this)

        //検索を開始
        routeOverlay.search()

        //MapViewにRouteOverlayを追加
        Map.getOverlays().add(routeOverlay)

    }

    fun MyLocationData(){
        //MyLocationOverlayインスタンス作成
        _overlay = MyLocationOverlay(applicationContext, Map)

        //現在位置取得開始
        _overlay!!.enableMyLocation()

        //位置が更新されると、地図の位置も変わるよう設定
        _overlay!!.runOnFirstFix(Runnable {
            if (Map.mapController != null) {
                //現在位置を取得
                p = _overlay!!.myLocation

                //地図移動
                Map.mapController.animateTo(p)

            }
        })

        //MapViewにMyLocationOverlayを追加。
        Map.overlays.add(_overlay)

    }


}

