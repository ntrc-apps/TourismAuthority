package com.example.navigationcomponent.ui.home

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
import androidx.lifecycle.ViewModelProviders
import com.example.navigationcomponent.R
import com.example.navigationcomponent.custom_classes.TourismSite
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class HomeFragment : Fragment(), OnMapReadyCallback, MapboxMap.OnMarkerClickListener {
    private lateinit var mMapView: MapView
    private lateinit var mMapboxMap: MapboxMap
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var locationComponent: LocationComponent

    var defaultDirections: String? = null
    var defaultDirectionsOn = false
    val DRIVING_DIRECTIONS = "Driving Directions"
    val WALKING_DIRECTIONS = "Walking Directions"

    private var siteList: ArrayList<TourismSite> = ArrayList()
    private val getSitesURL = "https://cert-manager.ntrcsvg.com/tourism/getTourismSites.php"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        /*frameLayout.visibility = View.VISIBLE
        siteInfo.visibility = View.GONE*/

        mMapView = root.findViewById(R.id.mapView)
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)

        return root
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let { Mapbox.getInstance(it, getString(R.string.access_token)) }
    }

    private fun displaySiteInfo(title: String){
        frameLayout.visibility = View.GONE
        siteInfo.visibility = View.VISIBLE


        close_info.setOnClickListener {
            frameLayout.visibility = View.VISIBLE
            siteInfo.visibility = View.GONE
        }

        for (site in siteList){
            if (title == site.name){
                site_name.text = site.name
                description.text = site.description

                Picasso.with(context).load(site.image).placeholder(R.drawable.ic_launcher_foreground).into(siteImage)
            }
        }

        directions.setOnClickListener {
            /*checkLocationEnginePermission()
            if (locationComponent.getLastKnownLocation() != null) {
                val origin = Point.fromLngLat(
                    locationComponent.getLastKnownLocation()!!.getLongitude(),
                    locationComponent.getLastKnownLocation()!!.getLatitude()
                )
                val destination =
                    Point.fromLngLat(lng, lat)
                when (defaultDirections) {
                    WALKING_DIRECTIONS -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Showing Walking Directions",
                            Toast.LENGTH_SHORT
                        ).show()
                        launchNavigation(origin, destination, DirectionsCriteria.PROFILE_WALKING)
                    }
                    DRIVING_DIRECTIONS -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Showing Driving Directions",
                            Toast.LENGTH_SHORT
                        ).show()
                        launchNavigation(origin, destination, DirectionsCriteria.PROFILE_DRIVING)
                    }
                }
            } else if (lastKnownLocation != null) {
                val origin = Point.fromLngLat(
                    lastKnownLocation.getLongitude(),
                    lastKnownLocation.getLatitude()
                )
                val destination =
                    Point.fromLngLat(lng, lat)
                when (defaultDirections) {
                    WALKING_DIRECTIONS -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Showing Walking Directions",
                            Toast.LENGTH_SHORT
                        ).show()
                        launchNavigation(origin, destination, DirectionsCriteria.PROFILE_WALKING)
                    }
                    DRIVING_DIRECTIONS -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Showing Driving Directions",
                            Toast.LENGTH_SHORT
                        ).show()
                        launchNavigation(origin, destination, DirectionsCriteria.PROFILE_DRIVING)
                    }
                }
            } else {
                mMapView.post {
                    Toast.makeText(
                        getBaseContext(),
                        "Your location is not found, please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }*/
        }
    }

    private fun loadMarkers(){
        AsyncTask.execute {
            /* This method receives the URL to a PHP code hosted on the web server
            * then tries connects to it.
            * The PHP then executes a stored procedure on the database and return
            * the relevant information
            */

            val `is`: InputStream

            val link = URL(getSitesURL)
            val conn = link.openConnection() as HttpURLConnection
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
                        for (i in 0 until jArray.length()) {
                            val siteInfo = jArray.getJSONObject(i)

                            val site = TourismSite()

                            site.id = siteInfo.getInt("siteid")
                            site.name = siteInfo.getString("sitename")
                            site.description = siteInfo.getString("description")
                            site.latitude = siteInfo.getDouble("latitude")
                            site.longitude = siteInfo.getDouble("longitude")
                            site.image = siteInfo.getString("image_url")

                            siteList.add(site)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } finally {
                        conn.disconnect()
                        mMapView.post {
                            displayMarkers()
                        }
                        synchronized(this) {}
                    }
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Log.e("log_tag", "Error in http connection $e")
                    Toast.makeText(context, "Couldn't connect to database", Toast.LENGTH_SHORT).show()
                }

            } finally {
                conn.disconnect()
            }
        }
    }

    private fun displayMarkers(){
        //val markerManager = MarkerViewManager(mMapView, mMapboxMap)
        for (site in siteList){
//            // Use an XML layout to create a View object
//            // Use an XML layout to create a View object
//            val customView: View =
//                LayoutInflater.from(context).inflate(R.layout.marker_view_bubble, null)
//            customView.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
//            val marker = MarkerView(LatLng(site.latitude,site.longitude), mMapView)
//
//            marker.let {
//                markerManager.addMarker(it)
//            }

            mMapboxMap.addMarker(MarkerOptions()
                    .position(LatLng(site.latitude, site.longitude))
                    //.setTitle(site.name)
                    .snippet(site.name))
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mMapboxMap = mapboxMap

        mapboxMap.setStyle(Style.Builder().fromUri("mapbox://styles/ntrcsvg/cii5zp5ut006xa3lv9zf1jhuc")) {
            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
        }

        mMapboxMap.setOnMarkerClickListener(this)

        loadMarkers()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        displaySiteInfo(marker.snippet)

        return false
    }
}
