package com.example.navigationcomponent.ui.tourguide

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationcomponent.R
import com.example.navigationcomponent.SearchableActivity
import com.example.navigationcomponent.TourDetails
import com.example.navigationcomponent.TourRecyclerAdapter

import com.mapbox.mapboxsdk.maps.MapView
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

class TourFragment : Fragment(), SearchView.OnQueryTextListener {


    private lateinit var shareViewModel: ShareViewModel
    private var toursList: ArrayList<TourDetails> = ArrayList()
    private lateinit var search: SearchView
    private lateinit var adapter: TourRecyclerAdapter
    lateinit var recycler: RecyclerView

    // This fragment loads the images however it is not responsible for the opening of more shop information. That would be ShopActivity.


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tour, container, false)
        shareViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        search = root.findViewById(R.id.tourguide_search)
        search.setOnQueryTextListener(this)

        recycler = root.findViewById(R.id.tour_recylerview)

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AsyncTask.execute{
            val `is`: InputStream
            val link =
                    URL("http://ec2-54-237-130-84.compute-1.amazonaws.com/tourism/getTourguides.php")
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
        adapter = TourRecyclerAdapter(list, context as Activity)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)
        //tour_recylerview.adapter = activity?.let { TourRecyclerAdapter(list, it) }!!
        //tour_recylerview.layoutManager = LinearLayoutManager(context)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.e("Search", "Send")
        var intent = Intent(context, SearchableActivity::class.java).apply {
            putExtra("Query", query)
        }
        startActivity(intent)

        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.e("LEtter", newText.toString())
        adapter.filter.filter(newText)
        return false
    }

}