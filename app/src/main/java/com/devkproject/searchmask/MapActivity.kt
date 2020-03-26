package com.devkproject.searchmask

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.devkproject.searchmask.api.MaskClient
import com.devkproject.searchmask.api.MaskInterface
import com.devkproject.searchmask.model.MaskDetailResponse
import com.devkproject.searchmask.model.MaskModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_map.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    var latitude: Double? = null
    var longitude: Double? = null
    val marker = Marker()

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

//        val infoWindow = InfoWindow()
//        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
//            override fun getText(p0: InfoWindow): CharSequence {
//                return "현재 위치"
//            }
//        }
//        infoWindow.open(marker)
        val maskService: MaskInterface = MaskClient.getClient()
        maskService.getMaskGeo(latitude!!,longitude!!,1000).enqueue(object : Callback<MaskModel> {
            override fun onResponse(call: Call<MaskModel>, response: Response<MaskModel>) {
                if (response != null && response.isSuccessful) {
                    Log.d("MaskActivity", "성공 : " + response.body().toString())
                    jsonObject(response.body())
                }
            }
            override fun onFailure(call: Call<MaskModel>, t: Throwable) {
                Log.d("MaskActivity", "실패")
            }
        })
        marker.position = LatLng(latitude!!, longitude!!)
        marker.captionText = "현재 위치"
        marker.map = naverMap
    }

    fun api(latitude: Double, longitude: Double) {

    }

    fun jsonObject (data: MaskModel?) {
        val maskDetail : MaskDetailResponse? = data?.stores?.get(0)
        maskDetail?.let {

        }
        val jObject : JSONObject = JSONObject(data.toString())
        val jArray = jObject.getJSONArray("stores")
        for (i in 0 until jArray.length()) {
            val obj = jArray.getJSONObject(i)
            val name = obj.getString("name")
            Log.d("MaskActivity", name)
        }
    }

}
//                    body?.let {
//                        if (it.stores != null) {
//                            for(i in 0 until it.stores.size) {
//                                val name = it.stores[i]?.name
//                                val addr = i!!.addr
//                                val lat = it.stores[i]?.lat
//                                val lng = it.stores[i]?.lng
//                                val remain = it.stores[i]?.remain_stat
//                                val stock = i!!.stock_at
//                                val create = i!!.created_at
//                                Log.d("MapActivity", name)
//                                marker.position = LatLng(lat!!, lng!!)
//                                marker.captionText = name!!
//                                marker.subCaptionText = "재고 : $remain"
//                                marker.subCaptionColor = Color.BLUE
//                            }
//                            }