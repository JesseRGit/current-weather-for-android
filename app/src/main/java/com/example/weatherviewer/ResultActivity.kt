package com.example.weatherviewer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ResultActivity : AppCompatActivity() {

    private var city = ""
    private var country = ""
    private var temperature = ""
    private var description = ""
    private var windSpeed = ""
    private var humidity = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val textViewTitle = findViewById<TextView>(R.id.textView_title_result)

        val textViewTemperature = findViewById<TextView>(R.id.textView_temp)
        val textViewTemperatureValue = findViewById<TextView>(R.id.textView_temp_value)

        val textViewCondition = findViewById<TextView>(R.id.textView_condition)
        val textViewConditionValue = findViewById<TextView>(R.id.textView_condition_value)

        val textViewWindSpeed = findViewById<TextView>(R.id.textView_windSpeed)
        val textViewWindSpeedValue = findViewById<TextView>(R.id.textView_windSpeed_value)

        val textViewHumidity = findViewById<TextView>(R.id.textView_humidity)
        val textViewHumidityValue = findViewById<TextView>(R.id.textView_humidity_value)

        // get intent extras
        val bundle = intent.extras
        if (bundle != null) {
            city = bundle.getString("city")
            country = bundle.getString("country")
            temperature = bundle.getString("temperature")
            description = bundle.getString("description")
            windSpeed = bundle.getString("windSpeed")
            humidity = bundle.getString("windSpeed")
        }

        textViewTitle.setText(city + ", " + country)

        textViewTemperature.setText("Temperature")
        textViewTemperatureValue.setText(temperature + " °C")

        textViewCondition.setText("Condition")
        textViewConditionValue.setText(description)

        textViewWindSpeed.setText("Wind speed")
        textViewWindSpeedValue.setText(windSpeed + " m/s")

        textViewHumidity.setText("Humidity")
        textViewHumidityValue.setText(humidity + " %")
    }

}
