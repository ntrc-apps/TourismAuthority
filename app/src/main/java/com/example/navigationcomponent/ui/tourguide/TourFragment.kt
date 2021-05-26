package com.example.navigationcomponent.ui.tourguide

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigationcomponent.R
import com.example.navigationcomponent.TourDetails
import com.example.navigationcomponent.TourRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_tour.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class TourFragment : Fragment() {


    private lateinit var ShareViewModel: ShareViewModel
    private var toursList: ArrayList<TourDetails> = ArrayList()

    // This fragment loads the images however it is not responsible for the opening of more shop information. That would be ShopActivity.


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        ShareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tour, container, false)
        ShareViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AsyncTask.execute{
            val `is`: InputStream
            val link =
                    URL("http://cert-manager.ntrcsvg.com/tourism/getTourguides.php")
            var conn = link.openConnection() as HttpURLConnection
            var resultList = ""
            try {
                conn.doInput = true
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    `is` = BufferedInputStream(conn.inputStream)
                    val reader =
                            BufferedReader(InputStreamReader(`is`, "iso-8859-1"), 8)
                    val sb = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        sb.append(line)
                        sb.append("\n")
                    }
                    `is`.close()
                    reader.close()
                    resultList = sb.toString()
                    Log.e("Result", resultList)
                    try {
                        val jArray = JSONArray(resultList)
                        Log.e("Result2", jArray.length().toString())
                        for (i in 0 until jArray.length()) {
                            val tourInfoAll: JSONObject = jArray.getJSONObject(i)

                            val newTourDetails = TourDetails()
                            newTourDetails.tourId = tourInfoAll.getInt("tourguide_id")
                            newTourDetails.tourName = tourInfoAll.getString("tourguide_name")
                            newTourDetails.tourDesc = tourInfoAll.getString("tourguide_desc")
                            newTourDetails.tourPhone = tourInfoAll.getString("tourguide_num")
                            newTourDetails.tourLocation = tourInfoAll.getString("tourguide_location")
                            newTourDetails.tourHours = tourInfoAll.getString("tourguide_openhrs")
                            newTourDetails.tourLink= tourInfoAll.getString("tourguide_link")
                            newTourDetails.tourImage= tourInfoAll.getString("tourimageurl")
                            toursList.add(newTourDetails)

                        }}
                    catch (e: JSONException) {
                        e.printStackTrace()
                    } finally {
                        conn.disconnect()
                        tour_recylerview.post(Runnable {
                            loadRecyclerView(toursList)
                        })

                        synchronized(this) {}
                    }

                }
            }catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Log.e("log_tag", "Error in http connection $e")
                    Toast.makeText(context, "Couldn't connect to database", Toast.LENGTH_SHORT).show()
                }

            } finally {
                conn.disconnect()
            }
        }

    }

    private fun loadRecyclerView(list: ArrayList<TourDetails>) {
        tour_recylerview.adapter = activity?.let { TourRecyclerAdapter(list, it) }
        tour_recylerview.layoutManager = LinearLayoutManager(context)
    }

}