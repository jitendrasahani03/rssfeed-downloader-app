package com.example.top10downloader

import android.content.Context
import android.os.Build.VERSION_CODES.R
import android.provider.MediaStore.Audio.Artists
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

//container for all widgets of view
class ViewHolder(v:View){
    val tvName = v.findViewById<TextView>(R.id.tvName)
    val tvArtist = v.findViewById<TextView>(R.id.tvArtist)
    val tvSummary = v.findViewById<TextView>(R.id.tvSSummary)
}
class FeedAdapter(context: Context, val resource: Int = 0, val applications: List<FeedEntry>) :
    ArrayAdapter<FeedEntry>(context, resource) {
    private val TAG = "FeedAdapter"
    override fun getCount(): Int {
        return applications.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder : ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(parent.context).inflate(resource, parent, false)
            viewHolder = ViewHolder(view)           //instantiate the viewHolder
            view.tag = viewHolder                   //set viewholder as tag of convertview
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder     // get list item of view
        }
//        val tvName = view.findViewById<TextView>(R.id.tvName)
//        val tvArtist = view.findViewById<TextView>(R.id.tvArtist)
//        val tvSummary = view.findViewById<TextView>(R.id.tvSSummary)

        val currentApp = applications[position]

        //calling viewHolder list item
        viewHolder.tvName.text = currentApp.name
        viewHolder.tvArtist.text = currentApp.artist
        viewHolder.tvSummary.text = currentApp.summary

        return view
    }
}