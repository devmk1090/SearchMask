package com.devkproject.searchmask.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/"
const val HOSPITAL_URL = "http://apis.data.go.kr/B551182/pubReliefHospService/"

object RestClient {

    fun getMaskClient(): MaskInterface {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MaskInterface::class.java)
    }
}