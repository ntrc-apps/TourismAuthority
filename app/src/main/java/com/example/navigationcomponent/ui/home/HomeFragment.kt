package com.example.navigationcomponent.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.navigationcomponent.R

class HomeFragment : Fragment() {
    private var homeViewModel: HomeViewModel? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView = root.findViewById<TextView?>(R.id.text_home)
        homeViewModel!!.getText()?.observe(viewLifecycleOwner, { s -> textView.text = s })
        return root

    }
}