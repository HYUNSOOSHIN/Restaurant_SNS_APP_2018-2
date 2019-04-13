package com.example.hyunsoo.android_programming_2017_2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class Profile_editActivity extends Activity {

    private static final int REQUEST_PERMISSION_CODE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_IMAGE_CROP = 3;

    private String mPhotoFileName;
    private File mPhotoFile=null;
    private static Uri imageUri;
    Bitmap bitmap;

    TransferUtility transferUtility;

    SharedPreferences test;

    CharSequence items[] = {
            "앨범선택",
            "기본이미지",
            "닫기"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        //권한요청
        ActivityCompat.requestPermissions(Profile_editActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                REQUEST_PERMISSION_CODE);

        //s3 구현
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:a7a14030-975a-4e40-ba26-d9e46e42740f", // Identity Pool ID
                Regions.AP_NORTHEAST_2 // Region
        );

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        transferUtility = new TransferUtility(s3, getApplicationContext());
        //

        final EditText edit_name = (EditText) findViewById(R.id._username);
        final EditText edit_email = (EditText) findViewById(R.id._useremail);

        Intent intent = getIntent();
        edit_name.setText(intent.getStringExtra("username"));
        edit_email.setText(intent.getStringExtra("email"));
        final String user_image = intent.getStringExtra("imageurl");

        if(user_image != "null") {
            Log.e("ddddd","image_url: "+user_image);
            Thread mThread = new Thread() {
                public void run() {
                    try {
                        URL url = new URL(user_image);
                        //web에서 이미지 가져오고 imageview에 저장할 bitmap 생성
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setDoInput(true);// 서버로부터 응답 수신
                        con.connect();

                        InputStream is = con.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            mThread.start();//쓰레드실행
            try

            {
                mThread.join();
                ImageView icon = (ImageView) findViewById(R.id.user_image);
                icon.setImageBitmap(bitmap);
            }catch(
                    InterruptedException e)

            {
                e.printStackTrace();
            }
        } else{
            ImageView userimage = findViewById(R.id.user_image);
            userimage.setImageBitmap(null);
        }

        Button image_change_btn = findViewById(R.id.image_change_btn);
        image_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Profile_editActivity.this);
                alert.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected = items[which].toString();

                        switch(selected){
                            case "앨범선택":
                                GetAlbum();
                                break;
                            case "기본이미지":
                                //TakePicture();
                                test = getSharedPreferences("token",MODE_PRIVATE);

                                JSONObject postDataParam = new JSONObject();
                                try {
                                    postDataParam.put("token",test.getString("token",""));
                                    postDataParam.put("imageurl","null");
                                    new Profile_ImageRequest(Profile_editActivity.this).execute(postDataParam);
                                    ImageView userimage = findViewById(R.id.user_image);
                                    userimage.setImageResource(R.drawable.temp_image);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "닫기":
                                Toast.makeText(Profile_editActivity.this,"걍 닫아버림",Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
                alert.setTitle("프로필 사진 변경");
                alert.show();
            }
        });

        Button infobtn = findViewById(R.id.changebtn);
        infobtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences test = getSharedPreferences("token",MODE_PRIVATE);
                JSONObject postDataParam = new JSONObject();
                try {
                    postDataParam.put("username", edit_name.getText().toString());
                    postDataParam.put("email", edit_email.getText().toString());
                    postDataParam.put("token",test.getString("token",""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Profile_updateRequest(Profile_editActivity.this).execute(postDataParam);
                Toast.makeText(getApplicationContext(), "개인정보 변경 완료",Toast.LENGTH_LONG).show();
            }
        });

        Button passwordbtn = findViewById(R.id.passwordbtn);
        passwordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile_editActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_passchange, null);
                builder.setView(view);
                final Button submit = (Button) view.findViewById(R.id.buttonSubmit);
                final EditText edit_pre_pass = (EditText) view.findViewById(R.id.edit_pre_pass);
                final EditText edit_new_pass = (EditText) view.findViewById(R.id.edit_new_pass);
                final EditText edit_new_pass2 = (EditText) view.findViewById(R.id.edit_new_pass2);

                final AlertDialog dialog = builder.create();
                submit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        SharedPreferences test = getSharedPreferences("token",MODE_PRIVATE);
                        String pre_pass = edit_pre_pass.getText().toString();
                        String new_pass = edit_new_pass.getText().toString();
                        String new_pass2 = edit_new_pass2.getText().toString();

                        if(new_pass.equals(new_pass2)){
                            Toast.makeText(getApplicationContext(), "비밀번호 변경 완료",Toast.LENGTH_LONG).show();
                            //비밀번호수정코드
                            //이전 비밀번호 확인 -> 비밀번호 수정
                            JSONObject postDataParam = new JSONObject();
                            try {
                                postDataParam.put("pre_password",pre_pass);
                                postDataParam.put("new_password", new_pass);
                                postDataParam.put("token",test.getString("token",""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new Pass_updateRequest(Profile_editActivity.this).execute(postDataParam);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "비밀번호가 틀립니다.",Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        Button logoutbtn = findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences test = getSharedPreferences("token",MODE_PRIVATE);
                SharedPreferences.Editor editor = test.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(Profile_editActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //사진 촬영하기
    private void TakePicture() {
        //권한요청
        ActivityCompat.requestPermissions(Profile_editActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                REQUEST_PERMISSION_CODE);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            //1. 카메라 앱으로 찍은 이미지를 저장할 파일 객체 생성
            mPhotoFileName = "IMG" +currentDateFormat()+ ".jpg";
            //mPhotoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), mPhotoFileName);
            mPhotoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),mPhotoFileName);

            if (mPhotoFile != null) {
                //2. 생성된 파일 객체에 대한 Uri 객체를 얻기     sdcard/DCIM/Camera 경로에 사진저장하고싶음
                imageUri = FileProvider.getUriForFile(Profile_editActivity.this, "com.example.hyunsoo.android_programming_2017_2", mPhotoFile);
                Log.e("ddddd",mPhotoFile.toString());
                Log.e("ddddd",imageUri.toString());
                //3. Uri 객체를 Extras를 통해 카메라 앱으로 전달
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            //    startActivity(intent);
            }
        }
    }

    //앨범에서 가져오기
    private void GetAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent,REQUEST_IMAGE_PICK);
        }

    }

    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_OK){

            if (requestCode == REQUEST_IMAGE_CAPTURE) { //사진 촬영하기
                Log.e("ddddd",imageUri.toString());
                //Toast.makeText(Profile_editActivity.this,imageUri.toString(),Toast.LENGTH_LONG).show();
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

//                ImageView imageview = (ImageView) findViewById(R.id.user_image);
//                imageview.setImageURI(imageUri);

            } else if(requestCode == REQUEST_IMAGE_PICK) { // 앨범에서 가져오기
                imageUri = data.getData();
                //Toast.makeText(Profile_editActivity.this,imageUri.toString(),Toast.LENGTH_LONG).show();
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

//            ImageView imageview = (ImageView) findViewById(R.id.user_image);
//            imageview.setImageURI(imageUri);
            } else if(requestCode == REQUEST_IMAGE_CROP) { //이미지 크롭 처리하기
                imageUri = data.getData();

                test = getSharedPreferences("token",MODE_PRIVATE);

                String filename = "profile_image_"+test.getString("id","")+".jpg";

                TransferObserver observer = transferUtility.upload(

                        "wepapp123",     /* 업로드 할 버킷 이름 */
                        filename,    /* 버킷에 저장할 파일의 이름 */
                        new File(getPath(Profile_editActivity.this,imageUri))        /* 버킷에 저장할 파일  */
                );

                JSONObject postDataParam = new JSONObject();
                try {
                    postDataParam.put("token",test.getString("token",""));
                    postDataParam.put("imageurl","https://s3.ap-northeast-2.amazonaws.com/wepapp123/"+filename);
                    new Profile_ImageRequest(Profile_editActivity.this).execute(postDataParam);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                ImageView imageview = (ImageView) findViewById(R.id.user_image);
                imageview.setImageURI(imageUri);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Profile_editActivity.this,MainActivity.class);
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
