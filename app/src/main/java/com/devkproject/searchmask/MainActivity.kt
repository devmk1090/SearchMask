package com.devkproject.searchmask

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var first_time : Long = 0
    private var second_time : Long = 0
    var latitude: Double? = null
    var longitude: Double? = null
    var km: Int? = null
    var addr: String? = null

    companion object {
        private const val REQUEST_LOCATION_PERMISSION_CODE = 100
    }

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fineLocationPermission && coarseLocationPermission
    }

    private fun moveMapActivity() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        @SuppressLint("MissingPermission")
        if(!isGPSEnabled && !isNetworkEnabled) {
            Snackbar.make(main_layout, "폰의 위치기능을 켜야 사용할 수 있습니다", Snackbar.LENGTH_INDEFINITE)
                .setAction("설정") {
                    val goToSettings = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(goToSettings)
                }.show()
        }
        else {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_MEDIUM
            criteria.powerRequirement = Criteria.POWER_MEDIUM
            locationManager.requestSingleUpdate(criteria, object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    latitude = location!!.latitude
                    longitude = location!!.longitude
                    ArrayAdapter.createFromResource(applicationContext, R.array.km_spinner, android.R.layout.simple_spinner_item)
                        .also { adapter ->
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            km_spinner.adapter = adapter
                        }
                    km_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            when(position) {
                                0 -> { km = 1000 }
                                1 -> { km = 2000 }
                                2 -> { km = 3000 }
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                    km_spinner.setSelection(1)

                    map_btn.setOnClickListener {
                        val intent = Intent(applicationContext, MapActivity::class.java)
                        intent.putExtra("km", km)
                        Log.d("MainActivity", "반경" + km.toString())
                        intent.putExtra("latitude", latitude!!)
                        intent.putExtra("longitude", longitude!!)
                        startActivity(intent)
                        finish()
                    }
                    ArrayAdapter.createFromResource(applicationContext, R.array.spinner_region, android.R.layout.simple_spinner_item)
                        .also { adapter ->
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            region_spinner.adapter = adapter
                        }
                    region_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            when(position) {
                                0 -> { setSigunSpinner(R.array.spinner_region_seoul) }
                                1 -> { setSigunSpinner(R.array.spinner_region_busan) }

                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                    addr_btn.setOnClickListener {
                        addr = region_spinner.selectedItem.toString() + " " + sigun_spinner.selectedItem.toString()
                        val intent = Intent(applicationContext, AddressActivity::class.java)

                        intent.putExtra("addr", addr)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                override fun onProviderEnabled(provider: String?) {}

                override fun onProviderDisabled(provider: String?) {}

            }, null)
        }
    }

    private fun setSigunSpinner(list: Int) {
        ArrayAdapter.createFromResource(applicationContext, list, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sigun_spinner.adapter = adapter
            }
        sigun_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position) {
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onResume() {
        super.onResume()
        if(checkLocationPermission()) {
            moveMapActivity()
        } else {
            //shouldShowRequestPermissionRationale : 사용자가 권한을 거절했던 적이 있는지 확인
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "이 앱을 실행하려면 위치 권한이 필요합니다", Toast.LENGTH_LONG).show()
            }
            //System Activity 를 띄움
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSION_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (getDayOfWeek() == "월요일") {
            dayOfWeek_txt.text = getDayOfWeek() + " 구입 가능 출생연도 끝자리 : 1,6"
        } else if (getDayOfWeek() == "화요일") {
            dayOfWeek_txt.text = getDayOfWeek() + " 구입 가능 출생연도 끝자리 : 2,7"
        } else if (getDayOfWeek() == "수요일") {
            dayOfWeek_txt.text = getDayOfWeek() + " 구입 가능 출생연도 끝자리 : 3,8"
        } else if (getDayOfWeek() == "목요일") {
            dayOfWeek_txt.text = getDayOfWeek() + " 구입 가능 출생연도 끝자리 : 4,9"
        } else if (getDayOfWeek() == "금요일") {
            dayOfWeek_txt.text = getDayOfWeek() + " 구입 가능 출생연도 끝자리 : 5,0"
        } else {
            dayOfWeek_txt.text = getDayOfWeek() + "은 주 중에 못 산 사람 구입 가능"
        }
        hospital_btn.setOnClickListener {
            val intent = Intent(applicationContext, HospitalActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getDayOfWeek() :String {
        val instance = Calendar.getInstance()
        val dayOfWeek = instance.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.KOREA)
        return dayOfWeek
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
