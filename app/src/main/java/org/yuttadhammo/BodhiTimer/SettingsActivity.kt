package org.yuttadhammo.BodhiTimer

import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import timber.log.Timber

class SettingsActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = getString(R.string.preferences)
            toolbar.setNavigationOnClickListener { v: View? -> onBackPressed() }
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == RESULT_OK) {
            var uri = intent!!.data
            val uriString = intent.dataString
            val settings = prefs!!.edit()
            when (requestCode) {
                SELECT_RINGTONE -> {
                    uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    if (uri != null) {
                        Timber.i("Got ringtone $uri")
                        settings.putString("SystemUri", uri.toString())
                    }
                }
                SELECT_PRE_RINGTONE -> {
                    uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    if (uri != null) {
                        Timber.i("Got ringtone $uri")
                        settings.putString("PreSystemUri", uri.toString())
                    }
                }
                SELECT_FILE ->                     // Get the Uri of the selected file
                    if (uriString != null) {
                        getPersistablePermission(uri)
                        Timber.i("File Path: " + uri.toString())
                        settings.putString("FileUri", uri.toString())
                    }
                SELECT_PRE_FILE ->                     // Get the Uri of the selected file
                    if (uriString != null) {
                        getPersistablePermission(uri)
                        Timber.i("File Path: " + uri.toString())
                        settings.putString("PreFileUri", uri.toString())
                    }
                SELECT_PHOTO -> if (uri != null) {
                    getPersistablePermission(uri)
                    settings.putString("bmp_url", uri.toString())
                }
            }
            settings.commit()
        }
    }

    private fun getPersistablePermission(uri: Uri?) {
        try {
            contentResolver.takePersistableUriPermission(
                uri!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: Exception) {
            Timber.e(e.toString())
        }
    }


    companion object {
        private const val SELECT_RINGTONE = 0
        private const val SELECT_FILE = 1
        private const val SELECT_PRE_RINGTONE = 2
        private const val SELECT_PRE_FILE = 3
        private const val SELECT_PHOTO = 4

    }
}