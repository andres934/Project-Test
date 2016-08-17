package test.project;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class addProduct extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;
    public ImageView imageView;
    View rootView;
    TextView productBarcode,productName, productDescription, productoPrice;
    Button button, barCode;
    String signaturePath;
    String nombre, descripcion, codigoBarra, signatureState;
    float precio;
    int userId = 0;
    File imageFromCamera;
    Location currentLocation = null;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Nuevo producto");
        userId = MainActivity.userId;
        rootView = inflater.inflate(R.layout.activity_add_product, container, false);
        productBarcode = (TextView) rootView.findViewById(R.id.codigo);
        productName = (TextView) rootView.findViewById(R.id.tv_nombre);
        productDescription = (TextView) rootView.findViewById(R.id.tv_descripcion);
        productoPrice = (TextView) rootView.findViewById(R.id.tv_precio);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dispatchTakePictureIntent();
                //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        barCode = (Button) rootView.findViewById(R.id.barCode);
        barCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScan();
            }
        });
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_product, menu);
        MenuItem item = menu.findItem(R.id.search);

        if (item != null)
            item.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_foto:
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (permissionCheck == PackageManager.PERMISSION_GRANTED)
                        dispatchTakePictureIntent();
                    else
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                else
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA}, 1);
                return true;

            case R.id.item_agregar:
                Intent i = new Intent(getActivity(), Signature.class);
                startActivityForResult(i, 1000);

                return true;
            default:
                return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        }
        else {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            //galleryAddPic();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setVisibility(View.VISIBLE);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), imageBitmap, imageFileName , "");
            App.CurrentPhotoPath = path;
            Log.d("PATH IN GALLERY", path+"");
            Uri galleryUri = Uri.parse(path);
            long id = getImageId(getActivity(), galleryUri);
            Log.d("ID", id+"");
            String thumbnailUri = getThumbnailUri(getActivity(), id);
            Log.d("THUMBNAIL PATH", thumbnailUri+"");
            imageView.setImageBitmap(imageBitmap);
        }
        else if (requestCode == 0) {
            if (resultCode == getActivity().RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                productBarcode.setText(contents);
                Log.d("Barcode tag", "contents: " + contents);
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Log.d("Barcode tag", "RESULT_CANCELED");
            }
        }else;if (requestCode == CAMERA_REQUEST && resultCode == getActivity().RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            saveImage(photo,"Test");
            imageView.setImageBitmap(photo);
        }else;if(requestCode == 1000 && resultCode == getActivity().RESULT_OK){
            signaturePath = data.getStringExtra("FileName");
            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                buildAlertMessageNoGps();
            }else
                getLocation();
        }
    }

    public void openScan(){
        Intent intent = new Intent(getActivity(),CaptureActivity.class);
        intent.setAction("com.google.zxing.client.android.SCAN");
        intent.putExtra("SAVE_HISTORY", false);
        startActivityForResult(intent, 0);
    }

    public void saveImage(Bitmap bitmap, String name) {
        FileOutputStream out = null;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();


//you can create a new file name "test.jpg" in sdcard folder.
        File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + name);
        Log.v("File",f.toString());
        FileOutputStream fo = null;

        try {
            f.createNewFile();
            fo = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fo);
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

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

    public static long getImageId(Context context, Uri uri) {
        String[] projection = { MediaStore.Images.Media._ID };
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = { ImageUtils.getPath(context, uri) };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor.moveToFirst()) {
            int idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            long id = cursor.getLong(idColumn);
            cursor.close();
            return id;
        }

        return -1;
    }

    public static String getThumbnailUri(Context context, long id) {
        Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                context.getContentResolver(), id,
                MediaStore.Images.Thumbnails.MINI_KIND,
                null);

        if (cursor.moveToFirst()) {
            String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            cursor.close();
            return uri;
        }
        return null;
    }

    public void setLocation(Location location){
        currentLocation = location;
        Log.v("Location",String.valueOf(currentLocation.getLatitude())+" / "+String.valueOf(currentLocation.getLongitude()));
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
        }else
            Log.v("permisions","not granted");
        nombre = productName.getText().toString();
        descripcion = productDescription.getText().toString();
        precio = Float.parseFloat(productoPrice.getText().toString());
        codigoBarra = productBarcode.getText().toString();
        signatureState = "True";
        Log.v("Photo path",App.CurrentPhotoPath);

        SharedPreferences prefs = getContext().getSharedPreferences("UserId",Context.MODE_PRIVATE);
        userId = prefs.getInt("userId", 0);//"No name defined" is the default value.

        saveProduct(userId,nombre,descripcion,precio,signatureState,currentLocation,App.CurrentPhotoPath,signaturePath,codigoBarra);
    }

    public void getLocation(){
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                setLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        String locationProvider = LocationManager.GPS_PROVIDER;
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
        }else
            Log.v("permisions","not granted");
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Tu GPS parece estar desactivado, desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        getLocation();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void saveProduct(int userId, String nombre, String descripcion, float precio, String signatureState, Location location, String imagePath, String signaturePath, String codigoBarra){
        MySqliteHelper database = new MySqliteHelper(getContext());
        Log.v("Photo path",imagePath);
        int barcodeId,imageId,locationId,productId;

        barcodeId = (int)database.insertBarcode(codigoBarra);
        imageId = (int)database.insertImages(imagePath);
        locationId = (int)database.insertLocation(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
        productId = (int)database.insertProduct(nombre,descripcion,precio,userId,imageId,locationId,barcodeId,signatureState);
        database.insertSignature(signaturePath,productId);
    }
}
