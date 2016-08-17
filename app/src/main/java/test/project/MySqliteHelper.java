package test.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by andre_000 on 8/14/2016.
 */
public class MySqliteHelper extends SQLiteOpenHelper {

    // Database Version
    public static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "Inventory";
    //     Database tables name
    public static final String TABLE_USERS = "Users";
    public static final String TABLE_PRODUCTS = "Products";
    public static final String TABLE_LOCATIONS = "Location";
    public static final String TABLE_BARCODES = "Barcodes";
    public static final String TABLE_IMAGES = "PImage";
    public static final String TABLE_SIGNATURES = "Signature";

    //     Users Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FACEBOOK = "facebook";
    public static final String KEY_ACTIVE = "active";
    //    Products Table Columns names
    public static final String KEY_PNAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PRICE = "price";
    public static final String KEY_USERID = "user_id";
    public static final String KEY_IMAGEID = "image_id";
    public static final String KEY_LOCATIONID = "location_id";
    public static final String KEY_BARCODEID = "barcode_id";
    public static final String KEY_SIGSTATE = "signature_state";
    //    Location table Columns names
    public static final String KEY_LAT = "latitude";
    public static final String KEY_LON = "longitude";
    //    Barcodes table Column names
    public static final String KEY_BARCODE = "barcode";
    //    Images table Column names
    public static final String KEY_IMGURL = "image_url";
    //    Signature table Column names
    public static final String KEY_SIGNATURE = "signature";
    public static final String KEY_PRODUCTID = "product_id";

    public MySqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE_TABLE_USERS = "CREATE TABLE "+
            TABLE_USERS+"(" +
            KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            KEY_NAME+" TEXT, " +
            KEY_EMAIL+" varchar, " +
            KEY_PASSWORD+" varchar, " +
            KEY_FACEBOOK+" varchar, " +
            KEY_ACTIVE+" integer)";
    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE "+
            TABLE_PRODUCTS+"(" +
            KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            KEY_PNAME+" TEXT, " +
            KEY_DESCRIPTION+" varchar, " +
            KEY_PRICE+" REAL, " +
            KEY_SIGSTATE+" TEXT, " +
            KEY_IMAGEID+" INTEGER REFERENCE"+TABLE_IMAGES+", " +
            KEY_LOCATIONID+" INTEGER REFERENCE"+TABLE_LOCATIONS+", " +
            KEY_BARCODEID+" INTEGER REFERENCE"+TABLE_BARCODES+", " +
            KEY_USERID+" INTEGER REFERENCE"+TABLE_USERS+")";
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE "+
            TABLE_LOCATIONS+"(" +
            KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            KEY_LAT+" VARCHAR, " +
            KEY_LON+" VARCHAR)";
    private static final String CREATE_TABLE_BARCODES = "CREATE TABLE "+
            TABLE_BARCODES+"(" +
            KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            KEY_BARCODE+" TEXT)";
    private static final String CREATE_TABLE_SIGNATURES = "CREATE TABLE "+
            TABLE_SIGNATURES+"(" +
            KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            KEY_PRODUCTID+" INTEGER REFERENCE"+TABLE_PRODUCTS+"," +
            KEY_SIGNATURE+" VARCHAR)";
    private static final String CREATE_TABLE_IMAGES = "CREATE TABLE "+
            TABLE_IMAGES+"(" +
            KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            KEY_IMGURL+" VARCHAR)";

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_USERS);
        database.execSQL(CREATE_TABLE_PRODUCTS);
        database.execSQL(CREATE_TABLE_LOCATIONS);
        database.execSQL(CREATE_TABLE_BARCODES);
        database.execSQL(CREATE_TABLE_IMAGES);
        database.execSQL(CREATE_TABLE_SIGNATURES);
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.w(MySqliteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS +" , "+TABLE_PRODUCTS+" , "+TABLE_LOCATIONS+" , "+TABLE_BARCODES+" , "+TABLE_IMAGES+" , "+TABLE_SIGNATURES);
        onCreate(db);
    }

    public long insertUser(String name, String email, String password, String facebook, int active){
        long userId = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME,name);
        cv.put(KEY_EMAIL,email);
        cv.put(KEY_PASSWORD,password);
        cv.put(KEY_FACEBOOK,facebook);
        cv.put(KEY_ACTIVE,active);
        userId = db.insert(TABLE_USERS,null,cv);
        db.close();
        return userId;
    }


//    USERS TASKS

    public int updateUser(String email, int active){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_ACTIVE,active);
        int i = db.update(TABLE_USERS,cv,KEY_EMAIL+"="+email,null);
        db.close();
        return i;
    }

    public String[] getUser(String email){
        String[] results = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_USERS+" WHERE "+KEY_EMAIL+" = '"+email+"'",null);
        if (row.moveToFirst()){
            results = new String[5];
            row.moveToFirst();
            results[0] = String.valueOf(row.getInt(0));
            results[1] = row.getString(1);
            results[2] = row.getString(2);
            results[3] = row.getString(3);
            results[4] = row.getString(4);
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public String[] getActiveUser(){
        String[] results = new String[5] ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_USERS+" WHERE "+KEY_ACTIVE+" = "+1,null);
        if (row.moveToFirst()){
            row.moveToFirst();
            results[0] = String.valueOf(row.getInt(0));
            results[1] = row.getString(1);
            results[2] = row.getString(2);
            results[3] = row.getString(3);
            results[4] = row.getString(4);
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

//    PRODUCT TASKS

    public long insertProduct(String name, String description, float price, int userid, int imageid, int locationid, int barcodeid, String signature_state){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long productId = 0;
        cv.put(KEY_PNAME,name);
        cv.put(KEY_DESCRIPTION,description);
        cv.put(KEY_PRICE,price);
        cv.put(KEY_USERID,userid);
        cv.put(KEY_IMAGEID,imageid);
        cv.put(KEY_LOCATIONID,locationid);
        cv.put(KEY_BARCODEID,barcodeid);
        cv.put(KEY_SIGSTATE,signature_state);
        productId = db.insert(TABLE_PRODUCTS,null,cv);
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return productId;
    }

    public Product getProductbyId(int id){
        Product results = new Product();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_PRODUCTS+" WHERE "+KEY_ID+" = "+id,null);
        Log.v("Row Count",String.valueOf(row.getCount()));
        if (row.moveToFirst()){
            row.moveToFirst();
            results = new Product(
                    row.getInt(0),
                    row.getString(1),
                    row.getString(2),
                    row.getInt(3),
                    row.getInt(4),
                    row.getInt(5),
                    row.getInt(6),
                    row.getInt(7),
                    row.getString(8)
            );
        }
        else
            Log.v("Database Error","No results");
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public Product[] getUserProducts(int id){
        Product[] results = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_PRODUCTS+" WHERE "+KEY_USERID+" = "+id,null);
        int i = 0;
        if (row.moveToFirst()){
            row.moveToFirst();
            results = new Product[row.getCount()];
            results[i] = new Product(
                    row.getInt(0),
                    row.getString(1),
                    row.getString(2),
                    row.getInt(3),
                    row.getInt(4),
                    row.getInt(5),
                    row.getInt(6),
                    row.getInt(7),
                    row.getString(8)
            );
            while(row.moveToNext()){
                i++;
                results[i] = new Product(
                        row.getInt(0),
                        row.getString(1),
                        row.getString(2),
                        row.getInt(3),
                        row.getInt(4),
                        row.getInt(5),
                        row.getInt(6),
                        row.getInt(7),
                        row.getString(8)
                );
            }
        }
        else
            Log.v("Database Error","No results");

        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public Product getProductsbyBarcode(int barcodeId){
        Product results = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_PRODUCTS+" WHERE "+KEY_BARCODEID+" = "+barcodeId,null);
        if (row.moveToFirst()){
            row.moveToFirst();
            results = new Product(
                    row.getInt(0),
                    row.getString(1),
                    row.getString(2),
                    row.getInt(3),
                    row.getInt(4),
                    row.getInt(5),
                    row.getInt(6),
                    row.getInt(7),
                    row.getString(8)
            );
        }
        else
            Log.v("Database Error","No results");

        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public Product[] getProductsbyPriceAsc(){
        Product[] results = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_PRODUCTS+" ORDER BY "+KEY_PRICE+" ASC",null);
        int i = 0;
        if (row.moveToFirst()){
            row.moveToFirst();
            results = new Product[row.getCount()];
            results[i] = new Product(
                    row.getInt(0),
                    row.getString(1),
                    row.getString(2),
                    row.getInt(3),
                    row.getInt(4),
                    row.getInt(5),
                    row.getInt(6),
                    row.getInt(7),
                    row.getString(8)
            );
            while(row.moveToNext()){
                i++;
                results[i] = new Product(
                        row.getInt(0),
                        row.getString(1),
                        row.getString(2),
                        row.getInt(3),
                        row.getInt(4),
                        row.getInt(5),
                        row.getInt(6),
                        row.getInt(7),
                        row.getString(8)
                );
            }
        }
        else
            Log.v("Database Error","No results");

        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public Product[] getProductsbyPriceDes(){
        Product[] results = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_PRODUCTS+" ORDER BY "+KEY_PRICE+" DESC",null);
        int i = 0;
        if (row.moveToFirst()){
            row.moveToFirst();
            results = new Product[row.getCount()];
            results[i] = new Product(
                    row.getInt(0),
                    row.getString(1),
                    row.getString(2),
                    row.getInt(3),
                    row.getInt(4),
                    row.getInt(5),
                    row.getInt(6),
                    row.getInt(7),
                    row.getString(8)
            );
            while(row.moveToNext()){
                i++;
                results[i] = new Product(
                        row.getInt(0),
                        row.getString(1),
                        row.getString(2),
                        row.getInt(3),
                        row.getInt(4),
                        row.getInt(5),
                        row.getInt(6),
                        row.getInt(7),
                        row.getString(8)
                );
            }
        }
        else
            Log.v("Database Error","No results");

        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

//    LOCATIONS TASKS

    public long insertLocation(String lat, String lon){
        long locationId = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_LAT,lat);
        cv.put(KEY_LON,lon);
        locationId = db.insert(TABLE_LOCATIONS,null,cv);
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationId;
    }

    public int getLocationId(String lat, String lon){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor row = db.rawQuery("SELECT "+KEY_ID+" FROM "+TABLE_LOCATIONS+" WHERE "+KEY_LAT+" = "+lat+" AND "+KEY_LON+" = "+lon,null);
        int locationId = 0;
        if (row.moveToFirst()){
            row.moveToFirst();
            locationId = row.getInt(0);
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationId;
    }
//
    public String[] getLocation(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_LOCATIONS+" WHERE "+KEY_ID+" = "+id,null);
        String[] location = new String[2];
        if (row.moveToFirst()){
            row.moveToFirst();
            location[0] = row.getString(1);
            location[1] = row.getString(2);
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

//    BARCODES TASKS

    public long insertBarcode(String barcode){
        long barcodeId = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_BARCODE,barcode);
        barcodeId = db.insert(TABLE_BARCODES,null,cv);
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return barcodeId;
    }

    public int getBarcode(String barcode){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT "+KEY_ID+" FROM "+TABLE_BARCODES+" WHERE "+KEY_BARCODE+" = "+barcode,null);
        int barcodeId = 0;
        if (row.moveToFirst()){
            row.moveToFirst();
            barcodeId = row.getInt(0);
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return barcodeId;
    }

//    IMAGES TASKS

    public long insertImages(String url){
        long imageId = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_IMGURL,url);
        imageId = db.insert(TABLE_IMAGES,null,cv);
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageId;
    }

    public int getImageId(String path){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor row = db.rawQuery("SELECT "+KEY_ID+" FROM "+TABLE_IMAGES+" WHERE "+KEY_IMGURL+" = "+path,null);
        int imageId = 0;
        if (row.moveToFirst()){
            row.moveToFirst();
            imageId = row.getInt(0);
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageId;
    }

    public String getImagePath(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT "+KEY_IMGURL+" FROM "+TABLE_IMAGES+" WHERE "+KEY_ID+" = "+id,null);
        String imagePath = null;
        if (row.moveToFirst()){
            row.moveToFirst();
            imagePath = row.getString(0);
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePath;
    }

//    SIGNATURES TASKS

    public long insertSignature(String signature, int product_id){
        long signatureId = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_SIGNATURE,signature);
        cv.put(KEY_PRODUCTID,product_id);
        signatureId = db.insert(TABLE_SIGNATURES,null,cv);
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signatureId;
    }

    public int getSignatureId(int product_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT "+KEY_ID+" FROM "+TABLE_SIGNATURES+" WHERE "+KEY_PRODUCTID+" = "+product_id,null);
        int signatureId = 0;
        if (row.moveToFirst()){
            row.moveToFirst();
            signatureId = row.getInt(0);
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signatureId;
    }

    public String getSignaturePath(int product_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT "+KEY_SIGNATURE+" FROM "+TABLE_SIGNATURES+" WHERE "+KEY_PRODUCTID+" = "+product_id,null);
        String signaturePath = null;
        if (row.moveToFirst()){
            row.moveToFirst();
            signaturePath = row.getString(0);
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signaturePath;
    }



    public static void BD_backup() throws IOException {

        final String inFileName = "/data/data/test.project/databases/"+DATABASE_NAME;
        File dbFile = new File(inFileName);
        FileInputStream fis = null;

        fis = new FileInputStream(dbFile);

        String directorio = Environment.getExternalStorageDirectory()+"/Database";
        File d = new File(directorio);
        if (!d.exists()) {
            d.mkdir();
        }
        String outFileName = directorio + "/"+DATABASE_NAME;

        OutputStream output = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        fis.close();

    }


}