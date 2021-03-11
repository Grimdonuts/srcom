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

public class CategoriesListAdapter extends ArrayAdapter<String> {

  private final Activity context;
  private final ArrayList<String> itemname;

  public CategoriesListAdapter(Activity context, ArrayList<String> itemname) {
    super(context, R.layout.categories_list_item, itemname);
    this.context = context;
    this.itemname = itemname;
  }

  public View getView(int position, View view, ViewGroup parent) {
    LayoutInflater inflater = context.getLayoutInflater();
    View rowView = inflater.inflate(R.layout.categories_list_item, null, true);
    TextView Category = (TextView) rowView.findViewById(R.id.category);
    Category.setText(itemname.get(position));
    return rowView;
  }
}
