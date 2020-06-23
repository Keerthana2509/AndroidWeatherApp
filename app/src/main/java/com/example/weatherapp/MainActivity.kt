package com.example.weatherapp

import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

lateinit var country_text: EditText
lateinit var result_text:TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        country_text = findViewById(R.id.country)
        result_text = findViewById(R.id.result)
    }

    class DownloadWeatherAPI: AsyncTask<String, Unit, String>(){
        override fun doInBackground(vararg urls: String?): String? {
            var result : String =""
            try {
                val url: URL = URL(urls[0])
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection.inputStream
                val reader = InputStreamReader(inputStream)
                var data = reader.read()
                while(data != -1){
                    val ch = data.toChar()
                    result += ch
                    Log.e("Result: ",result.toString())
                    data = reader.read()
                }
                connection.disconnect()
                return result
            }
            catch (e: Exception){
                e.printStackTrace()
                return null
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.e("JSON: ",result.toString())
            try {
                val jsonObject = JSONObject(result)
                val weatherInfo: String = jsonObject.getString("weather")
                val arr = JSONArray(weatherInfo)

                var i = 0
                var main: String= ""
                var description : String =""
                var res=""
                while (i < arr.length()) {
                    val jsonPart = arr.getJSONObject(i)
                    main = jsonPart.getString("main")
                    description = jsonPart.getString("description")
                    Log.e("Main: ", jsonPart.getString("main"))
                    Log.e("Description: ", jsonPart.getString("description"))
                    if( main != "" && description != ""){
                         res = "$main : $description"
                    }
                    i++
                }
                if(res != ""){
                    result_text.text = res
                }
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun ButtonClick(view: View){
        val task = DownloadWeatherAPI()
        task.execute("https://samples.openweathermap.org/data/2.5/weather?q="+ country_text.text.toString()+"&appid=439d4b804bc8187953eb36d2a8c26a02")

    }
}
