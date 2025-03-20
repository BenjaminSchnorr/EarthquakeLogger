package com.example.final_project_435

import EarthquakeReportDatabaseHelper
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Vibrator
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EarthquakeEntryActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var earthquakeReportDatabaseHelper: EarthquakeReportDatabaseHelper
    private lateinit var sqLiteDatabase: SQLiteDatabase
    private lateinit var editTextCountry : EditText
    private lateinit var editTextState : EditText
    private lateinit var editTextCity : EditText
    private lateinit var textView: TextView
    private lateinit var editTextMag: EditText
    private lateinit var vibrator: Vibrator
    private lateinit var error: TextView
    var date = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_earthquake_entry)
        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        editTextCountry = findViewById(R.id.editTextCountry)
        editTextState = findViewById(R.id.editTextState)
        editTextCity= findViewById(R.id.editTextCity)
        textView = findViewById(R.id.textViewCountry)
        editTextMag= findViewById(R.id.editTextMag)
        error = findViewById(R.id.error)
        //https://in-kotlin.com/android/edittext/inputtypes/
        //change the edit text input type so it only excepts numbers
        editTextMag.inputType = InputType.TYPE_CLASS_NUMBER

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        earthquakeReportDatabaseHelper = EarthquakeReportDatabaseHelper(applicationContext)
        sqLiteDatabase = earthquakeReportDatabaseHelper.readableDatabase
    }
    //https://medium.com/@hasangurgur95/using-vibration-in-android-building-a-simple-app-to-demonstrate-vibration-afc59995479a
    //https://developer.android.com/reference/android/os/VibrationEffect#inherited-constants
    fun startVibration(mag: Int){
        var mag = mag * 1000L
        val vibrationDuration = mag
        vibrator.vibrate(vibrationDuration)
    }

    fun onSubmit(view: View){
        error.text= ""
        var country =editTextCountry.text.toString()
        var state = editTextState.text.toString()
        var city = editTextCity.text.toString()

        var mag = editTextMag.text.toString()


        if (country.isEmpty() || state.isEmpty() || city.isEmpty() || date.isEmpty() || mag.isEmpty()){
            error.text = "ERROR: Fill in all fields"
        }
        else if (mag.toInt() < 1 || mag.toInt() > 10){
            error.text = "ERROR: Magnitude must be between 1 - 10"
        }
        else{
            val earthquakeReport = ContentValues().apply {
                put("country", country)
                put("state", state)
                put("city", city)
                put("magnitude", mag)
                put("date", date)
            }
            sqLiteDatabase.insert("reported_earthquakes", null, earthquakeReport)
            startVibration(mag.toInt())
            error.text = "Form Submitted"
            date = ""
            editTextCountry.setText("")
            editTextState.setText("")
            editTextCity.setText("")
            editTextMag.setText("")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.E ->{
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true

    }

    fun onClickDatePicker(view : View){
        val dateFragment = DateFragment()
        dateFragment.show(supportFragmentManager, null)
    }
    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int){
        date = String.format("%d/%d/%d", month + 1, day, year)
    }
}