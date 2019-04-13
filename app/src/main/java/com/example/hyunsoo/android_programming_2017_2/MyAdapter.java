package com.example.hyunsoo.android_programming_2017_2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

class MyItem {
    String mimageURL; // image resource
    String nWriter; // text
    String ncontent; // text
    MyItem(String aimageURL, String aWriter, String acontent) { mimageURL = aimageURL; nWriter = aWriter; ncontent = acontent; }
}
class MyAdapter extends BaseAdapter {
    private  Bitmap bitmap;
    private Context mContext;
    private int mResource;
    private ArrayList<MyItem> mItems = new ArrayList<MyItem>();
    public MyAdapter(Context context, int resource, ArrayList<MyItem> items) {
        mContext = context;
        mItems = items;
        mResource = resource;
    }
    public int getCount() { return mItems.size(); }
    public Object getItem(int position) { return mItems.get(position); }
    public long getItemId(int position) { return position;}
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent,false);
        }

        Thread mThread = new Thread() {
            public void run() {
                try {
                    URL url = new URL(mItems.get(position).mimageURL);
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
            ImageView icon = (ImageView) convertView.findViewById(R.id.list_image);
            icon.setImageBitmap(bitmap);
        }catch(
                InterruptedException e)

        {
            e.printStackTrace();
        }

//        //URL에서 이미지 불러오는 라이브러리
//        Picasso.with(mContext).load(mItems.get(position).mimageURL).into(new Target(){
//
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                ImageView icon = (ImageView) finalConvertView.findViewById(R.id.list_image);
//                icon.setImageBitmap(bitmap);
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {}
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {}
//        });

// Set Text 01
        TextView name = (TextView) convertView.findViewById(R.id.list_writer);
        name.setText(mItems.get(position).nWriter);
// Set Text 02
        TextView age = (TextView) convertView.findViewById(R.id.list_content);
        age.setText(mItems.get(position).ncontent);
        return convertView;
    }
}