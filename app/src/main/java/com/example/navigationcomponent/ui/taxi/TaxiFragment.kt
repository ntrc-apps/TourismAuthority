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
import com.example.navigationcomponent.com.example.navigationcomponent.TaxiDetails
import com.example.navigationcomponent.com.example.navigationcomponent.TaxiRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_taxi.*
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

class TaxiFragment : Fragment() {


    private lateinit var shareViewModel: TaxiShareViewModel
    private var taxiList: ArrayList<TaxiDetails> = ArrayList()

    // This fragment loads the images however it is not responsible for the opening of more shop information. That would be ShopActivity.


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        shareViewModel =
                ViewModelProviders.of(this).get(TaxiShareViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_taxi, container, false)
        shareViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AsyncTask.execute{
            val `is`: InputStream
            val link =
                    URL("http://ec2-54-237-130-84.compute-1.amazonaws.com/tourism/getTaxis.php")
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
                            val taxiInfoAll: JSONObject = jArray.getJSONObject(i)

                            val newTaxiDetails = TaxiDetails()
                            newTaxiDetails.taxiId = taxiInfoAll.getInt("taxi_id")
                            newTaxiDetails.taxiName = taxiInfoAll.getString("taxi_name")
                            newTaxiDetails.taxiDesc = taxiInfoAll.getString("taxi_desc")
                            newTaxiDetails.taxiPhone = taxiInfoAll.getString("taxi_number")
                            newTaxiDetails.taxiLocation = taxiInfoAll.getString("taxi_location")
                            newTaxiDetails.taxiHours = taxiInfoAll.getString("taxi_openhrs")
                            newTaxiDetails.taxiLink= taxiInfoAll.getString("taxi_link")
                            newTaxiDetails.taxiImage= taxiInfoAll.getString("taxiimageurl")
                            taxiList.add(newTaxiDetails)

                        }}
                    catch (e: JSONException) {
                        e.printStackTrace()
                    } finally {
                        conn.disconnect()
                        taxi_recylerview.post(Runnable {
                            loadRecyclerView(taxiList)
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

    private fun loadRecyclerView(list: ArrayList<TaxiDetails>) {
        taxi_recylerview.adapter = activity?.let { TaxiRecyclerAdapter(list, it) }
        taxi_recylerview.layoutManager = LinearLayoutManager(context)
    }

}