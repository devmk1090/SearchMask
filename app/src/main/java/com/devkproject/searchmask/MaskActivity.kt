package com.devkproject.searchmask

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkproject.searchmask.api.MaskClient
import com.devkproject.searchmask.api.MaskInterface
import com.devkproject.searchmask.model.MaskDetailResponse
import com.devkproject.searchmask.model.MaskModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_mask.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class MaskActivity : AppCompatActivity() {

    var latitude: Double? = null
    var longitude: Double? = null
    private var first_time : Long = 0
    private var second_time : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mask)

        toMapActivity.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            Log.d("MaskActivity", latitude.toString())
            Log.d("MaskActivity", longitude.toString())
            startActivity(intent)
        }
    }

    fun api(latitude: Double, longitude: Double) {
        val maskService: MaskInterface = MaskClient.getClient()
        maskService.getMaskGeo(latitude,longitude,1000).enqueue(object : Callback<MaskModel> {

            override fun onResponse(call: Call<MaskModel>, response: Response<MaskModel>) {
                if(response.isSuccessful) {
                    val body = response.body()
                    Log.d("MaskActivity", "성공 : ${response.raw()}")
                    body?.let {
                        if(it.stores != null) {
                            setAdapter(it.stores)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<MaskModel>, t: Throwable) {
                Log.d("MaskActivity", "실패")
            }
        })
    }

    private fun setAdapter(maskList: ArrayList<MaskDetailResponse?>) {
        val mAdapter = MaskRVAdapter(this, maskList)
        maskRV.adapter = mAdapter
        maskRV.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        @SuppressLint("MissingPermission")
        if(!isGPSEnabled && !isNetworkEnabled) {
            Snackbar.make(mask_layout, "폰의 위치기능을 켜야 기능을 사용할 수 있습니다", Snackbar.LENGTH_LONG)
                .setAction("설정", View.OnClickListener {
                    val goToSettings = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(goToSettings)
                }).show()
        }
        else {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_MEDIUM
            criteria.powerRequirement = Criteria.POWER_MEDIUM

            locationManager.requestSingleUpdate(criteria, object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    latitude = location!!.latitude
                    longitude = location!!.longitude
                    api(latitude!!, longitude!!)
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                override fun onProviderEnabled(provider: String?) {}

                override fun onProviderDisabled(provider: String?) {}

            }, null)
        }
    }

    override fun onBackPressed() {
        second_time = System.currentTimeMillis()
        if(second_time - first_time < 2000){
            super.onBackPressed()
            finish()
        } else Toast.makeText(this,"뒤로가기 버튼을 한번 더 누르시면 종료",Toast.LENGTH_SHORT).show()
        first_time = System.currentTimeMillis()
    }

}