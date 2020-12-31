package com.gmail.petraccaro.angelo.placesofinterest.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.petraccaro.angelo.placesofinterest.R;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CustumAdapterRecognizedPhotos extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Bitmap> bitmap;
    private ArrayList<String> distanza;

    public CustumAdapterRecognizedPhotos(ArrayList<Bitmap> bitmap, ArrayList<String> distanza, Context applicationContext) {
        this.bitmap=bitmap;
        this.distanza=distanza;
        this.context=applicationContext;
        this.layoutInflater = (LayoutInflater) applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return distanza.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if(view == null){
            view=layoutInflater.inflate(R.layout.row_item,viewGroup,false);
        }

        TextView textGrid = view.findViewById(R.id.textGrid);
        ImageView imageGrid = view.findViewById(R.id.imageGrid);

        textGrid.setText("Distanza: "+distanza.get(position));
        imageGrid.setImageBitmap(bitmap.get(position));

        return view;
    }


    public void clearData(){
        this.bitmap.clear();
        this.distanza.clear();
    }
}
