package test.project

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import com.google.zxing.client.android.CaptureActivity

import org.w3c.dom.Text

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date

class addProduct : Fragment() {
    internal var imageView: ImageView? = null
    internal var rootView: View? = null
    internal var productBarcode: TextView? = null
    internal var productName: TextView? = null
    internal var productDescription: TextView? = null
    internal var productoPrice: TextView? = null
    internal var button: Button? = null
    internal var barCode: Button? = null
    internal var signaturePath: String? = null
    internal var nombre: String? = null
    internal var descripcion: String? = null
    internal var codigoBarra: String? = null
    internal var signatureState: String? = null
    internal var precio: Float = 0.toFloat()
    internal var userId = 0
    internal var imageFromCamera: File? = null
    internal var currentLocation: Location? = null
    internal var locationManager: LocationManager? = null
    internal var locationListener: LocationListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.title = "Nuevo producto"
        userId = MainActivity.userId
        rootView = inflater!!.inflate(R.layout.activity_add_product, container, false)
        productBarcode = rootView?.findViewById(R.id.codigo) as TextView
        productName = rootView?.findViewById(R.id.tv_nombre) as TextView
        productDescription = rootView?.findViewById(R.id.tv_descripcion) as TextView
        productoPrice = rootView?.findViewById(R.id.tv_precio) as TextView
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        imageView = rootView?.findViewById(R.id.imageView) as ImageView

        imageView?.setOnClickListener {
            //dispatchTakePictureIntent();
            //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }

        barCode = rootView?.findViewById(R.id.barCode) as Button
        barCode?.setOnClickListener { openScan() }
        setHasOptionsMenu(true)

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.add_product, menu)
        val item = menu!!.findItem(R.id.search)

        if (item != null)
            item.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.item_foto -> {
                var permissionCheck = ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.CAMERA)

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    permissionCheck = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    if (permissionCheck == PackageManager.PERMISSION_GRANTED)
                        dispatchTakePictureIntent()
                    else
                        ActivityCompat.requestPermissions(activity,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                } else
                    ActivityCompat.requestPermissions(activity,
                            arrayOf(Manifest.permission.CAMERA), 1)
                return true
            }

            R.id.item_agregar -> {
                val i = Intent(activity, Signature::class.java)
                startActivityForResult(i, 1000)

                return true
            }
            else -> return false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent()
        } else {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            //galleryAddPic();
            val extras = data!!.extras
            val imageBitmap = extras.get("data") as Bitmap
            imageView?.visibility = View.VISIBLE
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val path = MediaStore.Images.Media.insertImage(activity.contentResolver, imageBitmap, imageFileName, "")
            App.CurrentPhotoPath = path
            Log.d("PATH IN GALLERY", path + "")
            val galleryUri = Uri.parse(path)
            val id = getImageId(activity, galleryUri)
            Log.d("ID", id.toString())
            val thumbnailUri = getThumbnailUri(activity, id)
            Log.d("THUMBNAIL PATH", thumbnailUri!! + "")
            imageView?.setImageBitmap(imageBitmap)
        } else if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                val contents = data!!.getStringExtra("SCAN_RESULT")
                productBarcode?.text = contents
                Log.d("Barcode tag", "contents: " + contents)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("Barcode tag", "RESULT_CANCELED")
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val photo = data!!.extras.get("data") as Bitmap
            saveImage(photo, "Test")
            imageView?.setImageBitmap(photo)
        }
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            signaturePath = data!!.getStringExtra("FileName")
            if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
                buildAlertMessageNoGps()
            } else
                getLocation()
        }
    }

    fun openScan() {
        val intent = Intent(activity, CaptureActivity::class.java)
        intent.action = "com.google.zxing.client.android.SCAN"
        intent.putExtra("SAVE_HISTORY", false)
        startActivityForResult(intent, 0)
    }

    fun saveImage(bitmap: Bitmap, name: String) {
        val out: FileOutputStream? = null

        val bytes = ByteArrayOutputStream()


        //you can create a new file name "test.jpg" in sdcard folder.
        val ruta:String = File.separator + name
        val f = File(Environment.getExternalStorageDirectory().toString() + ruta)
        Log.v("File", f.toString())
        var fo: FileOutputStream? = null

        try {
            f.createNewFile()
            fo = FileOutputStream(f)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fo)
            fo!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

            /*
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageFromCamera = photoFile;
                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            */
        }
    }

    fun setLocation(location: Location) {
        currentLocation = location
        Log.v("Location", currentLocation!!.latitude.toString() + " / " + currentLocation!!.longitude.toString())
        val permissionCheck = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationManager?.removeUpdates(locationListener)
        } else
            Log.v("permisions", "not granted")
        nombre = productName?.text.toString()
        descripcion = productDescription?.text.toString()
        precio = java.lang.Float.parseFloat(productoPrice?.text.toString())
        codigoBarra = productBarcode?.text.toString()
        signatureState = "True"
        Log.v("Photo path", App.CurrentPhotoPath)

        val prefs = context.getSharedPreferences("UserId", Context.MODE_PRIVATE)
        userId = prefs.getInt("userId", 0)//"No name defined" is the default value.

        saveProduct(userId, nombre as String, descripcion as String, precio, signatureState as String, currentLocation as Location, App.CurrentPhotoPath.toString(), signaturePath as String, codigoBarra as String)
    }

    fun getLocation() {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                setLocation(location)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }

        val locationProvider = LocationManager.GPS_PROVIDER
        val permissionCheck = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationManager?.requestLocationUpdates(locationProvider, 0, 0f, locationListener)
        } else
            Log.v("permisions", "not granted")
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Tu GPS parece estar desactivado, desea activarlo?").setCancelable(false).setPositiveButton("Yes") { dialog, id ->
            startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            getLocation()
        }.setNegativeButton("No") { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    fun saveProduct(userId: Int, nombre: String, descripcion: String, precio: Float, signatureState: String, location: Location, imagePath: String, signaturePath: String, codigoBarra: String) {
        val database = MySqliteHelper(context)
        Log.v("Photo path", imagePath)
        val barcodeId: Int
        val imageId: Int
        val locationId: Int
        val productId: Int

        barcodeId = database.insertBarcode(codigoBarra).toInt()
        imageId = database.insertImages(imagePath).toInt()
        locationId = database.insertLocation(location.latitude.toString(), location.longitude.toString()).toInt()
        productId = database.insertProduct(nombre, descripcion, precio, userId, imageId, locationId, barcodeId, signatureState).toInt()
        database.insertSignature(signaturePath, productId)
    }

    companion object {

        internal val REQUEST_IMAGE_CAPTURE = 1
        private val CAMERA_REQUEST = 1888

        fun getImageId(context: Context, uri: Uri): Long {
            val projection = arrayOf(MediaStore.Images.Media._ID)
            val selection = MediaStore.Images.Media.DATA + " = ?"
            val selectionArgs = arrayOf(ImageUtils().getPath(context, uri))

            val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null)

            if (cursor!!.moveToFirst()) {
                val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(idColumn)
                cursor.close()
                return id
            }

            return -1
        }

        fun getThumbnailUri(context: Context, id: Long): String? {
            val cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                    context.contentResolver, id,
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null)

            if (cursor.moveToFirst()) {
                val uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA))
                cursor.close()
                return uri
            }
            return null
        }
    }
}

