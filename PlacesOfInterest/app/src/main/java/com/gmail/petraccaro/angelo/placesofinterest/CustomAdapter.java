package com.gmail.petraccaro.angelo.placesofinterest;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Predispone la visualizzazione degli elementi
 */
public class CustomAdapter extends ArrayAdapter<ElementoLista>  {

    public CustomAdapter(Context context, int textViewResourceId, List<ElementoLista> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item,null);

        TextView n = (TextView)convertView.findViewById(R.id.textView);
        TextView d = (TextView)convertView.findViewById(R.id.textView2);
        CircleImageView i = (CircleImageView) convertView.findViewById(R.id.imageView2);

        ElementoLista c = getItem(position);
        n.setText(c.getNome());
        d.setText(c.getBreve_descrizione());

        //picasso per passare da uri a img
        Uri myUri=Uri.parse(c.getUrl_foto());
        Picasso.get().load(myUri).into(i);

        return convertView;
    }
}
