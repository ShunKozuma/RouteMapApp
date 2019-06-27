package kozuma.shun.techacademy.routemapapp

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.co.yahoo.android.maps.GeoPoint
import jp.co.yahoo.android.maps.MapActivity
import jp.co.yahoo.android.maps.MapView
import jp.co.yahoo.android.maps.MyLocationOverlay
import jp.co.yahoo.android.maps.ar.ARController
import jp.co.yahoo.android.maps.ar.ARControllerListener
import jp.co.yahoo.android.maps.navi.NaviController
import jp.co.yahoo.android.maps.routing.RouteOverlay

class ARViewActivity : MapActivity(), RouteOverlay.RouteOverlayListener, NaviController.NaviControllerListener,
    ARControllerListener {


    private var _overlay: MyLocationOverlay? = null //現在地
    private lateinit var mDatabaseReference: DatabaseReference
    private var mLocationRef: DatabaseReference? = null

    //ログイン中のユーザID
    val user = FirebaseAuth.getInstance().currentUser!!.uid

    //Naviのインスタンス
    lateinit var naviController: NaviController

    //ARのインスタンス
    lateinit var arController: ARController

    //受信相手のデータ
    var keido: Int = 0
    var ido: Int = 0

    lateinit var routeOverlay: RouteOverlay

    lateinit var Map: MapView

    private val PERMISSIONS_REQUEST_CODE = 100

    //ARControllerのインターフェース
    override fun ARControllerListenerOnPOIPick(p0: Int) {
//        finish()
//        val intent = Intent(applicationContext, MainActivity::class.java)
//        startActivity(intent)
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
        //arController.onPause()

        //案内処理を継続しない場合は停止させる
        //naviController.stop()

        //ARControllerをNaviControllerから削除
        //naviController.setARController(null)

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


        //横向き固定
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        //ARControllerインスタンス作成
        arController = ARController(this, this)

        //ARControllerをNaviControllerに設定
        naviController.setARController(arController)

        //案内処理を開始
        naviController.start()


        return false
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

                keido = latitude.replace(".", "").toInt()
                println(keido)
                ido = longitude.replace(".", "").toInt()


            }

        }


        override fun onCancelled(p0: DatabaseError) {
        }
    }


    override fun isRouteDisplayed(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_arview)


        val popkeido = intent.getStringExtra("popkeido").toInt()

        val popido = intent.getStringExtra("popido").toInt()
        // パーミッションの許可状態を確認する
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSIONS_REQUEST_CODE)
            } else {

                ARView(popkeido,popido)
            }
        }
    }



    fun ARView(arkeido: Int ,arido: Int) {
        Map = MapView(this, "dj0zaiZpPWowWHRab050ODJyTyZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-")


        //Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        //mLocationRef = mDatabaseReference.child(UsersPATH).child(user).child("location")
        //mLocationRef!!.addValueEventListener(mEventListener)
        //MyLocationOverlayインスタンス作成
        _overlay = MyLocationOverlay(applicationContext, Map)

        //現在位置取得開始
        _overlay!!.enableMyLocation()



        //位置が更新されると、地図の位置も変わるよう設定
        _overlay!!.runOnFirstFix(Runnable {
            if (Map.mapController != null) {
                //現在位置を取得
                var p = _overlay!!.myLocation


                var mylat = p.latitude.toString()
                var mylon = p.longitude.toString()

                //RouteOverlay作成
                routeOverlay = RouteOverlay(this, "dj0zaiZpPWowWHRab050ODJyTyZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-")

                //出発地ピンの吹き出し設定
                routeOverlay.setStartTitle("現在地")

                //目的地ピンの吹き出し設定
                routeOverlay.setGoalTitle("OOさん")


                //出発地、目的地、移動手段を設定
                routeOverlay.setRoutePos(
                    GeoPoint(mylat.replace(".", "").toInt(), mylon.replace(".", "").toInt()),
                    GeoPoint(arkeido, arido),
                    RouteOverlay.TRAFFIC_WALK
                )

                //RouteOverlayListenerの設定
                routeOverlay.setRouteOverlayListener(this)

                //検索を開始
                routeOverlay.search()


            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            100 -> { //ActivityCompat#requestPermissions()の第2引数で指定した値
                if (grantResults.size > 0 && grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    //許可された場合の処理
                    ARView(intent.getStringExtra("popkeido").toInt(),intent.getStringExtra("popido").toInt())
                } else {
                    //拒否された場合の処理
                    Toast.makeText(this, "AR表示機能が正しく動作しません。", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}


