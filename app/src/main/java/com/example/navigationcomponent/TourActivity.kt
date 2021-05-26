package com.example.navigationcomponent

import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_tour.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class TourActivity : AppCompatActivity() {

        var tourID = 0
        var resultString = ""
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_tour)

            val extras = intent.extras

            if (extras != null) {
                tourID = extras.getInt("tourguide_id")
            }

            AsyncTask.execute{
                /* This method receives the URL to a PHP code hosted on the web server
             * then tries connects to it.
             * The PHP then executes a stored procedure on the database and return
             * the relevant information
             */
                val `is`: InputStream
                val data = java.lang.StringBuilder()

                val link = URL("http://cert-manager.ntrcsvg.com/tourism/getTourguides.php")
                var conn: HttpURLConnection
                conn = link.openConnection() as HttpURLConnection

                try {
                    data.append(URLEncoder.encode("tourID", "UTF-8"))
                    data.append("=")
                    data.append(URLEncoder.encode(tourID.toString(), "UTF-8"))

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

                                _image.post {
                                    tour_name.text = tourInfoAll.getString("tourguide_name")
                                    tour_desc.text = tourInfoAll.getString("tourguide_desc")
                                    Picasso.get().load(tourInfoAll.getString("tourimageurl")).placeholder(R.drawable.cruise).into(_image)

                                    // Displays Views once info is present
                                    if ( tourInfoAll.getString("tourguide_location").isEmpty()){
                                        tour_location.visibility = View.GONE
                                    } else {
                                        tour_location.text = tourInfoAll.getString("tourguide_location")
                                    }
                                    if ( tourInfoAll.getString("tourguide_openhrs").isEmpty()){
                                        tour_hours.visibility = View.GONE
                                    } else {
                                        tour_hours.text = tourInfoAll.getString("tourguide_openhrs")
                                    }
                                    if ( tourInfoAll.getString("tourguide_num").isEmpty()){
                                        buttonCall.visibility = View.GONE
                                    } else {
                                        buttonCall.text = tourInfoAll.getString("tourguide_num")
                                    }
                                    if ( tourInfoAll.getString("tourguide_link").isEmpty()){
                                        buttonWeb.visibility = View.GONE
                                    } else {
                                        buttonWeb.text = tourInfoAll.getString("tourguide_link")
                                    }
                                }


                            }}
                        catch (e: JSONException) {
                            e.printStackTrace()
                        } finally {
                            conn.disconnect()
                            synchronized(this) {}
                        }
                    }
                } catch (e: java.lang.Exception) {
                    Looper.prepare()
                    Log.e("log_tag", "Error in http connection $e")
                    Toast.makeText(this,"Couldn't connect to database", Toast.LENGTH_SHORT).show()
                } finally {
                    conn.disconnect()
                }

            }

        }

    }

