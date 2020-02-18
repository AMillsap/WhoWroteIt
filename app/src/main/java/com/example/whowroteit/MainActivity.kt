package com.example.whowroteit

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<String>
{
    private var mBookInput: EditText? = null
    private var mTitleText: TextView? = null
    private var mAuthorText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBookInput = findViewById(R.id.bookInput)
        mTitleText = findViewById(R.id.titleText)
        mAuthorText = findViewById(R.id.authorText)

    }

    fun searchBooks(view: View)
    {
        val queryString = mBookInput!!.text.toString()
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager != null)
        {
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
        val connMgr =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo: NetworkInfo? = null
        if (connMgr != null) {
            networkInfo = connMgr.activeNetworkInfo
        }
        if (networkInfo != null && networkInfo.isConnected()
            && queryString.length != 0)
        {
            //Fetchbook(mTitleText, mAuthorText).execute(queryString)
            val queryBundle = Bundle()
            queryBundle.putString("queryString", queryString)
            supportLoaderManager.restartLoader(0, queryBundle, this)
            mAuthorText?.setText("");
            mTitleText?.setText(R.string.loading)
        }
        else {
            if (queryString.length == 0) {
                mAuthorText?.setText("")
                mTitleText?.setText(R.string.no_search_term)
            } else {
                mAuthorText?.setText("")
                mTitleText?.setText(R.string.no_network)
            }
        }

    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<String>
    {
        var queryString: String? = ""

        if (args != null) {
            queryString = args.getString("queryString")
        }

        return BookLoader(this, queryString)
    }

    override fun onLoadFinished(loader: Loader<String>, data: String?)
    {
        try {
            val jsonObject = JSONObject(data)
            val itemsArray: JSONArray = jsonObject.getJSONArray("items")
            var i = 0
            var title: String? = null
            var authors: String? = null
            while (i < itemsArray.length() &&
                authors == null && title == null
            ) {
                val book = itemsArray.getJSONObject(i)
                val volumeInfo = book.getJSONObject("volumeInfo")
                try {
                    title = volumeInfo.getString("title")
                    authors = volumeInfo.getString("authors")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                i++
            }
            if (title != null && authors != null) {
            } else {
                mTitleText?.setText(R.string.no_results)
                mAuthorText?.setText("")
            }
        } catch (e: JSONException) {
            mTitleText?.setText(R.string.no_results)
            mAuthorText?.setText("")
            e.printStackTrace()
        }
    }

    override fun onLoaderReset(loader: Loader<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
