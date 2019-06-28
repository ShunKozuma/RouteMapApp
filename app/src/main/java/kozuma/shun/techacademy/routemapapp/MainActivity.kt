package kozuma.shun.techacademy.routemapapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.co.yahoo.android.maps.*
import jp.co.yahoo.android.maps.ar.ARController
import jp.co.yahoo.android.maps.ar.ARControllerListener
import jp.co.yahoo.android.maps.navi.NaviController
import jp.co.yahoo.android.maps.routing.RouteOverlay
import jp.co.yahoo.android.maps.weather.WeatherOverlay
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), RouteOverlay.RouteOverlayListener, NaviController.NaviControllerListener,
    ARControllerListener, WeatherOverlay.WeatherOverlayListener, NavigationView.OnNavigationItemSelectedListener {


    private lateinit var mToolbar: Toolbar

    private val PERMISSIONS_REQUEST_CODE = 100


    private var _overlay: MyLocationOverlay? = null //現在地
    private lateinit var mDatabaseReference: DatabaseReference
    private var mLocationRef: DatabaseReference? = null

    //ログイン中のユーザID
    lateinit var user: String

    var context: Context? = null

    lateinit var Map: MapView

    //現在地のデータ
    private lateinit var p: GeoPoint

    //受信相手のデータ
    var keido: Int = 0
    var ido: Int = 0

    //ボタン配置
    lateinit var currentButton: FloatingActionButton

    lateinit var fab: FloatingActionButton

    lateinit var layout: LinearLayout

    //Naviのインスタンス
    lateinit var naviController: NaviController

    //ARのインスタンス
    lateinit var arController: ARController

    //ARをよぶ
    var arjudge = false

    //ログインユーザーの名前
    var user_name: String? = null
    lateinit var navUsername: TextView

    //共有中の友達
    var nowfriend = mutableListOf<String>()


    var datakeido = mutableListOf<Int>()
    var dataido = mutableListOf<Int>()

    var count = -1


    //popupのアダプター
    lateinit var adapter: ArrayAdapter<String>


    private lateinit var Topupanimation: Animation
    private lateinit var Topdownanimation: Animation
    private lateinit var Endupanimation: Animation
    private lateinit var Enddownanimation: Animation
    var touch: Boolean = true
    var touchID = 0


    //タイマー
    private var mTimer: Timer? = null

    // タイマー用の時間のための変数
    private var mTimerSec = 0.0

    private var mHandler = Handler()

    var nowLocationBoolean = true


    //WeatherOverlayのインターフェース
    override fun finishUpdateWeather(p0: WeatherOverlay?) {

    }

    override fun errorUpdateWeather(p0: WeatherOverlay?, p1: Int) {

    }

    //ARControllerのインターフェース
    override fun ARControllerListenerOnPOIPick(p0: Int) {

    }

    //NaviControllerのインターフェース

    //現在位置が更新された場合
    override fun onLocationChanged(arg0: NaviController): Boolean {


        //目的地までの残りの距離
        val rema_dist = naviController.totalDistance

        //目的地までの残りの時間
        val rema_time = naviController.totalTime

        //出発地から目的地までの距離
        val total_dist = naviController.distanceOfRemainder

        //出発地から目的地までの時間
        val total_time = naviController.timeOfRemainder

        //現在位置
        var location = naviController.location

        return false
    }

    //現在位置取得エラーが発生した場合
    override fun onLocationTimeOver(arg0: NaviController): Boolean {
        return false
    }

    //現在位置の精度が悪い場合
    override fun onLocationAccuracyBad(arg0: NaviController): Boolean {
        return false
    }

    //ルートから外れたと判断された場合
    override fun onRouteOut(arg0: NaviController): Boolean {
        return false
    }

    //目的地に到着した場合
    override fun onGoal(arg0: NaviController): Boolean {

        //ARの停止処理
        // arController.onPause()

        //案内処理を継続しない場合は停止させる
        naviController.stop()

        //ARControllerをNaviControllerから削除
        naviController.setARController(null)

        //案内処理を継続しない場合は停止させる
        //naviController.stop()
        return false
    }


    //Routeのインターフェース
    //ルート検索が正常に終了しなかった場合
    override fun errorRouteSearch(arg0: RouteOverlay, arg1: Int): Boolean {
        return false
    }


    //ルート検索が正常に終了した場合
    override fun finishRouteSearch(routeOverlay: RouteOverlay): Boolean {

        //NaviControllerを作成しRouteOverlayインスタンスを設定
        naviController = NaviController(this, routeOverlay)

        //MapViewインスタンスを設定
        naviController.setMapView(Map)
        //NaviControllerListenerを設定
        naviController.setNaviControlListener(this)


        //案内処理を開始
        naviController.start()


        if (arjudge) {

            //横向き固定
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


            //ARControllerインスタンス作成
            arController = ARController(this, this)

            //ARControllerをNaviControllerに設定
            naviController.setARController(arController)

            //案内処理を開始
            naviController.start()


        }

        return false
    }

    private val mEventname = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val map = dataSnapshot.value as Map<String, String>
            user_name = map["name"] ?: ""

            println(user_name)
            navUsername.text = user_name
            println(navUsername.text)


        }

    }

    private val mEvent = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        @SuppressLint("RestrictedApi")
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.value != null) {
                count++
                val map = dataSnapshot.value as Map<String, String>
                val user_id = dataSnapshot.key
                val nowfriendname = map["name"] ?: ""

                nowfriend.add(nowfriendname)

                //リストアラート
                val alert_menu = arrayOf(nowfriendname)


                //相手の現在地をピンで表示
                //val mid = GeoPoint(keido, ido)
                val mid = GeoPoint(datakeido[count], dataido[count])
                val pinOverlay = PinOverlay(PinOverlay.PIN_VIOLET)
                Map.getOverlays().add(pinOverlay)

                pinOverlay.addPoint(mid, nowfriendname, "a")

                val fkeido = datakeido[count]
                val fido = dataido[count]

                val popupOverlay = object : PopupOverlay() {
                    override fun onTap(item: OverlayItem?) {
                        //ポップアップをタッチした際の処理
                        //友達に位置情報の共有ダイアログ
                        AlertDialog.Builder(context as Activity).apply {
                            setTitle("ルート探索")
                            setMessage(nowfriendname + "ルートを探索しますか？")
                            setPositiveButton("探索", DialogInterface.OnClickListener { _, _ ->

                                //RouteOverlay作成
                                val routeOverlay =
                                    RouteOverlay(context, "dj0zaiZpPWowWHRab050ODJyTyZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-")

                                //出発地ピンの吹き出し設定
                                routeOverlay.setStartTitle("現在地")

                                //目的地ピンの吹き出し設定
                                routeOverlay.setGoalTitle(nowfriendname)

                                //MyLocationOverlayインスタンス作成
                                _overlay = MyLocationOverlay(applicationContext, Map)

                                //現在位置取得開始
                                _overlay!!.enableMyLocation()

                                //位置が更新されると、地図の位置も変わるよう設定
                                _overlay!!.runOnFirstFix(Runnable {
                                    if (Map.mapController != null) {
                                        //現在位置を取得
                                        p = _overlay!!.myLocation


                                        var mylat = p.latitude.toString()
                                        var mylon = p.longitude.toString()
                                        println("データ" + fkeido + "緯度" + fido)
                                        //出発地、目的地、移動手段を設定
                                        routeOverlay.setRoutePos(
                                            GeoPoint(mylat.replace(".", "").toInt(), mylon.replace(".", "").toInt()),
                                            GeoPoint(fkeido, fido),
                                            RouteOverlay.TRAFFIC_WALK
                                        )

                                        //RouteOverlayListenerの設定
                                        routeOverlay.setRouteOverlayListener(this@MainActivity)

                                        //検索を開始
                                        routeOverlay.search()

                                        //MapViewにRouteOverlayを追加
                                        Map.getOverlays().add(routeOverlay)

                                        //経由点ピンを非表示
                                        routeOverlay.setRoutePinVisible(false)

                                    }
                                })


                                Toast.makeText(context, "ルートを表示致しました！", Toast.LENGTH_LONG).show()
                            })

                            setNegativeButton("Cancel", null)
                            show()
                        }

                    }
                }

                Map.getOverlays().add(popupOverlay)
                pinOverlay.setOnFocusChangeListener(popupOverlay)

                recievebutton.visibility = View.VISIBLE

            }


        }

    }


    private val mEventfriendListener = object : ChildEventListener {
        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
            val map = dataSnapshot.value as Map<String, String>
            val friend_id = dataSnapshot.key ?: ""

            mLocationRef = mDatabaseReference.child(UsersPATH).child(user).child("location").child(friend_id)
            mLocationRef!!.addValueEventListener(mEventListener)
        }

        override fun onChildRemoved(p0: DataSnapshot) {

        }

        override fun onCancelled(p0: DatabaseError) {

        }


    }


    private val mEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            if (dataSnapshot.value != null) {

                val map = dataSnapshot.value as Map<String, String>
                //val user_id = map["user_id"] ?: ""
                val user_id = dataSnapshot.key ?: ""
                val latitude = map["latitude"] ?: ""
                val longitude = map["longitude"] ?: ""


                //経度緯度情報に小数点がふくまれるGeoPointはInt型の引数のため変更
                //小数点を削除（置換）し、Int型にして格納
                keido = latitude.replace(".", "").toInt()
                println(keido)
                ido = longitude.replace(".", "").toInt()
                datakeido.add(keido)
                dataido.add(ido)

                mLocationRef = mDatabaseReference.child(UsersPATH).child(user).child("friend").child(user_id)
                mLocationRef!!.addValueEventListener(mEvent)
            }

        }


        override fun onCancelled(p0: DatabaseError) {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Map = MapView(this, "dj0zaiZpPWowWHRab050ODJyTyZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-")
        setContentView(R.layout.activity_main)


        //FrameLayoutにマップ表示
        val mainmap = findViewById<FrameLayout>(R.id.maps)
        mainmap.addView(Map)


        user = FirebaseAuth.getInstance().currentUser!!.uid

        //Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        //ログインユーザーの名前呼び出し
        mLocationRef = mDatabaseReference.child(UsersPATH).child(user)
        mLocationRef!!.addListenerForSingleValueEvent(mEventname)


        context = this


        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nowfriend)

        recievebutton.setOnClickListener {
            //val intent = Intent(applicationContext, PopupListActivity::class.java)
            //startActivity(intent)
            displayPopupWindow(it)
        }

        //NavigationdrawerのユーザID
        val navigation: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigation.getHeaderView(0)
        val navUserid: TextView = headerView.findViewById(R.id.nav_id)
        navUsername= headerView.findViewById(R.id.nav_name)
        //navUsername.text = user_name
        navUserid.text = user

        navUserid.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, navUserid.text.toString())
            startActivity(intent)
        }

        ///レイアウト
        layout = LinearLayout(this)
        layout.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        // 右部に配置
        layout.gravity = Gravity.RIGHT or Gravity.BOTTOM
        // Verticalに設定する
        layout.orientation = LinearLayout.VERTICAL

        layout.setPadding(0, 0, 20, 30)


        //現在地を表示するボタン
        currentButton = FloatingActionButton(this)
        currentButton.setOnClickListener {
            MyLocationData()
        }
        //現在地画像
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.now)
        currentButton.setImageBitmap(bitmap)
        //ボタン追加
        layout.addView(currentButton)


        //友達リストボタンの追加
        //val fab = FloatingActionButton(this)
        fab = FloatingActionButton(this)
        fab.setOnClickListener {
            val intent = Intent(applicationContext, ListFriendActivity::class.java)
            intent.putExtra("button", "0")
            startActivity(intent)
        }

        val fabimage = BitmapFactory.decodeResource(resources, R.drawable.plus)
        fab.setImageBitmap(fabimage)
        layout.addView(fab)

        Map.addView(layout)


        // パーミッションの許可状態を確認する
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_CODE)
            }
        }

        // 位置情報を管理している LocationManager のインスタンスを生成
        var locationManager: LocationManager? = getSystemService(LOCATION_SERVICE) as LocationManager
        var locationProvider: String = ""

        if (null !== locationManager) {
            // GPSが利用可能になっているかどうかをチェック
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationProvider = LocationManager.GPS_PROVIDER
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationProvider = LocationManager.NETWORK_PROVIDER
            } else {
                // いずれも利用可能でない場合は、GPSを設定する画面に
                //友達に位置情報の共有ダイアログ
                AlertDialog.Builder(this).apply {
                    //setTitle("位置情報の有効化")
                    setMessage("デバイスの位置情報をONにしてください。")
                    setPositiveButton("設定画面へ", DialogInterface.OnClickListener { _, _ ->
                        val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(settingsIntent)

                    })

                    setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(context, "現在地を扱う機能が正しく動作しません。", Toast.LENGTH_LONG).show()
                    })
                    show()
                }



                return
            }
        }

        //WeatherOverlayを作成
        val weatherOverlay = WeatherOverlay(this)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        // トグルスイッチの生成
        val switch1 = Switch(this)
        // トグルスイッチにイベントを設定
        switch1.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                //MapViewにWeatherOverlayを追加


                //WeatherOverlayListenerを設定
                weatherOverlay.setWeatherOverlayListener(this)
                Map.getOverlays().add(weatherOverlay)
            } else {
                Map.getOverlays().remove(weatherOverlay)
            }
        }
        // トグルスイッチを配置するメニューを取得
        val menuItem2 = navigationView.menu.findItem(R.id.nav_rain)
        // 取得したメニューにトグルスイッチを設定
        menuItem2.actionView = switch1

        navigationView.setNavigationItemSelectedListener(this)




        Topupanimation = AnimationUtils.loadAnimation(this, R.anim.translate_animation)
        Topdownanimation = AnimationUtils.loadAnimation(this, R.anim.translate_downanimation)
        Endupanimation = AnimationUtils.loadAnimation(this, R.anim.rightend_upanimation)
        Enddownanimation = AnimationUtils.loadAnimation(this, R.anim.rightend_downanimation)


    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        super.dispatchTouchEvent(ev)


        when (ev!!.action) {
            MotionEvent.ACTION_DOWN -> {
                mTimer = Timer()
                //println(ev.x.toString() + ">=" + (currentButton.x-20) + "&&" + ev.y + ">=" + currentButton.top + 30)
                when (touchID) {
                    0 -> {
                        // if (ev.x >= recievebutton.x && ev.y <= recievebutton.height +recievebutton.height/2 ) {
                        if (ev.x >= recievebutton.x && ev.y <= recievebutton.height + recievebutton.height / 2 || ev.x >= currentButton.x - 20 && ev.y >= currentButton.top + 30) {

                        } else {
                            // タイマーの作成
                            // タイマーの始動
                            //mTimer = Timer()
                            mTimer!!.schedule(object : TimerTask() {
                                override fun run() {
                                    mTimerSec += 0.1
                                    mHandler.post {
                                        println(mTimerSec)
                                        if (mTimerSec >= 0.7) {
                                            recievebutton.startAnimation(Topupanimation)
                                            layout.startAnimation(Enddownanimation)
                                            touch = false
                                            touchID = 1
                                            mTimer!!.cancel()
                                            mTimerSec = 0.0
                                        }
                                    }
                                }
                            }, 0, 100) // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定


                        }

                    }
                    1 -> {

                        //mTimer = Timer()
                        mTimer!!.schedule(object : TimerTask() {
                            override fun run() {
                                mTimerSec += 0.1
                                mHandler.post {
                                    println(mTimerSec)
                                    if (mTimerSec >= 0.7) {
                                        recievebutton.startAnimation(Topdownanimation)
                                        layout.startAnimation(Endupanimation)
                                        touch = true
                                        touchID = 0
                                        mTimer!!.cancel()
                                        mTimerSec = 0.0
                                    }
                                }
                            }
                        }, 0, 100) // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定


                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                mTimer!!.cancel()
                mTimerSec = 0.0
            }

        }

        return false

    }


    fun displayPopupWindow(anchorView: View) {
        val popup = PopupWindow(this)
        val poplayout: View = layoutInflater.inflate(R.layout.activity_popup_list, null)
        popup.setContentView(poplayout)
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
        popup.setWidth(400)
        // Closes the popup window when touch outside of it - when looses focus
        // 背景設定
        popup.setBackgroundDrawable(getResources().getDrawable(R.color.white))
        popup.setOutsideTouchable(true)
        popup.setFocusable(true)

        var listView: ListView = poplayout.findViewById(R.id.listView)
        //val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nowfriend)
        listView.adapter = adapter

        //ListViewを長押ししたときの処理
        listView.setOnItemClickListener { parent, _, position, _ ->
            // アラートダイアログ
            alertCheck(datakeido[position], dataido[position], position)

            true
        }
        // Show anchored to button
        popup.showAsDropDown(anchorView)
    }

    private fun alertCheck(popkeido: Int, popido: Int, position: Int) {
        val alert_menu = arrayOf("マップ移動", "ルート探索", "AR表示", "キャンセル")

        val alert = AlertDialog.Builder(this)
        alert.setTitle("")
        alert.setItems(alert_menu) { dialog, idx ->
            // リストアイテムを選択したときの処理
            // 上に移動
            val mid = GeoPoint(popkeido, popido)
            if (idx == 0) {
                Map.mapController.animateTo(mid)
            } else if (idx == 1) {
                RouteFind(popkeido, popido, position)
                Map.mapController.animateTo(mid)
            } else if (idx == 2) {
                val intent = Intent(applicationContext, ARViewActivity::class.java)
                intent.putExtra("popkeido", popkeido.toString())
                intent.putExtra("popido", popido.toString())
                startActivity(intent)
            } else {

            }
        }
        alert.show()
    }

    override fun onResume() {
        super.onResume()
        adapter.clear()
        //マップ表示
        //map()
        //受信
        locationdata()

    }


    fun locationdata() {


        //フレンド一覧
        mLocationRef = mDatabaseReference.child(UsersPATH).child(user).child("friend")
        mLocationRef!!.addChildEventListener(mEventfriendListener)

        //mLocationRef = mDatabaseReference.child(UsersPATH).child(user).child("location")
        //mLocationRef!!.addValueEventListener(mEventListener)

    }


    override fun onBackPressed() {
        val drawer = DrawerLayout(this)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_main_drawer, menu)
        return true
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        when (id) {
            R.id.nav_map -> {

                Map.setMapType(0)
            }
            R.id.nav_map_air -> {
                Map.setMapType(8)//地図の種類変更
            }
            R.id.nav_share -> {
                val intent = Intent(applicationContext, ListFriendActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_find -> {
                val intent = Intent(applicationContext, UserFindActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(context, "ログアウトしました", Toast.LENGTH_LONG).show()
                finish()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    fun RouteFind(findkeido: Int, findido: Int, position: Int) {

        //RouteOverlay作成
        val routeOverlay = RouteOverlay(this, "dj0zaiZpPWowWHRab050ODJyTyZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-")

        //出発地ピンの吹き出し設定
        routeOverlay.setStartTitle("現在地")

        //目的地ピンの吹き出し設定
        routeOverlay.setGoalTitle(nowfriend[position])

        //MyLocationOverlayインスタンス作成
        _overlay = MyLocationOverlay(applicationContext, Map)

        //現在位置取得開始
        _overlay!!.enableMyLocation()

        //位置が更新されると、地図の位置も変わるよう設定
        _overlay!!.runOnFirstFix(Runnable {
            if (Map.mapController != null) {
                //現在位置を取得
                p = _overlay!!.myLocation


                var mylat = p.latitude.toString()
                var mylon = p.longitude.toString()
                //出発地、目的地、移動手段を設定
                routeOverlay.setRoutePos(
                    GeoPoint(mylat.replace(".", "").toInt(), mylon.replace(".", "").toInt()),
                    GeoPoint(findkeido, findido),
                    RouteOverlay.TRAFFIC_WALK
                )

                //RouteOverlayListenerの設定
                routeOverlay.setRouteOverlayListener(this)

                //検索を開始
                routeOverlay.search()

                //MapViewにRouteOverlayを追加
                Map.getOverlays().add(routeOverlay)

                //経由点ピンを非表示
                routeOverlay.setRoutePinVisible(false)

            }
        })
    }

    fun MyLocationData() {

        //MyLocationOverlayインスタンス作成
        _overlay = MyLocationOverlay(applicationContext, Map)

        //現在位置取得開始
        _overlay!!.enableMyLocation()

        //位置が更新されると、地図の位置も変わるよう設定
        _overlay!!.runOnFirstFix(Runnable {
            if (Map.mapController != null) {
                //現在位置を取得
                p = _overlay!!.myLocation


                if (nowLocationBoolean) {
                    //MapViewにMyLocationOverlayを追加。
                    Map.overlays.add(_overlay)
                    //地図移動
                    Map.mapController.animateTo(p)
                    nowLocationBoolean = false

                }else{
                    Map.mapController.animateTo(p)

                }




            }
        })



    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            100 -> { //ActivityCompat#requestPermissions()の第2引数で指定した値
                if (grantResults.size > 0 && grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    //許可された場合の処理
                    Toast.makeText(this, "現在地へのアクセスを許可しました。", Toast.LENGTH_LONG).show()
                } else {
                    //拒否された場合の処理
                    Toast.makeText(this, "現在地を扱う機能が正しく動作しません。", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}

