package com.devkproject.searchmask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.devkproject.searchmask.api.HospitalInterface
import com.devkproject.searchmask.api.RestClient
import com.devkproject.searchmask.model.HospitalModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HospitalActivity : AppCompatActivity() {

    companion object {
        const val key: String = "5P6O%2FpWJhPdT1TT4WozJRy7JM8flWa%2BgF6myzMPjj2JIB7BghBDwBe%2FD%2Fr2NhNnewfmoYAJQlDMmMc1ZTweYtA%3D%3D"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital)

        val hospitalService: HospitalInterface = RestClient.getHospitalClient()
        hospitalService.getHospital(key, 1, 10, "A0").enqueue(object : Callback<HospitalModel> {
            override fun onResponse(call: Call<HospitalModel>, response: Response<HospitalModel>) {
                if (response.isSuccessful) {
                    val results = response.body()
                    Log.d("HospitalActivity", "성공 : ${response.raw()}")
                    for (i in results!!.items) {
                        val name = i?.yadmNm
                        Log.d("HospitalActivity", "기관명 : $name")
                    }
                }
            }

            override fun onFailure(call: Call<HospitalModel>, t: Throwable) {
                Log.d("HospitalActivity","실패 : $t 콜 : $call")
            }
        })
    }
}
