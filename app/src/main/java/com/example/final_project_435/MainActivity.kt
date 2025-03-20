package com.example.final_project_435

import EarthquakeDatabaseHelper
import ItemAdapter
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project_435.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.System.currentTimeMillis
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var cursor: Cursor
    private lateinit var earthquakeDatabaseHelper: EarthquakeDatabaseHelper
    private lateinit var sqLiteDatabase: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        earthquakeDatabaseHelper = EarthquakeDatabaseHelper(applicationContext)

        sqLiteDatabase = earthquakeDatabaseHelper.readableDatabase
        //day in milli 86400000
        var day = 86400000
        var time = (currentTimeMillis()) - (2 * day)
        var formatter = SimpleDateFormat("yyyy-MM-dd")
        var date = Date()
        var date2 = Date(time)
        var dateOneDayAgo = formatter.format(date2)
        var currentDate = formatter.format(date)
        Log.v("TIME", time.toString())

        val cursor = sqLiteDatabase.rawQuery("SELECT * FROM earthquakes WHERE time > ?", arrayOf(time.toString()))
        cursor.moveToFirst()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val itemAdapter = ItemAdapter(cursor)
        recyclerView.adapter = itemAdapter
        itemAdapter.notifyDataSetChanged()
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        getRecentEarthquakes(dateOneDayAgo, currentDate)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.A ->{
                val intent = Intent(applicationContext, EarthquakeEntryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getRecentEarthquakes(date1: String, date2: String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var result = ""
                var httpURLConnection: HttpURLConnection? = null

                var url = URL("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + date1 +"&endtime="+ date2)
                httpURLConnection = url.openConnection() as HttpURLConnection
                val url_string = ("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + date1 +"&endtime="+ date2)
                Log.v("URL", url_string)
                httpURLConnection.requestMethod = "GET"
                delay(1000)
                if (httpURLConnection.responseCode != HttpURLConnection.HTTP_OK) {
                    result = "BAD CONNECTION"
                } else {
                    val inputStreamReader = httpURLConnection.inputStream
                    val bufferedReader = BufferedReader(InputStreamReader(inputStreamReader))
                    result = bufferedReader.readText()
                    bufferedReader.close()
                }
                var jsonObject = JSONObject(result)
                var features = jsonObject.getJSONArray("features")
                var length = features.length()

                for(i in 0..length - 1) {
                    var earthQuake = features.getJSONObject(i)
                    var properties = earthQuake.getJSONObject("properties")
                    var magnitude = properties.getInt("mag")
                    var place = properties.getString("place")
                    var time = properties.getLong("time")
                    var event_id = earthQuake.getString("id")

                    val earthquakeVal = ContentValues().apply {
                        put("_id", i)
                        put("place", place)
                        put("time", time)
                        put("mag", magnitude)
                        put("event_id", event_id)
                    }

                    sqLiteDatabase.insertWithOnConflict("earthquakes", null, earthquakeVal, SQLiteDatabase.CONFLICT_IGNORE)
                }
            } catch (e: Exception) {
                Log.d("ERROR", e.toString())
            }

        }
    }
}