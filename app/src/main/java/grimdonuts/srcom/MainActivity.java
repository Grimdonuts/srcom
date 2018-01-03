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
    private ArrayList<String> title = new ArrayList<String>();
    private ArrayList<String> image = new ArrayList<String>();
    private ArrayList<String> id = new ArrayList<String>();
    private String url;
    private String userSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.listView);
        final EditText userInput = (EditText) findViewById(R.id.userGame);
        deleteDirectoryTree(this.getCacheDir());

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              userSearch = userInput.getText().toString();
              url = "https://www.speedrun.com/api/v1/games?name=" + userSearch;
              title.clear();
              id.clear();
              image.clear();

                new GetGames().execute();
            }
        });

        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

               Intent i = new Intent(MainActivity.this, Categories.class);
                i.putExtra("ID", id.get(position));
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

        savedInstanceState.putString("Search", userSearch);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        userSearch = savedInstanceState.getString("Search");
    }

    @Override
    protected void onResume(){
        super.onResume();
        EditText userInput = (EditText) findViewById(R.id.userGame);
        url = "https://www.speedrun.com/api/v1/games?name=" + userInput.getText();
        title.clear();
        id.clear();
        image.clear();

        new GetGames().execute();
        GamesListAdapter adapter = new GamesListAdapter(MainActivity.this,title,image);
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        pDialog.dismiss();
        super.onDestroy();
    }

    private class GetGames extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
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
                    JSONArray jsonArray = jObject.getJSONArray("data");
                    if (jsonArray.length() == 0)
                    {
                        title.add("Couldn't find game");
                    }
                    else
                    {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject c = jsonArray.getJSONObject(i);
                            id.add(c.getString("id"));
                            JSONObject names = new JSONObject(c.getString("names"));
                            title.add(names.getString("international"));
                            JSONObject assets = new JSONObject(c.getString("assets"));
                            JSONObject images = new JSONObject(assets.getString("cover-medium"));

                            image.add(images.getString("uri"));
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

            GamesListAdapter adapter = new GamesListAdapter(MainActivity.this,title,image);
            adapter.notifyDataSetChanged();
            lv.setAdapter(adapter);


        }

    }
}
