package com.example.interviewassignment_niiti


import android.app.Activity
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.onComplete
import org.json.JSONArray
import org.json.JSONTokener
import java.lang.Exception
import java.net.URL
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import java.util.jar.Attributes


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Task"

        val loadList = { reloadButton:View ->
            val loadingBar = findViewById<ProgressBar>(R.id.progressBar)
            runOnUiThread {
                loadingBar.isVisible = true
                reloadButton.isVisible = false
            }
            doAsync {
                val data = getData()
                onComplete {
                    if (data == null || it == null) {
                        loadingBar.isVisible = false
                        reloadButton.isVisible = true
                    } else {
                        loadingBar.isVisible = false
                        reloadButton.isVisible = false
                        setRecyclerView(data, it)
                    }
                }
            }
        }

        val reloadButton:Button = findViewById<Button>(R.id.reloadButton)
        reloadButton.setOnClickListener{
            doAsync {  loadList(it) }
        }

        if (!isOnline(this)) {
            longToast("Internet is off !!")
            findViewById<ProgressBar>(R.id.progressBar).isVisible = false
            reloadButton.isVisible = true
        }else doAsync {loadList(reloadButton)}

        // TODO : get the recently closed app
        if ( checkUsageStatsPermission() ) {
            var packageName:String = ""
            val usageStatsManager = this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val pm = this.packageManager
            val currTime = System.currentTimeMillis()
            val usageEvents = usageStatsManager.queryEvents( currTime - (1000*60*10) , currTime ) as UsageEvents
            val event = UsageEvents.Event()
            while(usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (pm.getApplicationLabel(appInfo).toString() in packageName){continue}
                packageName += pm.getApplicationLabel(appInfo).toString()
                continue
                if(isAppStopped(this,event.packageName)) {
                    try{
                        val appInfo = pm.getApplicationInfo(event.packageName,PackageManager.GET_META_DATA)

                        if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0){

                            //packageName = pm.getApplicationLabel(appInfo).toString()
                        }
                    } catch (e:Exception){ }
                }
            }
            findViewById<TextView>(R.id.lastClosedApp).text = packageName
        }
        else {
            // Navigate the user to the permission settings
            Intent( Settings.ACTION_USAGE_ACCESS_SETTINGS ).apply {
                startActivity( this )
            }
        }

    }

    private fun checkUsageStatsPermission() : Boolean {
        val appOpsManager = this.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",android.os.Process.myUid(),packageName
            )
        }
        else {
            appOpsManager.checkOpNoThrow(
                "android:get_usage_stats",
                android.os.Process.myUid(), packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun isAppStopped(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val procInfos = activityManager.runningAppProcesses
        if (procInfos != null) {
            for (processInfo in procInfos) {
                if (processInfo.processName == packageName) {
                    return false
                }
            }
        }
        return true
    }

    private fun getData(): JSONArray? {
        var data : JSONArray? = null
        try {
            val url =
                "https://raw.githubusercontent.com/FEND16/movie-json-data/master/json/top-rated-indian-movies-01.json"
            val json: String = URL(url).readText()
            data = JSONArray(JSONTokener(json))
        } catch (e:Exception) { }
        return data
    }

    private fun setRecyclerView(data:JSONArray,context: Context){

        val movieRecyclerView:RecyclerView = findViewById(R.id.movieRecyclerView)
        val adapter = MovieRecycleViewAdapter(context,data)
        movieRecyclerView.adapter = adapter
        movieRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (capabilities != null) {
            return  capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        }
        return false
    }
}