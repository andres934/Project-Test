package test.project

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.google.zxing.client.android.CaptureActivity

import org.w3c.dom.Text

import java.io.File

class searchByBarcode : Fragment() {

    internal var rootView: View? = null
    internal var name: TextView? = null
    internal var description: TextView? = null
    internal var price: TextView? = null
    internal var signature: ImageView? = null
    internal var Image: ImageView? = null
    internal var Search: RelativeLayout? = null
    internal var product: Product.Product? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.activity_search_by_barcode, container, false)
        Search = rootView?.findViewById(R.id.Search) as RelativeLayout
        Search?.isClickable = true
        Search?.setOnClickListener { openScan() }
        name = rootView?.findViewById(R.id.name) as TextView
        description = rootView?.findViewById(R.id.description) as TextView
        price = rootView?.findViewById(R.id.price) as TextView
        signature = rootView?.findViewById(R.id.signature) as ImageView
        Image = rootView?.findViewById(R.id.Image) as ImageView
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                val contents = data!!.getStringExtra("SCAN_RESULT")
                val database = MySqliteHelper(activity)
                val barcodeId = database.getBarcode(contents)
                Log.d("Barcode tag", "contents: " + contents + "/ id: " + barcodeId.toString())
                if (barcodeId != 0) {
                    val id = database.getBarcode(contents)
                    product = database.getProductsbyBarcode(id)
                    Log.d("Barcode tag", "contents: " + product.toString() + "/ id: " + id.toString())
                    if (product != null) {
                        Image?.setImageURI(Uri.parse(database.getImagePath(product!!.image_id)))
                        name?.setText(product!!.name)
                        description?.setText(product!!.description)
                        price?.setText(product!!.price.toString())
                        signature?.setImageBitmap(loadImageFromDevice(database.getSignaturePath(product!!.id)))
                    }
                } else {
                    Log.v("Productos", "no se encontraron productos" + barcodeId.toString() + " / " + contents)
                }
                Log.d("Barcode tag", "contents: " + contents)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("Barcode tag", "RESULT_CANCELED")
            }
        }
    }

    fun openScan() {
        val intent = Intent(activity, CaptureActivity::class.java)
        intent.action = "com.google.zxing.client.android.SCAN"
        intent.putExtra("SAVE_HISTORY", false)
        startActivityForResult(intent, 0)
    }

    private fun loadImageFromDevice(imageURI: String): Bitmap {
        var bitmap: Bitmap? = null
        val file = File(imageURI)
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(file.absolutePath)
        }
        return bitmap!!
    }
}
