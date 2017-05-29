package test.project

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Environment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

import butterknife.BindView
import butterknife.ButterKnife

class Signature : AppCompatActivity() {

    @BindView(R.id.Signature) internal var parent: RelativeLayout? = null

    internal var myDrawView: MyDrawView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)
        ButterKnife.bind(this)
        setToolbar()

        myDrawView = MyDrawView(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        parent!!.addView(myDrawView)
    }

    fun saveSignature(parent: RelativeLayout): String {
        parent.isDrawingCacheEnabled = true
        val b = parent.drawingCache

        val directorio = Environment.getExternalStorageDirectory().toString() + "/Signatures"
        val d = File(directorio)
        if (!d.exists()) {
            d.mkdir()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "Signature_$timeStamp.png"
        val f = File(directorio + "/" + fileName)
        Log.v("File", f.toString())
        var fo: FileOutputStream? = null

        try {
            f.createNewFile()
            fo = FileOutputStream(f)
            b.compress(Bitmap.CompressFormat.PNG, 100, fo)
            fo!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return f.toString()
    }

    private fun setToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.firma, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.item_borrar -> {
                myDrawView?.clear()
                return true
            }
            R.id.item_agregar -> {
                val returnIntent = Intent()
                returnIntent.putExtra("FileName", saveSignature(parent!!))
                setResult(Activity.RESULT_OK, returnIntent)
                finish()

                return true
            }
            else -> return false
        }
    }
}
