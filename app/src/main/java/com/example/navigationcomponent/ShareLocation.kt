package com.example.navigationcomponent

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.v5.milestone.Milestone
import com.mapbox.services.android.navigation.v5.milestone.MilestoneEventListener
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShareLocation : AppCompatActivity(), PermissionsListener, OnMapReadyCallback, MilestoneEventListener {
    var latitude = 13.23727191
    var longitude = -61.18835449
    var locName: TextView? = null
    var locInfo: TextView? = null
    lateinit var directionsButt: TextView
    lateinit var mMapView: MapView
    var mMapboxMap: MapboxMap? = null
    var data: String? = null
    var lastKnownLocation: Location? = null
    var navigation: MapboxNavigation? = null
    private var locationComponent: LocationComponent? = null
    val HYBRID_URL = "mapbox://styles/ntrcsvg/cii5zp5ut006xa3lv9zf1jhuc"
    val DRIVING_DIRECTIONS = "Driving Directions"
    val WALKING_DIRECTIONS = "Walking Directions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, getString(R.string.access_token))
        navigation = MapboxNavigation(baseContext, getString(R.string.access_token))
        setContentView(R.layout.activity_share_location)
        Log.e("Activity", "Started")

        // Sets up toolbar
        val toolbar = findViewById<Toolbar>(R.id.app_bar)
        setSupportActionBar(toolbar)
        toolbar.title = "Shared Location"
        toolbar.setTitleTextColor(Color.WHITE)
        assert(supportActionBar != null)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Intent intent = getIntent();
        val uri = intent.data!!
        val params = uri.pathSegments
        for (i in params.indices) {
            Log.e("Param $i", params[i])
        }

        /*data = getIntent().getStringExtra("Shared Location");
        Log.e("Data", data);*/locName = findViewById(R.id.locTitle)
        locInfo = findViewById(R.id.locDesc)
        mMapView = findViewById(R.id.mapViewReceived)
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mMapboxMap = mapboxMap
        mMapboxMap!!.setStyle(Style.Builder().fromUri(HYBRID_URL)) { loadedMapStyle: Style -> enableLocationComponent(loadedMapStyle) }
        mMapboxMap!!.cameraPosition = CameraPosition.Builder().target(LatLng(latitude, longitude)).build()
        Log.e("Camera Pos", "Set")
        //mMapboxMap.setStyle(HYBRID_URL);
        if (data != null) {
            val keyValuePairs = Bundle()
            //String dataString = data.toString();
            val dataArray: Array<String>
            val keyValueStrings: Array<String>
            dataArray = data!!.split("\\?".toRegex()).toTypedArray()
            keyValueStrings = dataArray[1].split("&".toRegex()).toTypedArray()
            for (keyValueString in keyValueStrings) {
                var key: String
                var value: String
                key = keyValueString.split("=".toRegex()).toTypedArray()[0]
                value = keyValueString.split("=".toRegex()).toTypedArray()[1]
                Log.e("key val", key)
                keyValuePairs.putString(key, value.replace("%20".toRegex(), " "))
            }
            mMapboxMap!!.cameraPosition = CameraPosition.Builder().target(
                    LatLng(keyValuePairs.getString("lat")!!.toDouble(), keyValuePairs.getString("lng")!!.toDouble())).build()
            mMapboxMap!!.addMarker(MarkerOptions()
                    .title(keyValuePairs.getString("locname"))
                    .setSnippet(keyValuePairs.getString("locdesc"))
                    .position(LatLng(keyValuePairs.getString("lat")!!.toDouble(), keyValuePairs.getString("lng")!!.toDouble())))
            if (keyValuePairs.getString("locname") != "nil") {
                locName!!.text = keyValuePairs.getString("locname")
            } else {
                locName!!.text = "Untitled"
            }
            if (keyValuePairs.getString("locdesc") != "nil") {
                locInfo!!.text = keyValuePairs.getString("locdesc")
            } else {
                locInfo!!.text = "No Description"
            }
            directionsButt = findViewById(R.id.directions)
            //directionsButt.setOnClickListener(View.OnClickListener { v: View? -> launchNavigation(keyValuePairs.getString("lat")!!.toDouble(), keyValuePairs.getString("lng")!!.toDouble()) })
        }
    }

    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            Log.e("Status", "Permissions Granted")
            locationComponent = mMapboxMap!!.locationComponent

            // Activate with options
            locationComponent!!.activateLocationComponent(LocationComponentActivationOptions.builder(this@ShareLocation, loadedMapStyle).build())

            // Enable to make component visible
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            locationComponent!!.renderMode = RenderMode.COMPASS
            lastKnownLocation = locationComponent!!.lastKnownLocation

            /*locationEnabled = locationComponent.isLocationComponentEnabled();

            initLocationEngine();
            displayLocation();*/
        } else {
            Log.e("Status", "Permissions not granted")
            val permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    /*private fun launchNavigation(lat: Double, lng: Double) {
        if (locationComponent!!.lastKnownLocation != null) {
            val origin = Point.fromLngLat(locationComponent!!.lastKnownLocation!!.longitude, locationComponent!!.lastKnownLocation!!.latitude)
            val destination = Point.fromLngLat(lng, lat)
            val dialog = DirectionsDialog()
            dialog.setDialogListener(object : DialogListener() {
                fun onCompleted(choice: String?) {
                    when (choice) {
                        WALKING_DIRECTIONS -> {
                            Log.e("Walking Directions", "Inside")
                            launchNavigation(origin, destination, DirectionsCriteria.PROFILE_WALKING)
                        }
                        DRIVING_DIRECTIONS -> {
                            Log.e("Driving Directions", "Inside")
                            launchNavigation(origin, destination, DirectionsCriteria.PROFILE_DRIVING)
                        }
                    }
                }

                fun onCanceled() {}
            })
            dialog.show(supportFragmentManager, "Directions")
        } else if (lastKnownLocation != null) {
            val origin = Point.fromLngLat(lastKnownLocation!!.longitude, lastKnownLocation!!.latitude)
            val destination = Point.fromLngLat(lng, lat)
            val dialog = DirectionsDialog()
            dialog.setDialogListener(object : DialogListener() {
                fun onCompleted(choice: String?) {
                    //String criteria;
                    when (choice) {
                        WALKING_DIRECTIONS -> {
                            Log.e("Walking Directions", "Inside")
                            launchNavigation(origin, destination, DirectionsCriteria.PROFILE_WALKING)
                        }
                        DRIVING_DIRECTIONS -> {
                            Log.e("Driving Directions", "Inside")
                            launchNavigation(origin, destination, DirectionsCriteria.PROFILE_DRIVING)
                        }
                    }
                }

                fun onCanceled() {}
            })
            dialog.show(supportFragmentManager, "Directions")
        } else {
            mMapView!!.post {
                Toast.makeText(baseContext,
                        "Your location is not found, please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

    private fun launchNavigation(origin: Point, destination: Point, profile: String) {
        Log.e("Origin", origin.toString())
        Log.e("Destination", destination.toString())
        navigation!!.addMilestoneEventListener(this)
        NavigationRoute.builder(baseContext)
                .accessToken(getString(R.string.access_token))
                .origin(origin)
                .destination(destination)
                .profile(profile)
                .build()
                .getRoute(object : Callback<DirectionsResponse?> {
                    override fun onResponse(call: Call<DirectionsResponse?>, response: Response<DirectionsResponse?>) {
                        if (response.body() == null) {
                            Log.e("Status:", "Fold up")
                            return
                        } else if (response.body()!!.routes().size == 0) {
                            Log.e("Status:", "No routes")
                            return
                        }
                        val route = response.body()!!.routes()[0]
                        val options = NavigationLauncherOptions.builder()
                                .directionsRoute(route)
                                .shouldSimulateRoute(true)
                                .build()

                        // Call this method with Context from within an Activity
                        NavigationLauncher.startNavigation(this@ShareLocation, options)
                    }

                    override fun onFailure(call: Call<DirectionsResponse?>, t: Throwable) {}
                })
    }

    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView!!.onPause()
    }

    override fun onStop() {
        super.onStop()
        mMapView!!.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView!!.onSaveInstanceState(outState)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {}
    override fun onPermissionResult(granted: Boolean) {}
    override fun onMilestoneEvent(routeProgress: RouteProgress, instruction: String, milestone: Milestone) {}
}