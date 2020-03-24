package com.devkproject.searchmask

import android.annotation.SuppressLint
import android.content.Context
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
import com.devkproject.searchmask.api.MaskClient
import com.devkproject.searchmask.api.MaskInterface
import com.devkproject.searchmask.model.MaskModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_mask.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MaskActivity : AppCompatActivity() {

    var latitude: Double? = null
    var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mask)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        @SuppressLint("MissingPermission")
        if(!isGPSEnabled && !isNetworkEnabled) {
            Snackbar.make(layout1, "폰의 위치기능을 켜야 기능을 사용할 수 있습니다", Snackbar.LENGTH_LONG)
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
                Log.d("MaskActivity", "성공 : ${response.raw()}")
            }

            override fun onFailure(call: Call<MaskModel>, t: Throwable) {
                Log.d("MaskActivity", "실패")
            }
        })
    }
}