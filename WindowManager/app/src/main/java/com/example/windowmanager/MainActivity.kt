package com.example.windowmanager

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.audiofx.BassBoost.Settings
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.windowmanager.ui.theme.WindowManagerTheme

class MainActivity : ComponentActivity() {

    private lateinit var dialog: AlertDialog
    private lateinit var btnMin : ImageButton

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnMin = findViewById(R.id.btnMin)

        if(isServiceRunning()){
            stopService(Intent(this, FloatingWindowApp::class.java))
        }

        btnMin.setOnClickListener {
            if (checkOverlayPermission()) {
                startService(Intent(this, FloatingWindowApp::class.java))
                finish()
            } else {
                requestFloatingWindow()
            }
        }
    }

    private fun isServiceRunning(): Boolean{
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for(service in manager.getRunningServices(Int.MAX_VALUE)){

            if(FloatingWindowApp::class.java.name == service.service.className)
            {
                return true
            }
        }

        return false
    }

    private fun requestFloatingWindow(){

        val  builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle("Screen Overlay Permission")
        builder.setMessage("Enable display over the app from the settings")
        builder.setPositiveButton("Open Settings",  DialogInterface.OnClickListener{dialog, which ->

            val intent = Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package: $packageName"))
            startActivityForResult(intent, RESULT_OK)
        })
        dialog = builder.create()
        dialog.show()
    }

    private fun checkOverlayPermission() : Boolean{
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            android.provider.Settings.canDrawOverlays(this)
        }
        else return true

    }
}
