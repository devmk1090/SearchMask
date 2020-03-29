package com.devkproject.searchmask.api

import com.devkproject.searchmask.model.MaskModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MaskInterface {

    @GET("storesByGeo/json")
    fun getMaskGeo(@Query("lat") lat: Double,
                   @Query("lng") lng: Double,
                   @Query("m") radius: Int) : Call<MaskModel>

    @GET("storesByAddr/json")
    fun getMaskAddr(@Query("address") addr: String) : Call<MaskModel>

}