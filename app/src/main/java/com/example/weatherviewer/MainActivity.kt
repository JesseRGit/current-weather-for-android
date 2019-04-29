package com.example.weatherviewer

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import org.json.JSONException
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.widget.*
import android.location.Geocoder
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextSearch = findViewById<EditText>(R.id.editText_searchCity)
        val textViewMsg = findViewById<TextView>(R.id.textView_message)
        val buttonSearch = findViewById<Button>(R.id.button_search)
        val imageButtonGetLocation = findViewById<ImageButton>(R.id.imageButton_getLocation)

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

        imageButtonGetLocation.setOnClickListener {
            Toast.makeText(this, "GPS BUTTON PRESSED", Toast.LENGTH_SHORT).show()
            getLocation()
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

    // function to get user's location
    fun getLocation() {

        // checks for location permission
        if( ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // No permissions so they are asked
            ActivityCompat.requestPermissions(
                this, arrayOf( Manifest.permission.ACCESS_FINE_LOCATION), 0)
            return
        }

        val editTextSearch = findViewById<EditText>(R.id.editText_searchCity)


        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val longitude = location.longitude
        val latitude = location.latitude

        Toast.makeText(this, "Longitude:" + longitude.toString() + " and latitude: " + latitude.toString() , Toast.LENGTH_SHORT).show()

        val geocoder = Geocoder(this, Locale.getDefault())

        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        val cityName = addresses.get(0).getLocality()

        editTextSearch.setText(cityName)
    }

}
