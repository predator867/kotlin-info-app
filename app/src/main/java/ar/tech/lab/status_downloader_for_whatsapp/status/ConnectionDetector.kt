package ar.tech.lab.status_downloader_for_whatsapp.status

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class internal

class ConnectionDetector(private val _context: Context) {
    /**
     * Checking for all possible internet providers
     */
    val isConnectingToInternet: Boolean
        get() {
            val connectivity =
                _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo
                if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
            return false
        }

}
