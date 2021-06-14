package com.example.navigationcomponent.com.example.navigationcomponent

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.navigationcomponent.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_taxi.*
import kotlinx.android.synthetic.main.activity_tour.*
import kotlinx.android.synthetic.main.activity_tour.buttonCall
import kotlinx.android.synthetic.main.activity_tour.buttonWeb
import kotlinx.android.synthetic.main.activity_tour.tour_hours
import kotlinx.android.synthetic.main.activity_tour.tour_location
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class TaxiActivity : AppCompatActivity() {

        var taxiID = 0
        var resultString = ""


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_taxi)

            val button = findViewById<View>(R.id.buttonCall) as Button
            val extras = intent.extras
            if (extras != null) {
                taxiID = extras.getInt("taxi_id")
            }

            AsyncTask.execute{
                /* This method receives the URL to a PHP code hosted on the web server
             * then tries connects to it.
             * The PHP then executes a stored procedure on the database and return
             * the relevant information
             */
                val `is`: InputStream
                val data = java.lang.StringBuilder()

                val link = URL("http://ec2-54-237-130-84.compute-1.amazonaws.com/tourism/getSelectedTaxi.php")
                var conn: HttpURLConnection
                conn = link.openConnection() as HttpURLConnection

                try {
                    data.append(URLEncoder.encode("taxiID", "UTF-8"))
                    data.append("=")
                    data.append(URLEncoder.encode(taxiID.toString(), "UTF-8"))

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
                                val taxiInfoAll: JSONObject = jArray.getJSONObject(i)

                                image_taxi.post {
                                    taxi_name.text = taxiInfoAll.getString("taxi_name")
                                    taxi_desc.text = taxiInfoAll.getString("taxi_desc")
                                    Picasso.get().load(taxiInfoAll.getString("taxiimageurl"))
                                        .placeholder(R.drawable.cruise).into(image_taxi)

                                    // Displays Views once info is present
                                    if (taxiInfoAll.getString("taxi_location").isEmpty()) {
                                        tour_location.visibility = View.GONE
                                    } else {
                                        tour_location.text = taxiInfoAll.getString("taxi_location")
                                    }
                                    if (taxiInfoAll.getString("taxi_openhrs").isEmpty()) {
                                        tour_hours.visibility = View.GONE
                                    } else {
                                        tour_hours.text = taxiInfoAll.getString("taxi_openhrs")
                                    }
                                    if (taxiInfoAll.getString("taxi_number").isEmpty()) {
                                        buttonCall.visibility = View.GONE
                                    } else {
                                        buttonCall.text = taxiInfoAll.getString("taxi_number")
                                        buttonCall.setOnClickListener {
                                            val intent = Intent(Intent.ACTION_CALL)
                                            intent.data =
                                                Uri.parse("tel:" + taxiInfoAll.getString("tourguide_num"))
                                            startActivity(intent)
                                        }
                                    }
                                    if (taxiInfoAll.getString("taxi_link").isEmpty()) {
                                        webButton.visibility = View.GONE
                                    } else {
                                        webButton.text = taxiInfoAll.getString("taxi_link")
                                        webButton.setOnClickListener {
                                            val intent = Intent(Intent.ACTION_VIEW)
                                            intent.data =
                                                Uri.parse(taxiInfoAll.getString("taxi_link"))
                                            startActivity(intent)
                                        }
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

