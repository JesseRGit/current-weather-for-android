package com.example.weatherviewer

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import org.json.JSONException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextSearch = findViewById<EditText>(R.id.editText_searchCity)
        val textViewMsg = findViewById<TextView>(R.id.textView_message)
        val buttonSearch = findViewById<Button>(R.id.button_search)

        // sets the message textView to start state aka removes the red error text after failed search
        editTextSearch.setOnClickListener {
            textViewMsg.setTextColor(Color.parseColor("#808080"))
            textViewMsg.text = "Country code is optional and written after comma."
        }

        // handles enter/proceed press on device
        editTextSearch.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                getWeatherData()
                return@OnKeyListener true
            }
            false
        })

        // handles search button press
        buttonSearch.setOnClickListener {
            getWeatherData()
        }

    }

    // function to parse city from user input
    fun getCityName (): String {
        var cityWithCountryCode = findViewById<EditText>(R.id.editText_searchCity).text.toString()
        var city = ""

        if (cityWithCountryCode.contains(",")) {
            city = cityWithCountryCode.substring(0, cityWithCountryCode.indexOf(","))
        }
        return city
    }

    // function to parse country code from user input
    fun getCountryCode (): String {

        var cityWithCountryCode = findViewById<EditText>(R.id.editText_searchCity).text.toString()
        var country = ""

        val i = cityWithCountryCode.indexOf(",") // 4

        if (cityWithCountryCode.contains(", ")) {
            country = cityWithCountryCode.substring(i + 2)
        } else if (cityWithCountryCode.contains(",")) {
            country = cityWithCountryCode.substring(i + 1)
        }

        return country
    }


    // main function to get weather data from OpenWeatherMap
    fun getWeatherData () {

        val textViewMsg = findViewById<TextView>(R.id.textView_message)

        var city = findViewById<EditText>(R.id.editText_searchCity).text.toString()
        var country = ""

        // checks if user input has country code and parses it
        if (city.contains(",")) {
            city = getCityName()
            country = getCountryCode()
        }

        // if user input is empty feedback is given to user, else the data search is made
        if (city.isEmpty()) {
            textViewMsg.setTextColor(Color.parseColor("#DC143C"))
            textViewMsg.text = "Please type in city's name."
        } else {

            var url = ""

            // modifies the url depending on user input
            if (country.isNotEmpty()) {
                url = "https://api.openweathermap.org/data/2.5/weather?q=$city,$country&units=metric&APPID=189c7531f7a73a421a3c43ba1fc96313"
            } else {
                url = "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&APPID=189c7531f7a73a421a3c43ba1fc96313"
            }

            val queue = Volley.newRequestQueue(applicationContext)

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener { response ->
                    // Process the JSON
                    try {
                        val cityName = response.getString("name")

                        val sys = response.getJSONObject("sys")
                        val countryName = sys.getString("country")

                        val weatherArray = response.getJSONArray("weather")
                        val weatherItem = weatherArray.getJSONObject(0)
                        val description = weatherItem.getString("description")

                        val wind = response.getJSONObject("wind")
                        val windSpeed = wind.getString("speed")

                        val main = response.getJSONObject("main")
                        val temperature = main.getString("temp")
                        val humidity = main.getString("humidity")

                        val intent = Intent(applicationContext, ResultActivity::class.java)

                        // Sent currentBackgroundColor, city, country code, temperature and description as extra
                        intent.putExtra("city", cityName)
                        intent.putExtra("country", countryName)
                        intent.putExtra("temperature", temperature)
                        intent.putExtra("description", description)
                        intent.putExtra("windSpeed", windSpeed)
                        intent.putExtra("humidity", humidity)


                        startActivity(intent)
                    } catch (e: JSONException) {
                        textViewMsg.setTextColor(Color.parseColor("#DC143C"))
                        textViewMsg.setText("Oops! Cannot connect to OpenWeatherMap, please try again.")
                        Toast.makeText(applicationContext, "JSONException occurred", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    // Do something when error occurred
                    textViewMsg.setTextColor(Color.parseColor("#DC143C"))
                    textViewMsg.setText("No city found by that name, please try again.")
                }
            )
            // Add JsonObjectRequest to the RequestQueue
            queue.add(jsonObjectRequest)
        }
    }

}
