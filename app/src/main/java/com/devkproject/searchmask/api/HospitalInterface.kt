package com.devkproject.searchmask.api

import com.devkproject.searchmask.model.HospitalModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HospitalInterface {

    @GET("getpubReliefHospList")
    fun getHospital(@Query("serviceKey") key: String,
                    @Query("pageNo") page: Int,
                    @Query("numOfRows") numOfRows: Int,
                    @Query("spclAdmTyCd") type: String) : Call<HospitalModel>
}
//api/subway/{apikey}/json/stationByLine/1/100/{linenum}

