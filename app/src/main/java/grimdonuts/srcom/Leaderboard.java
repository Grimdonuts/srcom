package grimdonuts.srcom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Leaderboard extends AppCompatActivity {

    private ListView lv;
    private String ID;
    private String categoryName;
    private String gameID;
    private String url;
    private ProgressDialog pDialog;
    private String TAG = MainActivity.class.getSimpleName();
    private ArrayList<String> leaderboard = new ArrayList<String>();
    private ArrayList<String> leaderboardTimes = new ArrayList<String>();
    private ArrayList<String> videosList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        lv = (ListView) findViewById(R.id.listView);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                ID= null;
                categoryName=null;
                gameID=null;
            } else {
                ID= extras.getString("CategoryID");
                categoryName=extras.getString("Category");
                gameID=extras.getString("GameID");

            }
        } else {
            ID= (String) savedInstanceState.getSerializable("CategoryID");
            categoryName=(String) savedInstanceState.getSerializable("Category");
            gameID=(String) savedInstanceState.getSerializable("GameID");
        }
        url = "https://www.speedrun.com/api/v1/leaderboards/"+ gameID + "/category/" + ID + "?embed=players";
        TextView catNameDisplay = (TextView) findViewById(R.id.textView2);
        catNameDisplay.setText(categoryName);
        new GetLeaderboard().execute();
        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Intent i = new Intent(Leaderboard.this, VideoActivity.class);
                i.putExtra("Video", videosList.get(position));
                startActivity(i);

            }
        });

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("CategoryID", ID);
        savedInstanceState.putString("Category", categoryName);
        savedInstanceState.putString("GameID", gameID);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.putString("CategoryID", ID);
        savedInstanceState.putString("Category", categoryName);
        savedInstanceState.putString("GameID", gameID);

    }


    @Override
    protected void onDestroy() {
        pDialog.dismiss();
        super.onDestroy();


    }

    private class GetLeaderboard extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Leaderboard.this);
            pDialog.setMessage("Retrieving Leaderboard...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            UrlHandler sh = new UrlHandler();

            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jObject = new JSONObject(jsonStr);
                    JSONObject data = jObject.getJSONObject("data");
                    JSONArray jsonArray = data.getJSONArray("runs");
                    JSONObject players = data.getJSONObject("players");
                    JSONArray playerData = players.getJSONArray("data");



                    if (jsonArray.length() == 0)
                    {
                        leaderboard.add("No runs found!");
                        leaderboardTimes.add("Empty board?");
                        videosList.add("No Video");
                    }
                    else
                    {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject c = jsonArray.getJSONObject(i);
                            JSONObject run = new JSONObject(c.getString("run"));

                            String videoLink = null;
                           if (!run.isNull("videos"))
                            {
                                JSONObject videos = new JSONObject(run.getString("videos"));
                                if (videos.has("links"))
                                {
                                    JSONArray links = videos.getJSONArray("links");
                                    for (int j = 0; j < links.length(); j++)
                                {
                                    JSONObject firstLink = links.getJSONObject(j);
                                    videoLink = firstLink.getString("uri");
                                }
                                }


                            }



                            
                            JSONObject times = new JSONObject(run.getString("times"));
                            String actualTime = times.getString("primary").substring(2).replace("H", "hr ").replace("M", "min ").replace("S", "s");
                            JSONObject playerObject = playerData.getJSONObject(i);
                            String userName;
                            if (playerObject.has("names"))
                            {
                                JSONObject playerNames = playerObject.getJSONObject("names");
                                userName = playerNames.getString("international");
                            }
                            else
                            {
                                userName = playerObject.getString("name");
                            }

                            leaderboard.add(c.getString("place") + " " + userName);
                            leaderboardTimes.add(actualTime);
                            videosList.add(videoLink);
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

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            pDialog.dismiss();

            LeaderboardListAdapter adapter = new LeaderboardListAdapter(Leaderboard.this,leaderboard, leaderboardTimes);
            adapter.notifyDataSetChanged();
            lv.setAdapter(adapter);


        }

    }
}
