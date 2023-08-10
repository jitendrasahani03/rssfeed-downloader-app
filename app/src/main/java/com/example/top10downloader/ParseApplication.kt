package com.example.top10downloader

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class ParseApplication {
    private val TAG = "ParseApplication"
    val application = ArrayList<FeedEntry>()
    fun parse(xmlData: String) {
        Log.d(TAG, "Parse called with $xmlData")
        var status = true
        var isEntry = false
        var textValue = ""                      //value of current tag
        try {
            val xmlPullParserFactory = XmlPullParserFactory.newInstance()
            xmlPullParserFactory.isNamespaceAware = true
            val xmlPullParser = xmlPullParserFactory.newPullParser()
            xmlPullParser.setInput(xmlData.reader())

            var eventType = xmlPullParser.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = xmlPullParser.name

                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        Log.d(TAG, "parse: starting tag for $tagName")
                        if (tagName == "entry") {
                            isEntry = true
                        }
                    }

                    XmlPullParser.TEXT -> textValue = xmlPullParser.text
                    XmlPullParser.END_TAG -> {
                        Log.d(TAG, "parse: ending tag for $tagName")
                        if (isEntry) {
                            when (tagName) {
                                "entry" -> {
                                    application.add(currentRecord)
                                    isEntry = false
                                    currentRecord = FeedEntry()
                                }

                                "name" -> currentRecord.name = textValue
                                "artist" -> currentRecord.artist = textValue
                                "releaseDate" -> currentRecord.releaseDate = textValue
                                "summary" -> currentRecord.summary = textValue
                                "image" -> currentRecord.imageURL = textValue

                            }
                        }
                    }
                }
                eventType = xmlPullParser.next()
            }
            for (app in application){
                Log.d(TAG,"----------------------")
                Log.d(TAG,app.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            status = false

        }

    }
}
