package com.example.navigationcomponent.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.navigationcomponent.R

class SlideshowFragment : Fragment() {
    private var slideshowViewModel: SlideshowViewModel? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        slideshowViewModel = ViewModelProviders.of(this).get(SlideshowViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        val textView = root.findViewById<TextView?>(R.id.text_slideshow)
        slideshowViewModel!!.getText()?.observe(viewLifecycleOwner, { s -> textView.text = s })
        return root
    }
}