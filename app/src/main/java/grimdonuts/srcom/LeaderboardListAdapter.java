package grimdonuts.srcom;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by jason on 12/11/2017.
 */

public class LeaderboardListAdapter extends ArrayAdapter<String> {

  private final Activity context;
  private final ArrayList<String> itemname;
  private final ArrayList<String> times;

  public LeaderboardListAdapter(
    Activity context,
    ArrayList<String> itemname,
    ArrayList<String> times
  ) {
    super(context, R.layout.leaderboard_list_item, itemname);
    this.context = context;
    this.itemname = itemname;
    this.times = times;
  }

  public View getView(int position, View view, ViewGroup parent) {
    LayoutInflater inflater = context.getLayoutInflater();
    View rowView = inflater.inflate(R.layout.leaderboard_list_item, null, true);
    TextView Leaderboard = (TextView) rowView.findViewById(R.id.leaderboard);
    TextView TimesDisplay = (TextView) rowView.findViewById(R.id.timesDisplay);
    Leaderboard.setText(itemname.get(position));
    TimesDisplay.setText(times.get(position));
    switch (position) {
      case 0:
        Leaderboard.setTextColor(Color.parseColor("#D4AF37"));
        break;
      case 1:
        Leaderboard.setTextColor(Color.parseColor("#C0C0C0"));
        break;
      case 2:
        Leaderboard.setTextColor(Color.parseColor("#CD7F32"));
        break;
    }
    return rowView;
  }
}
