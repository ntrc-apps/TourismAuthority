package com.example.navigationcomponent.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.icu.number.NumberFormatter.with
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide.with
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.with
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.with
import com.example.navigationcomponent.R
import com.example.navigationcomponent.custom_classes.TourismSite
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class HomeFragment : Fragment(), OnMapReadyCallback, MapboxMap.OnMarkerClickListener, PermissionsListener {
    private lateinit var mMapView: MapView
    private lateinit var mMapboxMap: MapboxMap
    private lateinit var homeViewModel: HomeViewModel

    var defaultDirections: String? = null
    var defaultDirectionsOn = false
    val DRIVING_DIRECTIONS = "Driving Directions"
    val WALKING_DIRECTIONS = "Walking Directions"

    private var siteList: ArrayList<TourismSite> = ArrayList()
    private val getSitesURL = "https://cert-manager.ntrcsvg.com/tourism/getTourismSites.php"

    private var permissionsManager: PermissionsManager? = null
    private var locationComponent: LocationComponent? = null
    private var currentRoute: DirectionsRoute? = null
    private var navigationMapRoute: NavigationMapRoute? = null

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
        var destinationPoint = Point.fromLngLat(0.0, 0.0)
        val originPoint = Point.fromLngLat(
                locationComponent!!.lastKnownLocation!!.longitude,
                locationComponent!!.lastKnownLocation!!.latitude
        )

        close_info.setOnClickListener {
            frameLayout.visibility = View.VISIBLE
            siteInfo.visibility = View.GONE
        }

        for (site in siteList){
            if (title == site.name){
                site_name.text = site.name
                description.text = site.description
                destinationPoint = Point.fromLngLat(site.longitude, site.latitude)

                Picasso.get().load(site.image).placeholder(R.drawable.cruise).into(siteImage);

            }
        }

        directions.setOnClickListener {
            Log.e("Directions", "Clicked")
            Log.e("Points", originPoint.toString())
            Log.e("Points", destinationPoint.toString())
            getRoute(originPoint, destinationPoint)

            //getRoute(originPoint, destinationPoint)
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

        mapboxMap.setStyle(Style.Builder().fromUri("mapbox://styles/ntrcsvg/ckfzg19z518uq1aooxq5zwdf9")) {
            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
            enableLocationComponent(it)
        }


        mMapboxMap.setOnMarkerClickListener(this)

        loadMarkers()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        displaySiteInfo(marker.snippet)

        return false
    }

    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mMapboxMap.locationComponent
            locationComponent!!.activateLocationComponent(requireContext(), loadedMapStyle)
            if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            locationComponent!!.isLocationComponentEnabled = true
            // Set the component's camera mode
            locationComponent!!.cameraMode = CameraMode.TRACKING
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager!!.requestLocationPermissions(activity)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        //Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG)
                //.show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mMapboxMap.style!!)
        } else {
            //Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG)
                    //.show()
            activity?.finish()
        }
    }

    private fun getRoute(origin: Point, destination: Point) {
        Log.e("getRoute", "Here")

        NavigationRoute.builder(activity)
                .accessToken(getString(R.string.access_token))
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(object : Callback<DirectionsResponse?> {
                    override fun onResponse(
                            call: Call<DirectionsResponse?>,
                            response: Response<DirectionsResponse?>
                    ) {
                        // You can get the generic HTTP info about the response
                        //Log.d(Map.Companion.TAG, "Response code: " + response.code())
                        Log.e("Response", "Response code: " + response.code())
                        if (response.body() == null) {
                            /*Log.e(
                                    Map.Companion.TAG,
                                    "No routes found, make sure you set the right user and access token."
                            )*/
                            Log.e("Routes", "No routes found, make sure you set the right user and access token.")
                            return
                        } else if (response.body()!!.routes().size < 1) {
                            //Log.e(Map.Companion.TAG, "No routes found")
                            Log.e("Routes", "No routes found")
                            return
                        }
                        currentRoute = response.body()!!.routes()[0]

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute!!.removeRoute()
                        } else {
                            navigationMapRoute = NavigationMapRoute(
                                    null,
                                    mapView,
                                    mMapboxMap,
                                    R.style.NavigationMapRoute
                            )
                        }
                        navigationMapRoute!!.addRoute(currentRoute)

                        val options = NavigationLauncherOptions.builder()
                                .directionsRoute(currentRoute)
                                .shouldSimulateRoute(false)
                                .build()
                        // Call this method with Context from within an Activity
                        NavigationLauncher.startNavigation(activity, options)
                    }

                    override fun onFailure(call: Call<DirectionsResponse?>, throwable: Throwable) {
                        //Log.e(Map.Companion.TAG, "Error: " + throwable.message)
                        Log.e("Directions", "Failed")
                    }
                })
    }
}
