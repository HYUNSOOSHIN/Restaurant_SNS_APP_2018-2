package com.example.hyunsoo.android_programming_2017_2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    //데이터준비
    ArrayList<String> mData;
    private  Bitmap bitmap;
    ImageView imageView;

    public GridViewAdapter(Context context, ArrayList<String> Data) {
        mContext = context;
        mData = Data;
    }

    public int getCount() {
        return mData.size();
    }
    public Object getItem(int position) {
        return mData.get(position);
    }
    public long getItemId(int position) {
        return position;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            Thread mThread = new Thread() {
                public void run() {
                    try {
                        URL url = new URL(mData.get(position));
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
                imageView = new ImageView(mContext);
                imageView.setAdjustViewBounds(true);
                imageView.setImageBitmap(bitmap);
            }catch(
                    InterruptedException e)

            {
                e.printStackTrace();
            }


//            Bitmap bmp  = BitmapFactory.decodeResource(mContext.getResources(), mThumbIds[position]);
//            bmp = Bitmap.createScaledBitmap(bmp, 320, 240, false);
//
//            imageView = new ImageView(mContext);
//            imageView.setAdjustViewBounds(true);
//            imageView.setImageBitmap(bmp);

        } else {
            imageView = (ImageView) convertView;
        }
        //imageView.setImageResource([position]);



        return imageView;
    }
}