package kozuma.shun.techacademy.routemapapp

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
import jp.co.yahoo.android.maps.MapActivity
import jp.co.yahoo.android.maps.MapView
import jp.co.yahoo.android.maps.MyLocationOverlay
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : MapActivity() {

    private var _overlay: MyLocationOverlay? = null //現在地

    override fun isRouteDisplayed(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

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
            startActivity(intent)
        }

        val fabimage = BitmapFactory.decodeResource(resources, R.drawable.plus)
        fab.setImageBitmap(fabimage)
        layout.addView(fab)

        mapView.addView(layout)
    }

}

