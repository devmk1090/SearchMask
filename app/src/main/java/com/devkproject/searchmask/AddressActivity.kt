package com.devkproject.searchmask

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.devkproject.searchmask.api.MaskClient
import com.devkproject.searchmask.api.MaskInterface
import com.devkproject.searchmask.model.MaskModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.activity_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class AddressActivity : AppCompatActivity(), OnMapReadyCallback {

    var addr: String? = null
    private var first_time : Long = 0
    private var second_time : Long = 0

    companion object {
        private const val TAG = "AddressActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
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
        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
            override fun getText(p0: InfoWindow): CharSequence {
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }
        val maskService: MaskInterface = MaskClient.getClient()
        maskService.getMaskAddr(addr!!).enqueue(object : Callback<MaskModel> {
            override fun onResponse(call: Call<MaskModel>, response: Response<MaskModel>) {
                if (response != null && response.isSuccessful) {
                    val results : MaskModel? = response.body()
                    Log.d("MapActivity", "성공 : ${response.raw()}")

                    val markers = mutableListOf<Marker>()
                    val image = OverlayImage.fromResource(R.drawable.map_icon)
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
                                remain.equals("break") -> subCaptionText = "판매중지"
                                remain.equals("empty") -> subCaptionText = "품절"
                                remain.equals("few") -> subCaptionText = "30개 미만"
                                remain.equals("some") -> subCaptionText = "99 ~ 30개"
                                remain.equals("plenty") -> subCaptionText = "100개 이상"
                            }
                            subCaptionColor = Color.BLUE
                            icon = image
                            tag = "$addr\n" +
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
        return "${diff}분전 업데이트"
    }

    override fun onBackPressed() {
        second_time = System.currentTimeMillis()
        if(second_time - first_time < 2000){
            super.onBackPressed()
            finishAffinity()
        } else Toast.makeText(this,"뒤로가기 버튼을 한 번 더 누르면 종료", Toast.LENGTH_SHORT).show()
        first_time = System.currentTimeMillis()
    }
}
