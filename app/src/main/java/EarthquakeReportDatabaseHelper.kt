import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EarthquakeReportDatabaseHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private const val DB_NAME = "earthquakeReportDatabase"
        private const val DB_VERSION = 1
    }
    override fun onCreate(p0: SQLiteDatabase?) {
        val query = """
            CREATE TABLE reported_earthquakes (
                _id INTEGER PRIMARY KEY,
                country TEXT,
                state TEXT,
                city TEXT,
                magnitude INTEGER,
                date TEXT
            );
        """.trimIndent()
        p0?.execSQL(query)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

}