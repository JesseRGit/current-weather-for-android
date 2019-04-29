package com.example.weatherviewer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ResultActivity : AppCompatActivity() {

    private var city = ""
    private var country = ""
    private var temperature = ""
    private var description = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val textViewTitle = findViewById<TextView>(R.id.textView_title_result)
        val textViewInfo1 = findViewById<TextView>(R.id.textView_info1)
        val textViewInfo2 = findViewById<TextView>(R.id.textView_info2)
        val textViewInfo3 = findViewById<TextView>(R.id.textView_info3)

        // get intent extra
        val bundle = intent.extras
        if (bundle != null) {
            city = bundle.getString("city")
            country = bundle.getString("country")
            temperature = bundle.getString("temperature")
            description = bundle.getString("description")

        }

        textViewTitle.setText(city + ", " + country)
        textViewInfo1.setText("Temperature: " + temperature + " °C")
        textViewInfo2.setText("Description: " + city + " has " + description + ".")

    }


}
