package com.devkproject.searchmask

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    var latitude: Double? = null
    var longitude: Double? = null
    private val marker = Marker()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)

        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient("urjpw136l7")
        mapView.getMapAsync(this)

    }

    override fun onMapReady(naverMap: NaverMap) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude!!, longitude!!)).animate(CameraAnimation.Fly)
        naverMap.moveCamera(cameraUpdate)
        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
            override fun getText(p0: InfoWindow): CharSequence {
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }
        val maskService: MaskInterface = MaskClient.getClient()
        maskService.getMaskGeo(latitude!!,longitude!!,1000).enqueue(object : Callback<MaskModel> {
            override fun onResponse(call: Call<MaskModel>, response: Response<MaskModel>) {
                if (response != null && response.isSuccessful) {
                    Log.d("MaskActivity", "성공 : " + response.body().toString())
                    val results : MaskModel? = response.body()

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
                            tag = "$addr\n입고 시간 : $stock\n마지막 업데이트 : $create"
                            setOnClickListener {
                                infoWindow.open(this)
                                true
                            }
                            isHideCollidedCaptions = true
                            map = naverMap
                        }
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
        marker.position = LatLng(latitude!!, longitude!!)
        marker.width = 70
        marker.height = 110
        marker.captionText = "현재 위치"
        marker.map = naverMap
    }
}