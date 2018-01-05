package grimdonuts.srcom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private ProgressDialog pDialog;
    private String TAG = MainActivity.class.getSimpleName();
    private ArrayList<String> gameName = new ArrayList<String>();
    private ArrayList<String> categoryName = new ArrayList<String>();
    private ArrayList<String> imageURL = new ArrayList<String>();
    private ArrayList<String> videoList = new ArrayList<String>();
    private ArrayList<String> playerLeadTime = new ArrayList<String>();
    private String url;
    private String jsonStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.listView);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                jsonStr=null;
            } else {
                jsonStr=extras.getString("WebResponse");

            }
        } else {
            jsonStr=(String) savedInstanceState.getSerializable("WebResponse");
        }
        deleteDirectoryTree(this.getCacheDir());
        url = "https://www.speedrun.com/api/v1/runs?status=verified&orderby=submitted&direction=desc&max=10&embed=players,category,game";
        categoryName.clear();
        imageURL.clear();
        gameName.clear();
        new GetRecent().execute();
        MainListAdapter adapter = new MainListAdapter(MainActivity.this,gameName,imageURL,categoryName, playerLeadTime);
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);
        final Button button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(i);

            }
        });
        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                pDialog.dismiss();
                Intent i = new Intent(MainActivity.this, VideoActivity.class);
                i.putExtra("Video", videoList.get(position));
                startActivity(i);

            }
        });
    }

    public static void deleteDirectoryTree(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteDirectoryTree(child);
            }
        }

        fileOrDirectory.delete();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("WebResponse", jsonStr);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        jsonStr = savedInstanceState.getString("WebResponse");


    }

    @Override
    protected void onDestroy() {
        pDialog.dismiss();
        super.onDestroy();
    }

    private class GetRecent extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...Retrieving latest runs from speedrun.com.\n Tap outside this window dismiss this.");
            pDialog.setCancelable(true);
            pDialog.setCanceledOnTouchOutside(true);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            UrlHandler sh = new UrlHandler();
           if (jsonStr == null)
           {
               jsonStr = sh.makeServiceCall(url);
               Log.e(TAG, "Response from url: " + jsonStr);
           }


            if (jsonStr != null) {
                try {
                    JSONObject jObject = new JSONObject(jsonStr);
                    JSONArray jsonArray = jObject.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        JSONObject game = c.getJSONObject("game");
                        JSONObject gameData = game.getJSONObject("data");
                        JSONObject names = gameData.getJSONObject("names");

                            gameName.add(names.getString("international"));


                        JSONObject assets = gameData.getJSONObject("assets");
                        JSONObject coverMedium = assets.getJSONObject("cover-medium");

                            imageURL.add(coverMedium.getString("uri"));

                        JSONObject category = c.getJSONObject("category");
                        JSONObject categoryData = category.getJSONObject("data");

                            categoryName.add( categoryData.getString("name"));


                       String videoLink = null;
                        if (!c.isNull("videos")) {
                            JSONObject videos = c.getJSONObject("videos");
                            if (videos.has("links")) {
                                JSONArray links = videos.getJSONArray("links");
                                for (int j = 0; j < links.length(); j++) {
                                    JSONObject firstLink = links.getJSONObject(j);
                                    videoLink = firstLink.getString("uri");
                                }
                            }
                        }
                        JSONObject times = c.getJSONObject("times");
                        JSONObject players = c.getJSONObject("players");
                        JSONArray playerData = players.getJSONArray("data");
                        JSONObject playerNames = new JSONObject();
                        String userName = null;
                        for (int j = 0; j<playerData.length(); j++)
                        {
                            JSONObject playerArrayObject = playerData.getJSONObject(j);

                            if (playerArrayObject.has("names"))
                            {
                                playerNames = playerArrayObject.getJSONObject("names");
                                userName = playerNames.getString("international");
                            }
                            else
                            {
                                userName = playerNames.getString("name");
                            }

                        }

                        String leaderPosTimeUser = times.getString("primary").substring(2).replace("H", "hr ").replace("M", "min ").replace("S", "s")
                                + " " + " By " + userName;
                        playerLeadTime.add(leaderPosTimeUser);
                        videoList.add(videoLink);

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

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            pDialog.dismiss();

            MainListAdapter adapter = new MainListAdapter(MainActivity.this,gameName,imageURL, categoryName, playerLeadTime);
            adapter.notifyDataSetChanged();
            lv.setAdapter(adapter);


        }

    }
}
