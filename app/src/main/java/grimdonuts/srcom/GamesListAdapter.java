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

public class GamesListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemname;
    private final ArrayList<String> images;


    public GamesListAdapter(Activity context, ArrayList<String> itemname, ArrayList<String> images) {
        super(context, R.layout.games_list_item, itemname);

        this.context=context;
        this.itemname=itemname;
        this.images=images;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.games_list_item, null,true);
        TextView Title = (TextView) rowView.findViewById(R.id.Title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        Title.setText(itemname.get(position));
        if (itemname.get(position) != "Couldn't find game")
        {
            Picasso.with(context).invalidate(images.get(position));
            Picasso.with(context).load(images.get(position)).into(imageView);
        }

        return rowView;

    };
}