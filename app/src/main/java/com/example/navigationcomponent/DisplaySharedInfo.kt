package com.example.navigationcomponent

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.navigationcomponent.custom_classes.TourismSite
import com.example.navigationcomponent.ui.home.HomeViewModel
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import java.util.*
import kotlin.collections.ArrayList

public class DisplaySharedInfo : AppCompatActivity() {
    private lateinit var mMapView: MapView
    private lateinit var mMapboxMap: MapboxMap
    private lateinit var homeViewModel: HomeViewModel

    private var name: TextView? = null
    private var desc: TextView? = null


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_shared_info)

        val appLinkAction: String? = intent?.action
        val appLinkData: Uri? = intent?.data
        val test = appLinkData.toString()
        Log.e("trying", test)

        val params: String = pathSegments(test) as String
        Log.e("params",params)

        /*var params : List<String>

        params = test.*/

        /*if (test!= null){

            var dataArray = test.split("\\?") as ArrayList<String>
            Log.e("split1", dataArray.toString())
            var keyValueStrings = dataArray[1].split("&") as ArrayList<String>
            Log.e("split2", keyValueStrings.toString())

        }*/

    }

    private fun pathSegments(test: String): Any {
        var dataArray: ArrayList<String>
        if (test!= null) {

            var dataArray = test.split("\\?") as ArrayList<String>
            Log.e("split1", dataArray.toString())
            var keyValueStrings = dataArray[1].split("&") as ArrayList<String>
            Log.e("split2", keyValueStrings.toString())
        }
        return dataArray
    }

}



