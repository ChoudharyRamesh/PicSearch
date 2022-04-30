package com.example.picsearch

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger
import java.net.URL

data class ImageUrlData(val pageURL:String, val webformatURL:String, val largeImageURL:String,val id:BigInteger)

class ImageApiFetcher(private val searchText: String){


    private fun getUrl(pageNumber:Int,searchText:String):String{
        val mSearchText = searchText.replace("\\s+".toRegex(), "+")
        return "https://pixabay.com/api/?page=$pageNumber&per_page=100&key=${BuildConfig.PIXABAY_KEY}$mSearchText&image_type=photo"
    }


    fun getUrls(page:Int):ArrayList<ImageUrlData> {

        val imageUrlDataList:ArrayList<ImageUrlData> = ArrayList()

        val url = getUrl(page, searchText)
        val jsonResponse = try {
            URL(url).readText()
        } catch (e: Exception) {
            Log.d("nishi","error occured ${e.message}")
            return imageUrlDataList
        }

        val jsonArray: JSONArray = JSONObject(jsonResponse).getJSONArray("hits")

        for (index in 0 until jsonArray.length()) {
            val urlObject = jsonArray.getJSONObject(index)

            val imageUrlData = ImageUrlData(
                urlObject.getString("pageURL"),
                urlObject.getString("webformatURL"),
                urlObject.getString("largeImageURL"),
                urlObject.getString("id").toBigInteger()
            )

            imageUrlDataList.add(imageUrlData)
        }

        Log.d("nishi","urlList size ${imageUrlDataList.size}")

        return imageUrlDataList
    }

}
