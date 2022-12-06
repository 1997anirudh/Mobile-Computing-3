package com.example.mobilecomputing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    // Video Source - https://www.youtube.com/watch?v=tCIL_CxxdrY
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=(Button) findViewById(R.id.openbutton);
        button.setOnClickListener(new  View.OnClickListener(){
            public void onClick(View view){
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bitmap image = (Bitmap) intent.getExtras().get("data");



        ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArr);
        byte[] imageArray = byteArr.toByteArray();
        Intent redire = new Intent(MainActivity.this, UploadActivity.class);
        redire.putExtra("picture", imageArray);
        startActivity(redire);
    }
}