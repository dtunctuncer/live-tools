package iammert.com.app

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.raqun.liveconnection.ConnectionLiveData
import com.raqun.live_tools_core.LiveResult.LiveValue
import com.raqun.live_tools_core.LiveResult.PermissionRequired
import com.raqun.liveconnection.ConnectionType

class LiveConnectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ConnectionLiveData(this).observe(this, Observer {
            when (it) {
                is LiveValue<*> -> handleConnectionType(it.value as ConnectionType?)
                is PermissionRequired -> handlePermissions(it.requiredPermissions)
            }
        })
    }

    private fun handleConnectionType(connectionType: ConnectionType?) {
        when (connectionType) {
            ConnectionType.WIFI -> alert("You have a wifi connection!")
            ConnectionType.MOBILE -> alert("You're connected over mobile!")
            ConnectionType.UNAVAILABLE -> alert("Upps.. Something went wrong. We cannot define your connection!")
        }
    }

    private fun handlePermissions(permissions: Array<String>) {
        // you need to handle permissions
    }
}