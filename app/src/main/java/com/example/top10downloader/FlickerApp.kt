package com.example.top10downloader

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.net.URL
import kotlin.properties.Delegates

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""
    override fun toString(): String {
        return """
                Name : $name
                Artists: $artist
                releaseDate: $releaseDate
                imageURL : $imageURL
        """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var downloadData: DownloadData? = null
    private var feedLimit = 10
    private var feedUrl: String? =
        "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedCachedURL = "INVALIDATED"
    private var STATE_URL = "feedURL"
    private var STATE_LIMIT = "feedLimit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: called")
        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL)
            feedLimit = savedInstanceState.getInt(STATE_LIMIT)
        }
        downloadURL(feedUrl!!.format(feedLimit))
        //https://api.flickr.com/services/feeds/photos_public.gne?tags=android,oreo,sdk&tagmode=any&format=json&nojsoncallback=1
        //downloadData.execute<String>("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        //Log.d(TAG, "OnCreate: Done")
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_URL, feedUrl)
        outState.putInt(STATE_LIMIT, feedLimit)
    }

    private fun downloadURL(feedUrl: String) {
        if (feedUrl != feedCachedURL) {
            downloadData = DownloadData(this, findViewById(R.id.xmlListView))
            downloadData?.execute<String>(feedUrl)
            Log.d(TAG, "downloadURL: Done")
            feedCachedURL = feedUrl
        } else {
        }
    }

    //inflate menu on UI
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)
        if (feedLimit == 10) {
            menu?.findItem(R.id.top10)?.isChecked = true
        } else {
            menu?.findItem(R.id.top25)?.isChecked = true
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.mnuFree -> feedUrl =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"

            R.id.mnuPaid -> feedUrl =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"

            R.id.mnuSongs -> feedUrl =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"

            R.id.top10, R.id.top25 -> {   //to select feed limit from selected item
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    //if check box is change
                } else {
                    //if item is already checked
                }

            }

            R.id.mnuRefresh -> feedCachedURL = "INVALIDATED"
            else ->
                return super.onOptionsItemSelected(item)
        }
        downloadURL(feedUrl!!.format(feedLimit))
        return true
    }

    companion object {
        private class DownloadData(context: Context, listView: ListView) :
            AsyncTaskCoroutine<String, String>() {
            private val TAG = "DownloadData"
            var mContext: Context by Delegates.notNull()
            var mListView: ListView by Delegates.notNull()

            init {
                mContext = context
                mListView = listView
            }

            override fun doInBackground(vararg url: String): String {
                Log.d(TAG, "doInBackground: start with ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.d(TAG, "doInBackground: Error downloading")
                }
                return rssFeed
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)                 //output of background thread
                //Log.d(TAG, "onPostExecute: parameter is $result")
                val parseApplication = ParseApplication()
                parseApplication.parse(result!!)

                val feedAdapter =
                    FeedAdapter(mContext, R.layout.list_record, parseApplication.application)
                mListView.adapter = feedAdapter
            }

            fun downloadXML(urlPath: String?): String {
                return URL(urlPath).readText()
            }
        }
    }
}


