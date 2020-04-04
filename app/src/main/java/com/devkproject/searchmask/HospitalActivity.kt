package com.devkproject.searchmask

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import kotlinx.android.synthetic.main.activity_hospital.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.lang.Exception
import java.net.URL

class HospitalActivity : AppCompatActivity() {

    var runnable: Runnable? = null
    var handler: Handler? = null
    val buffer: StringBuffer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital)
        hospital_btn.setOnClickListener {
            runnable = Runnable {
                getXmlTask()
            }
            handler = Handler()
            handler?.run {
                postDelayed(runnable, 2000)
            }
        }

    }

    private fun getXmlTask() {
        val test: String =  "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?" +
                "serviceKey=5P6O%2FpWJhPdT1TT4WozJRy7JM8flWa%2BgF6myzMPjj2JIB7BghBDwBe%2FD%2Fr2NhNnewfmoYAJQlDMmMc1ZTweYtA%3D%3D" +
                "&numOfRows=10" +
                "&pageNo=1" +
                "&stationName=종로구" +
                "&dataTerm=DAILY" +
                "&ver=1.3"
        try {
            val url = URL(test)
            val parserCreate: XmlPullParserFactory? = XmlPullParserFactory.newInstance()
            val parser = parserCreate?.newPullParser()
            val inputStream: InputStream? = url.openStream()
            parser!!.setInput(inputStream, null)
            var parserEvent: Int = parser!!.eventType
            var tag: String? = null
            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                when (parserEvent) {
                    XmlPullParser.START_DOCUMENT -> {
                        Log.d("HospitalActivity", "스타트 다큐먼트")
                    }
                    XmlPullParser.START_TAG -> {
                        tag = parser.name
                        if (tag == "item") {
                            Log.d("HospitalActivity", "스타트 태그 : ${parser.name}")
                        } else if(tag == "so2Value") {
                            buffer?.append("so2Value : ")
                            parser.next()
                            buffer?.append(parser.text)
                            buffer?.append("\n")
                        }
                    }
                    XmlPullParser.TEXT -> {
                    }
                    XmlPullParser.END_TAG -> {
                        tag = parser.name
                        if (tag == "item") {
                            Log.d("HospitalActivity", "엔드 태그 : $tag")
                        }
                    }
                }
                parserEvent = parser.next()
            }
        } catch (e: Exception) {
            Log.d("HospitalActivity", "실패 : " + e.stackTrace)
        }
        Log.d("HospitalActivity", buffer.toString())
    }
//    private inner class getXMLTest : AsyncTask<Void, Void, String>() {
//        override fun doInBackground(vararg params: Void?): String? {
//
//            return null
//        }
//
//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//            getXmlTask()
//        }
//    }
}

