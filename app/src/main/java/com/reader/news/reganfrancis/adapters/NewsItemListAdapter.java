package com.reader.news.reganfrancis.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reader.news.reganfrancis.models.NewsItem;
import com.reader.news.reganfrancis.newsreader.R;
import com.reader.news.reganfrancis.utils.Misc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by reganfrancis on 28/11/14.
 */
public class NewsItemListAdapter extends BaseAdapter{

    private final List<NewsItem> mItems = new ArrayList<NewsItem>();
    private final Context mContext;

    private static final String TAG = "Lab-UserInterface";
    public int count = 15;
    public NewsItemListAdapter(Context context) {

        mContext = context;

    }

    public void add(NewsItem item)
    {
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void clear()
    {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder{

        TextView newsHeader = null;
        TextView newsDescription = null;
        TextView newsSite = null;
        ImageView image = null;
        TextView category = null;


        public ViewHolder(View v) {
            newsHeader = (TextView) v.findViewById(R.id.txtHeader);
            newsDescription = (TextView) v.findViewById(R.id.txtDesc);
            newsSite = (TextView) v.findViewById(R.id.txtSite);
            image = (ImageView) v.findViewById(R.id.image);
            category = (TextView) v.findViewById(R.id.txtCategory);
        }
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

      final NewsItem toDoItem = (NewsItem) getItem(i);
        RelativeLayout itemLayout = (RelativeLayout) view;
        ViewHolder viewHolder = null;

        if(itemLayout == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemLayout = (RelativeLayout) inflater.inflate(R.layout.activity_main, viewGroup, false);
            viewHolder = new ViewHolder(itemLayout);
            itemLayout.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) itemLayout.getTag();
        }

        viewHolder.newsHeader.setText(toDoItem.headlines);
        viewHolder.newsDescription.setText(toDoItem.description);
        viewHolder.newsSite.setText("Times of India");
        viewHolder.category.setText(toDoItem.category.toUpperCase());
        if(toDoItem.imageurl == null){
            viewHolder.image.setImageResource(R.drawable.no_picture);
        }else {
            new DownloadImageTask(viewHolder.image)
                    .execute(toDoItem.imageurl);
        }
        return itemLayout;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            String newURL = null;
            Log.i("url is ", urldisplay);
            if(urldisplay.contains("http")){
                newURL = urldisplay;
            }else{
                newURL = Misc.URLofTOI + urldisplay;
            }

            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(newURL).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.i("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
