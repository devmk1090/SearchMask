package com.devkproject.searchmask.api

import com.devkproject.searchmask.model.HospitalModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HospitalInterface {

    @GET("getpubReliefHospList")
    fun getHospital(@Query("ServiceKey") key: String,
                   @Query("pageNo") page: Int,
                   @Query("numOfRows") radius: Int,
                   @Query("spclAdmTyCd") type: String) : Call<HospitalModel>
}