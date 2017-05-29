package test.project

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by andresrodriguez on 8/16/16.
 */
class ProductsAdapter(private val mContext: Context) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {
    internal var products: Array<Product.Product>? = null

    class ProductsViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        // Campos respectivos de un item
        var productImg: ImageView
        var productName: TextView

        init {
            productImg = v.findViewById(R.id.productImg) as ImageView
            productName = v.findViewById(R.id.productName) as TextView
        }
    }

    override fun getItemCount(): Int {
        return products?.size!!
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ProductsViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.product_list, viewGroup, false)
        return ProductsViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ProductsViewHolder, i: Int) {
        val a = i
        viewHolder.itemView.setOnClickListener { }
        val database = MySqliteHelper(mContext)
        val imagePath = database.getImagePath(products!![i].image_id)
        //        Bitmap bmp = loadImageFromGallery(imagePath);
        //        viewHolder.productImg.setImageBitmap(bmp);
        viewHolder.productImg.setImageURI(Uri.parse(imagePath))
        viewHolder.productName.setText(products!![i].name)

    }

    private fun loadImageFromGallery(imageURI: String): Bitmap {
        var bitmap: Bitmap? = null
        val file = File(imageURI)
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(file.absolutePath)
        }
        return bitmap!!
    }

    private fun loadImageFromDevice(imageURI: String): Bitmap {
        var bitmap: Bitmap? = null
        val file = File(Environment.getExternalStorageDirectory().toString() + "/SilverbarsImg/" + imageURI)
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath())
        }
        return bitmap!!
    }

}