package com.devkproject.searchmask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.devkproject.searchmask.api.HospitalInterface
import com.devkproject.searchmask.api.RestClient
import com.devkproject.searchmask.model.HospitalModel
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.URL

class HospitalActivity : AppCompatActivity() {

    companion object {
        const val key: String = "5P6O%2FpWJhPdT1TT4WozJRy7JM8flWa%2BgF6myzMPjj2JIB7BghBDwBe%2FD%2Fr2NhNnewfmoYAJQlDMmMc1ZTweYtA%3D%3D"
        const val hospitalUrl: String = "http://apis.data.go.kr/B551182/pubReliefHospService/getpubReliefHospList?" +
                "serviceKey=5P6O%2FpWJhPdT1TT4WozJRy7JM8flWa%2BgF6myzMPjj2JIB7BghBDwBe%2FD%2Fr2NhNnewfmoYAJQlDMmMc1ZTweYtA%3D%3D" +
                "&pageNo=1" +
                "&numOfRows=10" +
                "&spclAdmTyCd=A0"
        const val pmUrl: String = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty" +
                "?stationName=종로구" +
                "&dataTerm=month" +
                "&pageNo=1" +
                "&numOfRows=10" +
                "&ServiceKey=5P6O%2FpWJhPdT1TT4WozJRy7JM8flWa%2BgF6myzMPjj2JIB7BghBDwBe%2FD%2Fr2NhNnewfmoYAJQlDMmMc1ZTweYtA%3D%3D" +
                "&ver=1.3"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital)

        getXmlTask()
    }

    private fun getXmlTask() {
        val hospitalModel: HospitalModel
        var pm10: String? = null
        try {
            val url = URL(pmUrl)
            val parserCreate: XmlPullParserFactory? = XmlPullParserFactory.newInstance()
            val parser = parserCreate?.newPullParser()
            var parserEvent: Int = parser!!.eventType
            var tag: String? = null
            parser.setInput(url.openStream(), "UTF-8")

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                when (parserEvent) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name == "pm10Value") {
                            pm10 = parser.name
                            Log.d("HospitalActivity", "성공 : $pm10")
                        }
                    }
                }
            }
            parser.next()
        } catch (e: Exception) {
            Log.d("HospitalActivity", "실패 : " + e.stackTrace)
        }
    }
}
