package com.devkproject.searchmask.model

data class HospitalModel (
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int,
    val items: ArrayList<HospitalDetailResponse?>
)
data class HospitalDetailResponse (
    val sidoNm: String, //시도명
    val sgguNm: String, //시군구명
    val yadmNm: String, //기관명
    val hospTyTpCd: String, //선정유형 A or B
    val telno: String, //전화번호
    val adtFrDd: String, //운영가능일자
    val spclAdmTyCd: String //구분코드 A0:국민안심병원, 97:코로나 검사 실시기관, 99: 코로나 선별진료소
)