package com.reader.news.reganfrancis.newsreader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.reader.news.reganfrancis.models.NewsDetailItem;
import com.reader.news.reganfrancis.models.NewsItem;
import com.reader.news.reganfrancis.utils.Misc;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by reganfrancis on 2/12/14.
 */
public class NewsDetailActivity extends Activity {

    private static String TAG = "NewsDetailActivity";
    public NewsItem item;

    public WebView viewPager;
    public TextView mTime;
    public TextView mTitleText;
    public TextView mDesc;
    public TextView mNewsSite;
    public ImageView imageView;
    public TextView mCategory;
    DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //public ImagePagerAdapter adapter;

    String[] formats = new String[] {
            "yyyy-MM-dd HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_desc);

        Intent intent = getIntent();

        // 2. get person object from intent
        NewsItem newsItem = (NewsItem) intent.getSerializableExtra("NewsArticle");
        item = newsItem;

        mTitleText = (TextView) findViewById(R.id.txtHeader);
        mTime = (TextView) findViewById(R.id.txtTime);
        mDesc = (TextView) findViewById(R.id.txtDesc);
        imageView = (ImageView) findViewById(R.id.image);
        mNewsSite = (TextView) findViewById(R.id.txtSite);
        mCategory = (TextView) findViewById(R.id.txtCategory);
    }



    @Override
    public void onResume() {
        super.onResume();

        loadItems(item);
    }

    // Load stored ToDoItems
    private void loadItems(NewsItem item) {
        try {
            new LongRunningGetIO(item).execute();
        } catch(Exception e){
            Log.i(TAG + "Exception in loadItems", e.toString());
        }
    }

    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

        public NewsItem newsItem;

        public LongRunningGetIO(NewsItem newsItem){
            this.newsItem = newsItem;
        }

        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
            InputStream in = entity.getContent();

            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];
                n =  in.read(b);
                if (n>0) out.append(new String(b, 0, n));
            }

            return out.toString();
        }


        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet(Misc.SERVER_URL + "/newsreader/getFeedContent?id=" + newsItem.article_id );
            String text = null;
            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);
            } catch (Exception e) {
                return e.getLocalizedMessage();
            }
            return text;
        }


        protected void onPostExecute(String results) {
            if (results!=null) {
                Log.i("On postExecute", results);
                JSONObject json = null;
                URL url = null;
                Gson gson = new GsonBuilder().create();
                NewsDetailItem p = gson.fromJson(results, NewsDetailItem.class);
                Log.i("TAG", p.headlines);
                String val = p.created_at;
                val = val.substring(0, val.length() - 4);

                Date t = null;

                try{
                    //SimpleDateFormat sdf = new SimpleDateFormat(fromFormat, Locale.US);
                    //Log.i(" asdasd", sdf.format(val).toString());
                }catch(Exception ex){
                    Log.i("error--->",ex.toString());
                }

                PrettyTime prettyTime = new PrettyTime();

                mTime.setText("");
                mTitleText.setText(p.headlines);
                mDesc.setText(p.full_description);
                mNewsSite.setText("Times of India");
                String image_url = p.coverImage;

                if(p.coverImage == null){
                    imageView.setImageResource(R.drawable.no_picture);
                }else {
                    new DownloadImageTask(imageView)
                            .execute(image_url);
                }

                mCategory.setText(p.category.toUpperCase());
                mCategory.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

            }
        }

        public String convert(String dateUnformatted){
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
            Date date = null;
            Date dateN = new Date();

            try {
                date = formatter.parse(dateUnformatted);
            } catch (Exception e) {
                e.printStackTrace();
            }

            long diff = date.getTime() - dateN.getTime();
            long diffMinutes = diff / (60 * 1000) % 60;

            if(diffMinutes < 1) return "Updated just now";

            return "Updated " + diffMinutes + " ago.";
        }


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