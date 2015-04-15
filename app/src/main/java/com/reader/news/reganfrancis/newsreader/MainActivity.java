package com.reader.news.reganfrancis.newsreader;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.reader.news.reganfrancis.adapters.NewsItemListAdapter;
import com.reader.news.reganfrancis.models.NewsItem;
import com.reader.news.reganfrancis.utils.Misc;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends ListActivity implements AbsListView.OnScrollListener {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    NewsItemListAdapter mAdapter;
    private static final String TAG = "MainActivity";
    public int currentPage =0;
    public int currentFirstVisibleItem = 0;
    public int currentVisibleItemCount = 0;
    public int currentScrollState = 0;
    public boolean isLoading = false;
    private Handler handler = new Handler();
    private SwipeRefreshLayout refreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new NewsItemListAdapter(getApplicationContext());
        setListAdapter(mAdapter);
        this.getListView().setOnScrollListener(this);
    }

    public void onScroll(AbsListView view,
                         int firstVisible, int visibleCount, int totalCount) {

        this.currentFirstVisibleItem = firstVisible;
        this.currentVisibleItemCount = visibleCount;

        int topRowVerticalPosition =
                (this.getListView() == null || this.getListView().getChildCount() == 0) ?
                        0 : this.getListView().getChildAt(0).getTop();
        //.setEnabled(topRowVerticalPosition >= 0);

        }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.currentScrollState = scrollState;
        if (this.getListView().getLastVisiblePosition() == mAdapter.getCount() - 1
                && this.getListView().getChildAt(this.getListView().getChildCount() - 1).getBottom() <= this.getListView().getHeight()) {

            if(isLoading == false){
                isLoading = true;
                loadItems(String.valueOf(currentPage + 1));
            }

        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        NewsItem item = (NewsItem) getListAdapter().getItem(position);

        Intent startNewActivity = new Intent(this, NewsDetailActivity.class);
        startNewActivity.putExtra("NewsArticle", item);
        startActivity(startNewActivity);
    }

    @Override
    public long getSelectedItemId() {
        return super.getSelectedItemId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_pull_to_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter.getCount() == 0)
            loadItems(String.valueOf(0));
    }

    private void loadItems(String page) {
        try {
            new LongRunningGetIO().execute(page);
        } catch(Exception e){
            Log.i(TAG + "Exception in loadItems", e.toString());
        }
    }

    private class LongRunningGetIO extends AsyncTask <String, Void, String> {


        OkHttpClient client = new OkHttpClient();
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
        protected String doInBackground(String... params) {


            Log.i ("Calling server" , Misc.SERVER_URL + "/newsreader/feeds?page="+params[0]);
            String url = Misc.SERVER_URL + "/newsreader/feeds?page="+params[0] ;
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;


        }

        protected void onPostExecute(String results) {
            if (results!=null) {
                Log.i("On postExecute", results);
                Gson gson = new Gson();
                Type collectionType = new TypeToken<List<NewsItem>>(){}.getType();
                List<NewsItem> details = gson.fromJson(results, collectionType);


                for (NewsItem news : details)
                {
                    if(!news.description.contains("none")){
                        mAdapter.add(news);}
                }
                isLoading = false;
            }
        }
    }



}





