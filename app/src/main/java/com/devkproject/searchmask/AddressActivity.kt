package com.devkproject.searchmask

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.devkproject.searchmask.api.MaskInterface
import com.devkproject.searchmask.api.RestClient
import com.devkproject.searchmask.model.MaskModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class AddressActivity : AppCompatActivity(), OnMapReadyCallback {

    var addr: String? = null
    private var first_time : Long = 0
    private var second_time : Long = 0
    private lateinit var locationSource: FusedLocationSource
    private lateinit var mAdView: AdView

    companion object {
        private const val TAG = "AddressActivity"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.ad_View)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        locationSource = FusedLocationSource(this, AddressActivity.LOCATION_PERMISSION_REQUEST_CODE)
        map_back_btn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        addr = intent.getStringExtra("addr")
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient("urjpw136l7")
        mapView.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
            override fun getText(p0: InfoWindow): CharSequence {
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }
        val maskService: MaskInterface = RestClient.getMaskClient()
        maskService.getMaskAddr(addr!!).enqueue(object : Callback<MaskModel> {
            override fun onResponse(call: Call<MaskModel>, response: Response<MaskModel>) {
                if (response != null && response.isSuccessful) {
                    val results : MaskModel? = response.body()
                    Log.d("MapActivity", "성공 : ${response.raw()}")

                    val markers = mutableListOf<Marker>()
                    val image_red = OverlayImage.fromResource(R.drawable.map_icon_red)
                    val image_blue = OverlayImage.fromResource(R.drawable.map_icon_blue)
                    for (i in results!!.stores) {
                        val name = i?.name
                        val lat = i?.lat
                        val lng = i?.lng
                        val remain = i?.remain_stat
                        val addr = i?.addr
                        val stock = i?.stock_at
                        val create = i?.created_at
                        markers += Marker().apply {
                            position = LatLng(lat!!, lng!!)
                            captionText = name!!
                            when {
                                remain.equals(null) -> {
                                    icon = image_red
                                    subCaptionText = "판매중지"
                                    subCaptionColor = Color.RED }
                                remain.equals("break") -> {
                                    icon = image_red
                                    subCaptionText = "판매중지"
                                    subCaptionColor = Color.RED }
                                remain.equals("empty") -> {
                                    icon = image_red
                                    subCaptionText = "품절"
                                    subCaptionColor = Color.RED }
                                remain.equals("few") -> {
                                    icon = image_blue
                                    subCaptionText = "30개 미만"
                                    subCaptionColor = Color.BLUE }
                                remain.equals("some") -> {
                                    icon = image_blue
                                    subCaptionText = "99 ~ 30개"
                                    subCaptionColor = Color.BLUE }
                                remain.equals("plenty") -> {
                                    icon = image_blue
                                    subCaptionText = "100개 이상"
                                    subCaptionColor = Color.BLUE }
                            }
                            tag = if (stock.equals(null))
                                "$addr\n" +
                                        "입고 시간: 정보없음\n" +
                                        "재고 : $subCaptionText\n" +
                                        "정보없음"
                            else
                                "$addr\n" +
                                        "입고 시간: $stock\n" +
                                        "재고 : $subCaptionText\n" +
                                        "${create?.let { simpleDateFormatter(it) }}"
                            setOnClickListener {
                                infoWindow.open(this)
                                true
                            }
                            isHideCollidedCaptions = true
                            map = naverMap
                        }
                        val cameraUpdate = CameraUpdate.scrollTo(LatLng(lat!!, lng!!)).animate(
                            CameraAnimation.Fly)
                        naverMap.moveCamera(cameraUpdate)
                    }
                }
            }
            override fun onFailure(call: Call<MaskModel>, t: Throwable) {
                Log.d("MaskActivity", "실패")
            }
        })
        naverMap.setOnMapClickListener { pointF, latLng ->
            infoWindow.close()
        }
    }

    fun simpleDateFormatter(create: String): String {
        val curTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val createDate = dateFormat.parse(create).time
        val diff = (curTime - createDate) / (24 * 60 * 60)
        return if(diff > 59)
            "${diff / 60}시간전 업데이트"
        else
            "${diff}분전 업데이트"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        if(locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        second_time = System.currentTimeMillis()
        if(second_time - first_time < 2000){
            super.onBackPressed()
            finishAffinity()
        } else Toast.makeText(this,"한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
        first_time = System.currentTimeMillis()
    }
}
