package com.example.mobilecomputing;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UploadActivity extends AppCompatActivity {

    // Video Source - https://www.youtube.com/watch?v=tCIL_CxxdrY
    byte[] imageAsArray;
    String imageCategory;
    Bitmap imageBitMap;
    Bitmap[] imgs;
    private ImageView image1, image2, image3, image4;
    Uri quad1, quad2, quad3, quad4;
    private ImageView capturedImage;
    private Uri getImageUri(ContentResolver contentResolver, Bitmap inImage, String title) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(contentResolver, inImage, title, null);
        return Uri.parse(path);
    }
    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.uploadactivity);
        Bundle info = getIntent().getExtras();
        imageAsArray = info.getByteArray("picture");
        Bitmap pic = BitmapFactory.decodeByteArray(imageAsArray, 0, imageAsArray.length);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(pic, 240, 240, true);
        imgs = new Bitmap[4];

        // Splitting image into four parts - https://stackoverflow.com/questions/9259503/divide-the-image-into-4-equal-parts-by-clicking-on-the-button
        imgs[0] = Bitmap.createBitmap(scaledBitmap, 0, 0, 120 , 120);
        //quad1 = getImageUri(this.getContentResolver(), imgs[0], "topLeftImageBitMap");
        imgs[1] = Bitmap.createBitmap(scaledBitmap, 120, 0, 120, 120);
        //quad2 = getImageUri(this.getContentResolver(), imgs[0], "topLeftImageBitMap");
        imgs[2] = Bitmap.createBitmap(scaledBitmap,0, 120, 120,120);
        //quad3 = getImageUri(this.getContentResolver(), imgs[0], "topLeftImageBitMap");
        imgs[3] = Bitmap.createBitmap(scaledBitmap, 120, 120, 120, 120);
        //quad4 = getImageUri(this.getContentResolver(), imgs[0], "topLeftImageBitMap");
        ImageView image1 = (ImageView) findViewById(R.id.imageView1);
        image1.setImageBitmap(imgs[0]);
        ImageView image2 = (ImageView) findViewById(R.id.imageView2);
        image2.setImageBitmap(imgs[1]);
        ImageView image3 = (ImageView) findViewById(R.id.imageView3);
        image3.setImageBitmap(imgs[2]);
        ImageView image4 = (ImageView) findViewById(R.id.imageView4);
        image4.setImageBitmap(imgs[3]);


        Button upload=(Button) findViewById(R.id.buttonupload);
        upload.setOnClickListener(new  View.OnClickListener(){
            public void onClick(View view){
                preparePostRequest();
            }
        });

    }
    private void preparePostRequest(){
        String[] quadOutput = new String[4];
        String[] urls = {"http://192.168.0.23:5001/","http://192.168.0.128:5001/","http://192.168.0.226:5001/","http://192.168.0.92:5001/"};
       quadOutput[0] = ImageToServer(imgs[0],urls[0]);
       quadOutput[1] = ImageToServer(imgs[1],urls[1]);
       quadOutput[2] = ImageToServer(imgs[2],urls[2]);
       quadOutput[3] = ImageToServer(imgs[3],urls[3]);

        Toast.makeText(this, "Received responses :"+quadOutput[0]+","+quadOutput[1]+","+quadOutput[2]+","+quadOutput[3], Toast.LENGTH_SHORT).show();
        TextView res1=findViewById(R.id.res1);
        res1.setText(quadOutput[0]);
        TextView res2=findViewById(R.id.res2);
        res2.setText(quadOutput[1]);
        TextView res3=findViewById(R.id.res3);
        res3.setText(quadOutput[2]);
        TextView res4=findViewById(R.id.res4);
        res4.setText(quadOutput[3]);

        for (int i = 0; i < 4; i++) {
            localstorageforimage(imgs[i],quadOutput[i]);
        }

//        localstorageforimage(imgs[0],res[0]);
//        localstorageforimage(imgs[1],res[1]);
//        localstorageforimage(imgs[2],res[2]);
//        localstorageforimage(imgs[3],res[3]);

    }
    // Storing image locally - https://www.tutorialspoint.com/how-to-write-an-image-file-in-internal-storage-in-android
    private void localstorageforimage(Bitmap img, String res){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir"+res, Context.MODE_PRIVATE);

        // timestamp resource used - https://stackoverflow.com/questions/16516888/how-to-get-current-date-time-in-milliseconds-in-android
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        File file = new File(directory, ts + ".jpg");
        if (!file.exists()) {
            Log.d("path", file.toString());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                img.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Source - https://square.github.io/okhttp/
    private String ImageToServer(Bitmap bmp, String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        InputStream iStream = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageAsArray = stream.toByteArray();

        MultipartBody.Builder obj = new MultipartBody.Builder().setType(MultipartBody.FORM);
        obj.addFormDataPart("image", "image"+ ".jpg", RequestBody.create(MediaType.parse("image/*jpg"), imageAsArray));
        RequestBody requestObject = obj.build();
        OkHttpClient httpHelper = new OkHttpClient();
        Request flaskRequest = new Request.Builder().post(requestObject).url(url).build();
        final String[] res = {""};
        httpHelper.newCall(flaskRequest).enqueue(new Callback() {
            public void onResponse(Call call, final Response response) throws IOException {
                res[0] =response.body().string();
                Log.d(TAG, "onResponse: "+res[0]);
            }

            public void onFailure(final Call call, final IOException e) {
                //Toast.makeText(UploadActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res[0];


    }
}