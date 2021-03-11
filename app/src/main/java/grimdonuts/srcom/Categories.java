package grimdonuts.srcom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Categories extends AppCompatActivity {

  private ListView lv;
  private ProgressDialog pDialog;
  private String url;
  private String TAG = SearchActivity.class.getSimpleName();
  private ArrayList<String> category = new ArrayList<String>();
  private String ID;
  private ArrayList<String> categoryID = new ArrayList<String>();
  private String jsonStr;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_categories);
    lv = (ListView) findViewById(R.id.listView);
    if (savedInstanceState == null) {
      Bundle extras = getIntent().getExtras();
      if (extras == null) {
        ID = null;
        jsonStr = null;
      } else {
        ID = extras.getString("ID");
        jsonStr = extras.getString("WebResponse");
      }
    } else {
      ID = (String) savedInstanceState.getSerializable("ID");
      jsonStr = (String) savedInstanceState.getSerializable("WebResponse");
    }
    url = "https://www.speedrun.com/api/v1/games/" + ID + "/categories";
    new GetCategories().execute();
    lv.setClickable(true);
    lv.setOnItemClickListener(
      new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(
          AdapterView<?> arg0,
          View arg1,
          int position,
          long arg3
        ) {
          Intent i = new Intent(Categories.this, Leaderboard.class);
          i.putExtra("Category", category.get(position));
          i.putExtra("CategoryID", categoryID.get(position));
          i.putExtra("GameID", ID);
          startActivity(i);
        }
      }
    );
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putString("ID", ID);
    savedInstanceState.putString("WebResponse", jsonStr);
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    jsonStr = savedInstanceState.getString("WebResponse");
    ID = savedInstanceState.getString("ID");
  }

  @Override
  protected void onDestroy() {
    pDialog.dismiss();
    super.onDestroy();
  }

  private class GetCategories extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pDialog = new ProgressDialog(Categories.this);
      pDialog.setMessage("Please wait...");
      pDialog.setCancelable(false);
      pDialog.show();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
      if (jsonStr == null) {
        UrlHandler sh = new UrlHandler();
        jsonStr = sh.makeServiceCall(url);
        Log.e(TAG, "Response from url: " + jsonStr);
      }
      if (jsonStr != null) {
        try {
          JSONObject jObject = new JSONObject(jsonStr);
          JSONArray jsonArray = jObject.getJSONArray("data");

          if (jsonArray.length() == 0) {
            category.add("Couldn't find game or categories");
          } else {
            for (int i = 0; i < jsonArray.length(); i++) {
              JSONObject c = jsonArray.getJSONObject(i);
              category.add(c.getString("name"));
              categoryID.add(c.getString("id"));
            }
          }
        } catch (final JSONException e) {
          Log.e(TAG, "Json parsing error: " + e.getMessage());
          runOnUiThread(
            new Runnable() {
              @Override
              public void run() {
                Toast
                  .makeText(
                    getApplicationContext(),
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG
                  )
                  .show();
              }
            }
          );
        }
      } else {
        Log.e(TAG, "Couldn't get json from server.");
        runOnUiThread(
          new Runnable() {
            @Override
            public void run() {
              Toast
                .makeText(
                  getApplicationContext(),
                  "Couldn't get json from server. Check LogCat for possible errors!",
                  Toast.LENGTH_LONG
                )
                .show();
            }
          }
        );
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      super.onPostExecute(result);
      pDialog.dismiss();
      CategoriesListAdapter adapter = new CategoriesListAdapter(
        Categories.this,
        category
      );
      adapter.notifyDataSetChanged();
      lv.setAdapter(adapter);
    }
  }
}
