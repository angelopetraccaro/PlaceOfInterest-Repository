package com.gmail.petraccaro.angelo.placesofinterest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


public class Detect extends AppCompatActivity {
    ImageView imageProfile;
    GridView gridView;
    String [] distance={"distance 1","distance 2","distance 3","distance 4"};
    int[] imagesGrid= {R.drawable.angelo,R.drawable.angelo,R.drawable.angelo,R.drawable.angelo};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);

        imageProfile=findViewById(R.id.imgFace);
        imageProfile.setImageResource(R.drawable.angelo);
        gridView=findViewById(R.id.grid);

        CustomAdapter1 customAdapter=new CustomAdapter1(distance,imagesGrid,this);
        gridView.setAdapter(customAdapter);

    }

    public class CustomAdapter1 extends BaseAdapter{

        private String[] imagesName;
        private int[] imagesPhoto;
        private Context context;
        private LayoutInflater layoutInflater;

        public CustomAdapter1(String[] imagesName, int[] imagesPhoto, Context context) {
            this.imagesName = imagesName;
            this.imagesPhoto = imagesPhoto;
            this.context = context;
            this.layoutInflater =  (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imagesPhoto.length;
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

            textGrid.setText(imagesName[position]);
            imageGrid.setImageResource(imagesPhoto[position]);

            return view;
        }
    }
}