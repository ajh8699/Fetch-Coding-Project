package com.example.fetchcodingproject;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView l;
    private static final String URL = "https://fetch-hiring.s3.amazonaws.com/hiring.json";
    private ArrayList<ListItem> itemList;
    private ArrayList<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemList = new ArrayList<>();
        stringList = new ArrayList<String>();
        l = (ListView) findViewById(R.id.list_view_id);

        new GetItems().execute();
    }

    private class GetItems extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHelper sh = new HttpHelper();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(URL);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray items = new JSONArray(jsonStr);

                    // looping through All items
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);

                        int id = Integer.parseInt(item.getString("id"));
                        int listId = Integer.parseInt(item.getString("listId"));
                        String name = item.getString("name");

                        if(!(name.equals("null") || name.equals("")))
                        {
                            ListItem listItem = new ListItem(id, listId, name);
                            itemList.add(listItem);
                        }


                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            // Sort itemList by listId
            for (int i = 0; i < itemList.size(); i++) {
                int pos = i;
                for (int j = i; j < itemList.size(); j++) {
                    if (itemList.get(j).getListId() < itemList.get(pos).getListId())
                        pos = j;
                }
                ListItem temp = itemList.get(pos);
                itemList.set(pos, itemList.get(i));
                itemList.set(i, temp);
            }

            // Sort itemList by name
            for (int i = 0; i < itemList.size() - 1; i++) {
                int pos = i;
                for (int j = i; j < itemList.size(); j++) {
                    if(itemList.get(i).getListId() == itemList.get(j).getListId())
                        if (itemList.get(j).getName().compareTo(itemList.get(pos).getName()) < 0)
                            pos = j;
                }
                ListItem temp = itemList.get(pos);
                itemList.set(pos, itemList.get(i));
                itemList.set(i, temp);
            }

            // Finally, convert itemList to strings
            for(ListItem l : itemList)
            {
                stringList.add(l.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            /*ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, hashList,
                    R.layout.list_item, new String[]{"id", "listId",
                    "name"}, new int[]{R.id.id,
                    R.id.listId, R.id.name});*/
            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, R.layout.list_item,
                    R.id.text, stringList);
            l.setAdapter(adapter);
        }
    }
}