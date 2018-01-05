package grimdonuts.srcom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    private String videoURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_activity);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                videoURL= null;
            } else {
                videoURL= extras.getString("Video");

            }
        } else {
            videoURL= (String) savedInstanceState.getSerializable("Video");

        }
        WebView browser = (WebView) findViewById(R.id.webview);

       if (videoURL != null && !videoURL.startsWith("http://www.speedrunslive.com"))
        {
            browser.loadUrl(videoURL);
            finish();
        }
<<<<<<< HEAD

=======
      
>>>>>>> origin/master
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "No Video provided",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
            finish();
        }


    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("Video", videoURL);

        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        videoURL = savedInstanceState.getString("Video");

    }

}
