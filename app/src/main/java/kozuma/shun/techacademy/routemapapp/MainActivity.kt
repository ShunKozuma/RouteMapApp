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
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.Layout
import android.util.Log
import android.view.*
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
import jp.co.yahoo.android.maps.ar.ARController
import jp.co.yahoo.android.maps.ar.ARControllerListener
import jp.co.yahoo.android.maps.navi.NaviController
import jp.co.yahoo.android.maps.routing.RouteOverlay
import jp.co.yahoo.android.maps.weather.WeatherOverlay
import kotlinx.android.synthetic.main.nav_header_main.*
import org.w3c.dom.Text


class MainActivity : AppCompatActivity(), RouteOverlay.RouteOverlayListener, NaviController.NaviControllerListener,
    ARControllerListener, WeatherOverlay.WeatherOverlayListener, NavigationView.OnNavigationItemSelectedListener  {


    private lateinit var mToolbar: Toolbar

    private val PERMISSIONS_REQUEST_CODE = 100


    private var _overlay: MyLocationOverlay? = null //現在地
    private lateinit var mDatabaseReference: DatabaseReference
    private var mLocationRef: DatabaseReference? = null

    //ログイン中のユーザID
    lateinit var user: String

    var context: Context? = null

    //var longitude: String? = null
    //var latitude: String? = null

    lateinit var Map: MapView


    //現在地のデータ
    private lateinit var p: GeoPoint

    //受信相手のデータ
    var keido: Int = 0
    var ido: Int = 0

    //ボタン配置
    lateinit var currentButton: FloatingActionButton

    //Naviのインスタンス
    lateinit var naviController: NaviController

    //ARのインスタンス
    lateinit var arController: ARController

    //ARをよぶ
    var arjudge = false

    //ログインユーザーの名前
    var user_name: String? = null
    lateinit var navUsername : TextView

    //共有中の友達
    var nowfriendname: String? = null



    //WeatherOverlayのインターフェース
    override fun finishUpdateWeather(p0: WeatherOverlay?) {

    }

    override fun errorUpdateWeather(p0: WeatherOverlay?, p1: Int) {

    }

    //ARControllerのインターフェース
    override fun ARControllerListenerOnPOIPick(p0: Int) {
        finish()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
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
        arController.onPause()

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

    private val mEventname = object : ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val map = dataSnapshot.value as Map<String,String>
            user_name = map["name"] ?: ""

        }

    }

    private val mEvent = object : ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val map = dataSnapshot.value as Map<String, String>
            nowfriendname = map["name"] ?: ""

            navUsername.text = user_name

            //相手の現在地をピンで表示
            val mid = GeoPoint(keido, ido)
            val pinOverlay = PinOverlay(PinOverlay.PIN_VIOLET)
            map()
            Map.getOverlays().add(pinOverlay)

            val popupOverlay = object : PopupOverlay() {
                override fun onTap(item: OverlayItem?) {
                    //ポップアップをタッチした際の処理
                    //友達に位置情報の共有ダイアログ
                    AlertDialog.Builder(context as Activity).apply {
                        setTitle("ルート探索")
                        setMessage(nowfriendname + "ルートを探索しますか？")
                        setPositiveButton("探索", DialogInterface.OnClickListener { _, _ ->
                            RouteFind()

//
                            /*
                            //AR表示ボタンの追加
                            val ArButton = Button(context)
                            ArButton.layoutParams =
                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            ArButton.text = "AR表示"
                            ArButton.setOnClickListener {
                                RouteFind()
                            }
                            Map.addView(ArButton)
                            */

                            Toast.makeText(context, "ルートを表示致しました！", Toast.LENGTH_LONG).show()
                        })

                        setNegativeButton("Cancel", null)
                        show()
                    }

                }
            }

            Map.getOverlays().add(popupOverlay)
            pinOverlay.setOnFocusChangeListener(popupOverlay)
            pinOverlay.addPoint(mid, nowfriendname, "")

            //共有中のユーザー表示
            val shareUser = TextView(context)
            shareUser.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            shareUser.text = "現在地を受信中"
            shareUser.gravity = Gravity.TOP
            shareUser.setBackgroundColor(Color.GRAY)
            shareUser.setOnClickListener {
                //地図移動
                Map.mapController.animateTo(mid)
            }
            Map.addView(shareUser)


        }

    }


    private val mEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            if (dataSnapshot.value != null) {

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

//                //相手の現在地をピンで表示
//                val mid = GeoPoint(keido, ido)
//                val pinOverlay = PinOverlay(PinOverlay.PIN_VIOLET)
//                map()
//                Map.getOverlays().add(pinOverlay)

//                if(arjudge==false){
//                    //地図移動
//                    Map.mapController.animateTo(mid)
//                }
                mLocationRef = mDatabaseReference.child(UsersPATH).child(user).child("friend").child(user_id)
                mLocationRef!!.addListenerForSingleValueEvent(mEvent)


//                val popupOverlay = object : PopupOverlay() {
//                    override fun onTap(item: OverlayItem?) {
//                        //ポップアップをタッチした際の処理
//                        //友達に位置情報の共有ダイアログ
//                        AlertDialog.Builder(context as Activity).apply {
//                            setTitle("ルート探索")
//                            setMessage(nowfriendname + "ルートを探索しますか？")
//                            setPositiveButton("探索", DialogInterface.OnClickListener { _, _ ->
//                                RouteFind()
//
////
//                                /*
//                                //AR表示ボタンの追加
//                                val ArButton = Button(context)
//                                ArButton.layoutParams =
//                                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                                ArButton.text = "AR表示"
//                                ArButton.setOnClickListener {
//                                    RouteFind()
//                                }
//                                Map.addView(ArButton)
//                                */
//
//                                Toast.makeText(context, "ルートを表示致しました！", Toast.LENGTH_LONG).show()
//                            })
//
//                            setNegativeButton("Cancel", null)
//                            show()
//                        }
//
//                    }
//                }
//
//                Map.getOverlays().add(popupOverlay);
//                pinOverlay.setOnFocusChangeListener(popupOverlay);
//                pinOverlay.addPoint(mid, nowfriendname, "東京ミッドタウンについて")

//                //共有中のユーザー表示
//                val shareUser = TextView(context)
//                shareUser.layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//                shareUser.text = "現在地を受信中"
//                shareUser.gravity = Gravity.TOP
//                shareUser.setBackgroundColor(Color.GRAY)
//                shareUser.setOnClickListener {
//                    //地図移動
//                    Map.mapController.animateTo(mid)
//                }
//                Map.addView(shareUser)


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

        //NavigationdrawerのユーザID
        val navigation : NavigationView  = findViewById(R.id.nav_view)
        val headerView : View = navigation.getHeaderView(0)
        val navUserid : TextView = headerView.findViewById(R.id.nav_id)
        navUsername = headerView.findViewById(R.id.nav_name)

        navUsername.text = user_name
        navUserid.text = user



        context = this



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
        val weatherOverlay =  WeatherOverlay(this)

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
            }else{
                Map.getOverlays().remove(weatherOverlay)
            }
        }
        // トグルスイッチを配置するメニューを取得
        val menuItem2 = navigationView.menu.findItem(R.id.nav_rain)
        // 取得したメニューにトグルスイッチを設定
        menuItem2.actionView = switch1


        navigationView.setNavigationItemSelectedListener(this)


    }

    override fun onResume() {
        super.onResume()

        //マップ表示
        map()
        //受信
        locationdata()

    }


    fun locationdata() {


        mLocationRef = mDatabaseReference.child(UsersPATH).child(user).child("location")
        mLocationRef!!.addValueEventListener(mEventListener)

    }


    fun map() {

        //地図を表示
//        Map = MapView(this, "dj0zaiZpPWowWHRab050ODJyTyZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-")
//        setContentView(Map)




        val layout = LinearLayout(this)
        layout.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        // 右部に配置
        layout.gravity = Gravity.RIGHT or Gravity.BOTTOM
        // Verticalに設定する
        layout.orientation = LinearLayout.VERTICAL

        layout.setPadding(0, 0, 20, 30)


        //AR表示ボタンの追加
        val ArButton = Button(this)
        ArButton.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        ArButton.text = "AR表示"
        ArButton.setOnClickListener {
            //ArView()
            val intent = Intent(applicationContext, ARViewActivity::class.java)
            startActivity(intent)


        }
        layout.addView(ArButton)


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
        val fab = FloatingActionButton(this)
        fab.setOnClickListener {
            val intent = Intent(applicationContext, FriendsListActivity::class.java)
            intent.putExtra("button", "0")
            startActivity(intent)
        }

        val fabimage = BitmapFactory.decodeResource(resources, R.drawable.plus)
        fab.setImageBitmap(fabimage)
        layout.addView(fab)


        Map.addView(layout)

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

        when(id){
            R.id.nav_map -> {
                Map.setMapType(0)
            }
            R.id.nav_map_air -> {
                Map.setMapType(8)//地図の種類変更
            }
            R.id.nav_rain -> {
            }
            R.id.nav_share -> {
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




    fun RouteFind() {

        //RouteOverlay作成
        val routeOverlay = RouteOverlay(this, "dj0zaiZpPWowWHRab050ODJyTyZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-")

        //出発地ピンの吹き出し設定
        routeOverlay.setStartTitle("現在地")

        //目的地ピンの吹き出し設定
        routeOverlay.setGoalTitle("OOさん")

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
                    GeoPoint(keido, ido),
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

                //地図移動
                Map.mapController.animateTo(p)


            }
        })

        //MapViewにMyLocationOverlayを追加。
        Map.overlays.add(_overlay)

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

