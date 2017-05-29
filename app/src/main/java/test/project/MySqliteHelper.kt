package test.project

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import android.os.Environment
import android.util.Log

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * Created by andre_000 on 8/14/2016.
 */
class MySqliteHelper(context: Context) : SQLiteOpenHelper(context, MySqliteHelper.DATABASE_NAME, null, MySqliteHelper.DATABASE_VERSION) {

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(CREATE_TABLE_USERS)
        database.execSQL(CREATE_TABLE_PRODUCTS)
        database.execSQL(CREATE_TABLE_LOCATIONS)
        database.execSQL(CREATE_TABLE_BARCODES)
        database.execSQL(CREATE_TABLE_IMAGES)
        database.execSQL(CREATE_TABLE_SIGNATURES)
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.w(MySqliteHelper::class.java.name,
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS , $TABLE_PRODUCTS , $TABLE_LOCATIONS , $TABLE_BARCODES , $TABLE_IMAGES , $TABLE_SIGNATURES")
        onCreate(db)
    }

    fun insertUser(name: String, email: String, password: String, facebook: String, active: Int): Long {
        var userId: Long = 0
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_NAME, name)
        cv.put(KEY_EMAIL, email)
        cv.put(KEY_PASSWORD, password)
        cv.put(KEY_FACEBOOK, facebook)
        cv.put(KEY_ACTIVE, active)
        userId = db.insert(TABLE_USERS, null, cv)
        db.close()
        return userId
    }


    //    USERS TASKS

    fun updateUser(email: String, active: Int): Int {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_ACTIVE, active)
        val i = db.update(TABLE_USERS, cv, KEY_EMAIL + "=" + email, null)
        db.close()
        return i
    }

    fun getUser(email: String): Array<String> {
        var results: Array<String>? = null
        val db = this.readableDatabase
        val row = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $KEY_EMAIL = '$email'", null)
        if (row.moveToFirst()) {
            results = arrayOf<String>()
            row.moveToFirst()
            results[0] = row.getInt(0).toString()
            results[1] = row.getString(1)
            results[2] = row.getString(2)
            results[3] = row.getString(3)
            results[4] = row.getString(4)
        }
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return results!!
    }

    val activeUser: Array<String>
        get() {
            val results = arrayOf<String>()
            val db = this.readableDatabase
            val row = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $KEY_ACTIVE = 1", null)
            if (row.moveToFirst()) {
                row.moveToFirst()
                results[0] = row.getInt(0).toString()
                results[1] = row.getString(1)
                results[2] = row.getString(2)
                results[3] = row.getString(3)
                results[4] = row.getString(4)
            }
            db.close()
            try {
                BD_backup()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return results!!
        }

    //    PRODUCT TASKS

    fun insertProduct(name: String, description: String, price: Float, userid: Int, imageid: Int, locationid: Int, barcodeid: Int, signature_state: String): Long {
        val db = this.writableDatabase
        val cv = ContentValues()
        var productId: Long = 0
        cv.put(KEY_PNAME, name)
        cv.put(KEY_DESCRIPTION, description)
        cv.put(KEY_PRICE, price)
        cv.put(KEY_USERID, userid)
        cv.put(KEY_IMAGEID, imageid)
        cv.put(KEY_LOCATIONID, locationid)
        cv.put(KEY_BARCODEID, barcodeid)
        cv.put(KEY_SIGSTATE, signature_state)
        productId = db.insert(TABLE_PRODUCTS, null, cv)
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return productId
    }

    fun getProductbyId(id: Int): Product.Product {
        var results: Product.Product? = null
        val db = this.readableDatabase
        val row = db.rawQuery("SELECT * FROM $TABLE_PRODUCTS WHERE $KEY_ID = $id", null)
        Log.v("Row Count", row.count.toString())
        if (row.moveToFirst()) {
            row.moveToFirst()
            results = Product.Product(
                    row.getInt(0),
                    row.getString(1),
                    row.getString(2),
                    row.getInt(3),
                    row.getInt(4),
                    row.getInt(5),
                    row.getInt(6),
                    row.getInt(7),
                    row.getString(8))
        } else
            Log.v("Database Error", "No results")
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return results!!
    }

    fun getUserProducts(id: Int): Array<Product.Product> {
        var results: Array<Product.Product>? = null
        val db = this.readableDatabase
        val row = db.rawQuery("SELECT * FROM $TABLE_PRODUCTS WHERE $KEY_USERID = $id", null)
        var i = 0
        if (row.moveToFirst()) {
            row.moveToFirst()
            results = arrayOf<Product.Product>()
            results[i] = Product.Product(
                    row.getInt(0),
                    row.getString(1),
                    row.getString(2),
                    row.getInt(3),
                    row.getInt(4),
                    row.getInt(5),
                    row.getInt(6),
                    row.getInt(7),
                    row.getString(8))
            while (row.moveToNext()) {
                i++
                results[i] = Product.Product(
                        row.getInt(0),
                        row.getString(1),
                        row.getString(2),
                        row.getInt(3),
                        row.getInt(4),
                        row.getInt(5),
                        row.getInt(6),
                        row.getInt(7),
                        row.getString(8))
            }
        } else
            Log.v("Database Error", "No results")

        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return results!!
    }

    fun getProductsbyBarcode(barcodeId: Int): Product.Product {
        var results: Product.Product? = null
        val db = this.readableDatabase
        val row = db.rawQuery("SELECT * FROM $TABLE_PRODUCTS WHERE $KEY_BARCODEID = $barcodeId", null)
        if (row.moveToFirst()) {
            row.moveToFirst()
            results = Product.Product(
                    row.getInt(0),
                    row.getString(1),
                    row.getString(2),
                    row.getInt(3),
                    row.getInt(4),
                    row.getInt(5),
                    row.getInt(6),
                    row.getInt(7),
                    row.getString(8))
        } else
            Log.v("Database Error", "No results")

        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return results!!
    }

    fun productsbyPriceAsc(): Array<Product.Product>? {
            var results: Array<Product.Product>? = null
            val db = this.readableDatabase
            val row = db.rawQuery("SELECT * FROM $TABLE_PRODUCTS ORDER BY $KEY_PRICE ASC", null)
            var i = 0
            if (row.moveToFirst()) {
                row.moveToFirst()
                results = arrayOf<Product.Product>()
                results[i] = Product.Product(
                        row.getInt(0),
                        row.getString(1),
                        row.getString(2),
                        row.getInt(3),
                        row.getInt(4),
                        row.getInt(5),
                        row.getInt(6),
                        row.getInt(7),
                        row.getString(8))
                while (row.moveToNext()) {
                    i++
                    results[i] = Product.Product(
                            row.getInt(0),
                            row.getString(1),
                            row.getString(2),
                            row.getInt(3),
                            row.getInt(4),
                            row.getInt(5),
                            row.getInt(6),
                            row.getInt(7),
                            row.getString(8))
                }
            } else
                Log.v("Database Error", "No results")

            db?.close()
//            try {
//                BD_backup()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }

            return results
        }

    val productsbyPriceDes: Array<Product.Product>
        get() {
            var results: Array<Product.Product>? = null
            val db = this.readableDatabase
            val row = db.rawQuery("SELECT * FROM $TABLE_PRODUCTS ORDER BY $KEY_PRICE DESC", null)
            var i = 0
            if (row.moveToFirst()) {
                row.moveToFirst()
                results = arrayOf<Product.Product>()
                results[i] = Product.Product(
                        row.getInt(0),
                        row.getString(1),
                        row.getString(2),
                        row.getInt(3),
                        row.getInt(4),
                        row.getInt(5),
                        row.getInt(6),
                        row.getInt(7),
                        row.getString(8))
                while (row.moveToNext()) {
                    i++
                    results[i] = Product.Product(
                            row.getInt(0),
                            row.getString(1),
                            row.getString(2),
                            row.getInt(3),
                            row.getInt(4),
                            row.getInt(5),
                            row.getInt(6),
                            row.getInt(7),
                            row.getString(8))
                }
            } else
                Log.v("Database Error", "No results")

            db.close()
            try {
                BD_backup()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return results!!
        }

    //    LOCATIONS TASKS

    fun insertLocation(lat: String, lon: String): Long {
        var locationId: Long = 0
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_LAT, lat)
        cv.put(KEY_LON, lon)
        locationId = db.insert(TABLE_LOCATIONS, null, cv)
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return locationId
    }

    fun getLocationId(lat: String, lon: String): Int {
        val db = this.writableDatabase
        val row = db.rawQuery("SELECT $KEY_ID FROM $TABLE_LOCATIONS WHERE $KEY_LAT = $lat AND $KEY_LON = $lon", null)
        var locationId = 0
        if (row.moveToFirst()) {
            row.moveToFirst()
            locationId = row.getInt(0)
        }
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return locationId
    }

    //
    fun getLocation(id: Int): Array<String> {
        val db = this.writableDatabase
        val row = db.rawQuery("SELECT * FROM $TABLE_LOCATIONS WHERE $KEY_ID = $id", null)
        val location = arrayOf<String>()
        if (row.moveToFirst()) {
            row.moveToFirst()
            location[0] = row.getString(1)
            location[1] = row.getString(2)
        }
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return location
    }

    //    BARCODES TASKS

    fun insertBarcode(barcode: String): Long {
        var barcodeId: Long = 0
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_BARCODE, barcode)
        barcodeId = db.insert(TABLE_BARCODES, null, cv)
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return barcodeId
    }

    fun getBarcode(barcode: String): Int {
        val db = this.readableDatabase
        val row = db.rawQuery("SELECT $KEY_ID FROM $TABLE_BARCODES WHERE $KEY_BARCODE = $barcode", null)
        var barcodeId = 0
        if (row.moveToFirst()) {
            row.moveToFirst()
            barcodeId = row.getInt(0)
        }
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return barcodeId
    }

    //    IMAGES TASKS

    fun insertImages(url: String): Long {
        var imageId: Long = 0
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_IMGURL, url)
        imageId = db.insert(TABLE_IMAGES, null, cv)
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imageId
    }

    fun getImageId(path: String): Int {
        val db = this.writableDatabase
        val row = db.rawQuery("SELECT $KEY_ID FROM $TABLE_IMAGES WHERE $KEY_IMGURL = $path", null)
        var imageId = 0
        if (row.moveToFirst()) {
            row.moveToFirst()
            imageId = row.getInt(0)
        }
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imageId
    }

    fun getImagePath(id: Int): String {
        val db = this.readableDatabase
        val row = db.rawQuery("SELECT $KEY_IMGURL FROM $TABLE_IMAGES WHERE $KEY_ID = $id", null)
        var imagePath: String? = null
        if (row.moveToFirst()) {
            row.moveToFirst()
            imagePath = row.getString(0)
        }
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imagePath!!
    }

    //    SIGNATURES TASKS

    fun insertSignature(signature: String, product_id: Int): Long {
        var signatureId: Long = 0
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_SIGNATURE, signature)
        cv.put(KEY_PRODUCTID, product_id)
        signatureId = db.insert(TABLE_SIGNATURES, null, cv)
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return signatureId
    }

    fun getSignatureId(product_id: Int): Int {
        val db = this.readableDatabase
        val row = db.rawQuery("SELECT $KEY_ID FROM $TABLE_SIGNATURES WHERE $KEY_PRODUCTID = $product_id", null)
        var signatureId = 0
        if (row.moveToFirst()) {
            row.moveToFirst()
            signatureId = row.getInt(0)
        }
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return signatureId
    }

    fun getSignaturePath(product_id: Int): String {
        val db = this.readableDatabase
        val row = db.rawQuery("SELECT $KEY_SIGNATURE FROM $TABLE_SIGNATURES WHERE $KEY_PRODUCTID = $product_id", null)
        var signaturePath: String? = null
        if (row.moveToFirst()) {
            row.moveToFirst()
            signaturePath = row.getString(0)
        }
        db.close()
        try {
            BD_backup()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return signaturePath!!
    }

    companion object {

        // Database Version
        val DATABASE_VERSION = 1
        // Database Name
        val DATABASE_NAME = "Inventory"
        //     Database tables name
        val TABLE_USERS = "Users"
        val TABLE_PRODUCTS = "Products"
        val TABLE_LOCATIONS = "Location"
        val TABLE_BARCODES = "Barcodes"
        val TABLE_IMAGES = "PImage"
        val TABLE_SIGNATURES = "Signature"

        //     Users Table Columns names
        val KEY_ID = "id"
        val KEY_EMAIL = "email"
        val KEY_NAME = "name"
        val KEY_PASSWORD = "password"
        val KEY_FACEBOOK = "facebook"
        val KEY_ACTIVE = "active"
        //    Products Table Columns names
        val KEY_PNAME = "name"
        val KEY_DESCRIPTION = "description"
        val KEY_PRICE = "price"
        val KEY_USERID = "user_id"
        val KEY_IMAGEID = "image_id"
        val KEY_LOCATIONID = "location_id"
        val KEY_BARCODEID = "barcode_id"
        val KEY_SIGSTATE = "signature_state"
        //    Location table Columns names
        val KEY_LAT = "latitude"
        val KEY_LON = "longitude"
        //    Barcodes table Column names
        val KEY_BARCODE = "barcode"
        //    Images table Column names
        val KEY_IMGURL = "image_url"
        //    Signature table Column names
        val KEY_SIGNATURE = "signature"
        val KEY_PRODUCTID = "product_id"

        private val CREATE_TABLE_USERS = "CREATE TABLE " +
                TABLE_USERS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                KEY_NAME + " TEXT, " +
                KEY_EMAIL + " varchar, " +
                KEY_PASSWORD + " varchar, " +
                KEY_FACEBOOK + " varchar, " +
                KEY_ACTIVE + " integer)"
        private val CREATE_TABLE_PRODUCTS = "CREATE TABLE " +
                TABLE_PRODUCTS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                KEY_PNAME + " TEXT, " +
                KEY_DESCRIPTION + " varchar, " +
                KEY_PRICE + " REAL, " +
                KEY_SIGSTATE + " TEXT, " +
                KEY_IMAGEID + " INTEGER REFERENCE" + TABLE_IMAGES + ", " +
                KEY_LOCATIONID + " INTEGER REFERENCE" + TABLE_LOCATIONS + ", " +
                KEY_BARCODEID + " INTEGER REFERENCE" + TABLE_BARCODES + ", " +
                KEY_USERID + " INTEGER REFERENCE" + TABLE_USERS + ")"
        private val CREATE_TABLE_LOCATIONS = "CREATE TABLE " +
                TABLE_LOCATIONS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                KEY_LAT + " VARCHAR, " +
                KEY_LON + " VARCHAR)"
        private val CREATE_TABLE_BARCODES = "CREATE TABLE " +
                TABLE_BARCODES + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                KEY_BARCODE + " TEXT)"
        private val CREATE_TABLE_SIGNATURES = "CREATE TABLE " +
                TABLE_SIGNATURES + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                KEY_PRODUCTID + " INTEGER REFERENCE" + TABLE_PRODUCTS + "," +
                KEY_SIGNATURE + " VARCHAR)"
        private val CREATE_TABLE_IMAGES = "CREATE TABLE " +
                TABLE_IMAGES + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                KEY_IMGURL + " VARCHAR)"


        @Throws(IOException::class)
        fun BD_backup() {

            val inFileName = "/data/data/test.project/databases/" + DATABASE_NAME
            val dbFile = File(inFileName)
            var fis: FileInputStream? = null

            fis = FileInputStream(dbFile)

            val directorio = Environment.getExternalStorageDirectory().toString() + "/Database"
            val d = File(directorio)
            if (!d.exists()) {
                d.mkdir()
            }
            val outFileName = directorio + "/" + DATABASE_NAME

            val output = FileOutputStream(outFileName)

            val buffer = ByteArray(1024)
            var length: Int
            while (fis.read(buffer) > 0) {
                length = fis.read(buffer)
                output.write(buffer, 0, length)
            }

            output.flush()
            output.close()
            fis.close()

        }
    }


}