package com.gmail.petraccaro.angelo.placesofinterest.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.petraccaro.angelo.placesofinterest.Models.Post;
import com.gmail.petraccaro.angelo.placesofinterest.R;
import com.squareup.picasso.Picasso;

import java.util.List;



/**
 * Predispone la visualizzazione degli elementi
 */

public class CustomAdapter extends ArrayAdapter<Post>  {


    public CustomAdapter(Context context, int textViewResourceId, List<Post> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item,null);


        TextView u = convertView.findViewById(R.id.breveDescrizione);
        TextView n = convertView.findViewById(R.id.NomeText);
        TextView d = convertView.findViewById(R.id.nomeText);
        ImageView i = convertView.findViewById(R.id.imageView2);

        Post c = getItem(position);


        n.setText(c.getNome());
        d.setText(c.getBreve_descrizione());
        u.setText(c.getUsername());

        //picasso per passare da uri a img
        Uri myUri=Uri.parse(c.getUrl_foto());
        Picasso.get().load(myUri).into(i);

        return convertView;
    }
}
