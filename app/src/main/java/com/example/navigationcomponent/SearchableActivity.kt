package com.example.navigationcomponent

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_tour.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class SearchableActivity : AppCompatActivity() {

    var search = ""
    var resultString = ""

    val button = findViewById<View>(R.id.buttonCall) as Button
    val extras = intent.extras


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_tour)

        if (extras != null) {
            search = extras.getInt("tourguide_name").toString()
        }
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                doMySearch(query)
            }
        }

    }

    fun doMySearch(query: String) {

        AsyncTask.execute {
            /* This method receives the URL to a PHP code hosted on the web server
     * then tries connects to it.
     * The PHP then executes a stored procedure on the database and return
     * the relevant information
     */
            val `is`: InputStream
            val data = java.lang.StringBuilder()

            val link =
                URL("http://ec2-54-237-130-84.compute-1.amazonaws.com/tourism/searchTourguide.php")
            var conn: HttpURLConnection = link.openConnection() as HttpURLConnection

            try {
                data.append(URLEncoder.encode("search", "UTF-8"))
                data.append("=")
                data.append(URLEncoder.encode(search, "UTF-8"))

                conn.readTimeout = 10000
                conn.connectTimeout = 15000
                conn.requestMethod = "POST"
                conn.doInput = true
                conn.doOutput = true
                conn.connect()
                val os: OutputStream = conn.outputStream
                val osWriter = OutputStreamWriter(os, "UTF-8")
                val writer = BufferedWriter(osWriter)
                writer.write(data.toString())
                writer.flush()
                writer.close()
                os.close()
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    `is` = BufferedInputStream(conn.inputStream)
                    val reader =
                        BufferedReader(InputStreamReader(`is`, "iso-8859-1"), 8)
                    val sb = java.lang.StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        sb.append(line)
                        sb.append("\n")
                    }
                    `is`.close()
                    reader.close()
                    resultString = sb.toString()

                    try {
                        val jArray = JSONArray(resultString)
                        Log.e("Result2", jArray.length().toString())
                        for (i in 0 until jArray.length()) {
                            val tourInfoAll: JSONObject = jArray.getJSONObject(i)

                            image.post {
                                tour_name.text = tourInfoAll.getString("tourguide_name")
                                tour_desc.text = tourInfoAll.getString("tourguide_desc")
                                Picasso.get().load(tourInfoAll.getString("tourimageurl"))
                                    .placeholder(R.drawable.cruise).into(image)

                                // Displays Views once info is present
                                if (tourInfoAll.getString("tourguide_location").isEmpty()) {
                                    tour_location.visibility = View.GONE
                                } else {
                                    tour_location.text =
                                        tourInfoAll.getString("tourguide_location")
                                }
                                if (tourInfoAll.getString("tourguide_openhrs").isEmpty()) {
                                    tour_hours.visibility = View.GONE
                                } else {
                                    tour_hours.text = tourInfoAll.getString("tourguide_openhrs")
                                }
                                if (tourInfoAll.getString("tourguide_num").isEmpty()) {
                                    buttonCall.visibility = View.GONE
                                } else {
                                    buttonCall.text = tourInfoAll.getString("tourguide_num")
                                    buttonCall.setOnClickListener {
                                        val intent = Intent(Intent.ACTION_CALL)
                                        intent.data =
                                            Uri.parse("tel:" + tourInfoAll.getString("tourguide_num"))
                                        startActivity(intent)
                                    }
                                }
                                if (tourInfoAll.getString("tourguide_link").isEmpty()) {
                                    buttonWeb.visibility = View.GONE
                                } else {
                                    buttonWeb.text = tourInfoAll.getString("tourguide_link")
                                    buttonWeb.setOnClickListener {
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.data =
                                            Uri.parse(tourInfoAll.getString("tourguide_link"))
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } finally {
                        conn.disconnect()
                        synchronized(this) {}
                    }
                }
            } catch (e: java.lang.Exception) {
                Looper.prepare()
                Log.e("log_tag", "Error in http connection $e")
                Toast.makeText(this, "Couldn't connect to database", Toast.LENGTH_SHORT).show()
            } finally {
                conn.disconnect()
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.searchable, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.tourguide_search).actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isIconifiedByDefault = false // Do not iconify the widget; expand it by default
        }

        return true
    }

}
