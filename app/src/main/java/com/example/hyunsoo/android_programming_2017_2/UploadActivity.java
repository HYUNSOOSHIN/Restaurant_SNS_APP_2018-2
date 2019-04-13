package com.example.hyunsoo.android_programming_2017_2;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class UploadActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 0;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_IMAGE_CROP = 3;

    EditText edit;
    ImageView image;
    ImageView image2;
    ImageView image3;
    Boolean issetFile=false;
    private Uri imageUri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        //s3 구현
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:a7a14030-975a-4e40-ba26-d9e46e42740f", // Identity Pool ID
                Regions.AP_NORTHEAST_2 // Region
        );

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        final TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

        //


        edit = (EditText) findViewById(R.id.editcontent);

        image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_bye(image);
            }
        });
        image2 = (ImageView) findViewById(R.id.image2);
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_bye(image2);
            }
        });
        image3 = (ImageView) findViewById(R.id.image3);
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_bye(image3);
            }
        });

        Button imageupload = findViewById(R.id.image_uploadbtn);
        imageupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //권한요청
                ActivityCompat.requestPermissions(UploadActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                        REQUEST_PERMISSION_CODE);

                GetAlbum();
                issetFile = true;
            }
        });

        Button uploadbtn = findViewById(R.id.Uploadbtn);
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit text 값 받아서 업로드
                //이미지 파일도..
                String content = edit.getText().toString();
                String filename = "tmp"+currentDateFormat()+".jpg";

                if(issetFile ==false){
                    Toast.makeText(UploadActivity.this,"이미지를 추가하세요",Toast.LENGTH_LONG).show();
                }else{
                    File file = new File(getPath(UploadActivity.this,imageUri));

                    TransferObserver observer = transferUtility.upload(

                            "wepapp123",     /* 업로드 할 버킷 이름 */
                            filename,    /* 버킷에 저장할 파일의 이름 */
                            file       /* 버킷에 저장할 파일  */
                    );


                    //유저정보 받아오는 코드
                    SharedPreferences test = getSharedPreferences("token", MODE_PRIVATE);

                    JSONObject postDataParam = new JSONObject();
                    try {
                        postDataParam.put("token",test.getString("token",""));
                        postDataParam.put("text",content);
                        postDataParam.put("imageuri","https://s3.ap-northeast-2.amazonaws.com/wepapp123/"+filename);
                        //new UploadRequest(UploadActivity.this).execute(postDataParam).get();
                        new UploadRequest(UploadActivity.this).execute(postDataParam).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(UploadActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Button backbtn = findViewById(R.id.Backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //홈 화면으로 이동
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void image_bye(ImageView image){
        image.setImageURI(null);
    }

    //앨범에서 가져오기
    private void GetAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent,REQUEST_IMAGE_PICK);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_OK){
            if(requestCode == REQUEST_IMAGE_PICK) { // 앨범에서 가져오기
                imageUri = data.getData();
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imageUri,"image/*");
                intent.putExtra( "outputX", 200);
                intent.putExtra( "outputY", 200);
                intent.putExtra("aspectX",1);
                intent.putExtra("aspectY",1);
                intent.putExtra("scale",true);
                intent.putExtra("return-data",true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                intent.putExtra( "outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(intent, REQUEST_IMAGE_CROP);

            } else if(requestCode == REQUEST_IMAGE_CROP) { //이미지 크롭 처리하기

                if(image.getDrawable()==null){
                    image.setImageURI(imageUri);
                } else if(image2.getDrawable()==null){
                    image2.setImageURI(imageUri);
                } else
                    image3.setImageURI(imageUri);
            }
        }
    }

    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UploadActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    //uri -> 파일경로 얻는 코드
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
