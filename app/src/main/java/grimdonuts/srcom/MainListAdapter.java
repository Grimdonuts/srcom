package grimdonuts.srcom;

import android.app.Activity;
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

public class MainListAdapter extends ArrayAdapter<String> {

  private final Activity context;
  private final ArrayList<String> itemname;
  private final ArrayList<String> images;
  private final ArrayList<String> categoryName;
  private final ArrayList<String> playerLeadTime;

  public MainListAdapter(
    Activity context,
    ArrayList<String> itemname,
    ArrayList<String> images,
    ArrayList<String> categoryName,
    ArrayList<String> playerLeadTime
  ) {
    super(context, R.layout.main_list, itemname);
    this.context = context;
    this.itemname = itemname;
    this.images = images;
    this.categoryName = categoryName;
    this.playerLeadTime = playerLeadTime;
  }

  public View getView(int position, View view, ViewGroup parent) {
    LayoutInflater inflater = context.getLayoutInflater();
    View rowView = inflater.inflate(R.layout.main_list, null, true);
    TextView Title = (TextView) rowView.findViewById(R.id.GameTitle);
    ImageView imageView = (ImageView) rowView.findViewById(R.id.GameImage);
    TextView Category = (TextView) rowView.findViewById(R.id.CategoryName);
    TextView PlayerTime = (TextView) rowView.findViewById(R.id.Time);
    Title.setText(itemname.get(position));
    Picasso.with(context).invalidate(images.get(position));
    Picasso.with(context).load(images.get(position)).into(imageView);
    Category.setText(categoryName.get(position));
    PlayerTime.setText(playerLeadTime.get(position));

    return rowView;
  }
}
