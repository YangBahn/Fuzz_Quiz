package com.kevin_yang.fuzz_quiz;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kevinyang on 9/17/15.
 */
public class FuzzQuizFragment extends ListFragment implements FuzzQuizActivity.Callbacks {
    // Filter Constants
    public static final int ALL = 0;
    public static final int TEXT = 1;
    public static final int IMAGE = 2;

    private static final String TAG = "FuzzQuizFragment";
    // URL to get mItems JSON
    private static String sUrl = "http://quizzes.fuzzstaging.com/quizzes/mobile/1/data.json";
    // JSON Node names
    private static final String TAG_ID = "id";
    private static final String TAG_TYPE = "type";
    private static final String TAG_DATE = "date";
    private static final String TAG_DATA = "data";
    private static final String DIALOG_IMAGE = "image";

    ArrayList<Item> mAllItems = new ArrayList<>(); // back up to restore for "ALL" btn
    ArrayList<Item> mItems = new ArrayList<>(); // back up to restore for "ALL" btn
    private Context mCtx;
    protected ItemAdapter mItemAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCtx = getActivity();
        new FetchItemsTask().execute();
    }

     // item click action
    @Override
    public void onListItemClick(ListView i, View v, int position, long id) {
        Item item = ((ItemAdapter)getListAdapter()).getItem(position);
        String type = item.getType();
        Uri uri = null;
        Intent intent = null;
        // TEXT
        if (type.equals("text")) {
            uri = Uri.parse("https://fuzzproductions.com");
            intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        // IMAGE - open ImageFragment with the item's image
        } else if (type.equals("image")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            item.getImage().compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] decoded = baos.toByteArray();
            FragmentManager fm = getActivity().getFragmentManager();
            ImageFragment.newInstance(decoded).show(fm, DIALOG_IMAGE);
        // OTHERS
        } else {
            return;
        }
    }
    // Callback received from Activity to activate filter
    @Override
    public void onFilterActivated(int filtermode) {

        ArrayList<Item> newItems = new ArrayList<>();
        for (int i = 0; i <  mAllItems.size(); i ++) {
            Item item = mAllItems.get(i);

            switch (filtermode) {
                case ALL:
                    newItems.add(item);
                    break;
                case TEXT:
                    if (item.getType().equals("text")) {
                        newItems.add(item);
                    }
                    break;
                case IMAGE:
                    if (item.getType().equals("image")) {
                        newItems.add(item);
                    }
                    break;
            }
        }
        mItemAdapter.clear();
        mItemAdapter.addAll(newItems);
        mItemAdapter.notifyDataSetChanged();
    }

    //Async task class to get json by making HTTP call
    private class FetchItemsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            String jsonStr = null;
            Bitmap bmp = null; // for images

            try {
                jsonStr = new ItemFetcher().getUrl(sUrl);
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to fetch URL: ", ioe);
            }

            if (jsonStr != null) {
                try {
                    JSONArray jsonItems = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < jsonItems.length(); i++) {
                        JSONObject c = jsonItems.getJSONObject(i);
                        // Create java objects from JSON
                        String id = "N/A";
                        if (c.has(TAG_ID)) {
                            id = c.getString(TAG_ID);
                        }
                        String type = "N/A";
                        if (c.has(TAG_TYPE)) {
                            type = c.getString(TAG_TYPE);
                        }
                        String date = "N/A";
                        if (c.has(TAG_DATE)) {
                            date = c.getString(TAG_DATE);
                        }
                        String data = null;
                        if (c.has(TAG_DATA)) {
                            data = c.getString(TAG_DATA);
                        }


                        // set up image items
                        if (type.equals("image")) {
                            // make sure file exists & URL is valid
                            HttpURLConnection con =  (HttpURLConnection) new URL(data).openConnection();
                            con.setRequestMethod("HEAD");
                            if ( URLUtil.isValidUrl(data) &&
                                    Patterns.WEB_URL.matcher(data).matches() &&
                                    con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                URL url = new URL(data);
                                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            } else {
                                // set no image as default for any error
                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.noimageavailable);
                            }
                        }

                        Item item = new Item(id,type,date, data);
                        item.setImage(bmp);
                        mItems.add(item);
                        mAllItems.add(item); // back up
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ItemFetcher", "Couldn't get any data from the sUrl");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mItemAdapter = new ItemAdapter(mItems);
            setListAdapter(mItemAdapter);
        }
    }


    // Custom Adapter to load item data to the List
    private class ItemAdapter extends ArrayAdapter<Item> {

        public ItemAdapter(ArrayList<Item> items) {
            super(mCtx, 0 , items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_fuzz_quizz, null);
            }

            Item item = getItem(position);
            TextView id = (TextView)convertView.findViewById(R.id.item_id);
            id.setText("ID: " + item.getId());
            TextView type = (TextView)convertView.findViewById(R.id.item_type);
            type.setText("TYPE: " + item.getType());
            TextView date = (TextView)convertView.findViewById(R.id.item_date);
            date.setText("DATE: " + item.getDate());
            TextView data = (TextView)convertView.findViewById(R.id.item_data);
            data.setText(item.getData());
            // Image items, show thumbnail, hide link embeded in 'data'
            ImageView image = (ImageView) convertView.findViewById(R.id.item_image);
            if (!item.getType().equals("image")) {
                image.setVisibility(View.GONE);
                data.setVisibility(View.VISIBLE);
            } else {
                // Set thumbnail image
                image.setImageBitmap(Bitmap.createBitmap(item.getImage()));
                data.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

    }
}
