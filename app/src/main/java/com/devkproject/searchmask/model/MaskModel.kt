package com.devkproject.searchmask.model

data class MaskModel(
    val count: Int,
    val stores: List<MaskDetailResponse>
)

data class MaskDetailResponse (
    val type: String,
    val name: String,
    val addr: String, //주소
    val lat: Double, //위도
    val lng: Double, //경도
    val stock_at: String, //입고 시간
    val remain_start: String, //재고 상태
    val created_at: String //데이터 생성 일자

)