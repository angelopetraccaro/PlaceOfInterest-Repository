package com.gmail.petraccaro.angelo.placesofinterest;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class GalleryActivity extends AppCompatActivity {
    private ImageView imgDetect;
    private Button btnProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        imgDetect=findViewById(R.id.imgFace);
        btnProgress=findViewById(R.id.progress);

        final Bitmap mBitmap= BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.angelo);
        imgDetect.setImageBitmap(mBitmap);

        final Paint boxPaint=new Paint();
        /*int dpSize =  10;
        DisplayMetrics dm = getResources().getDisplayMetrics() ;
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, dm);
        boxPaint.setStrokeWidth(strokeWidth);*/
        boxPaint.setStrokeWidth(10);
        boxPaint.setColor(Color.GREEN);
        boxPaint.setStyle(Paint.Style.STROKE);

        final Bitmap tempBitmap= Bitmap.createBitmap(mBitmap.getWidth(),mBitmap.getHeight(),Bitmap.Config.RGB_565);
        final Canvas canvas=new Canvas(tempBitmap);
        canvas.drawBitmap(mBitmap,0,0,null);

        btnProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceDetector faceDetector=new FaceDetector.Builder(getApplicationContext())
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .setMode(FaceDetector.FAST_MODE)
                        .build();

                if(!faceDetector.isOperational()) {
                    Toast.makeText(GalleryActivity.this, "Face detect need to set up, Please restart", Toast.LENGTH_SHORT).show();
                    return;
                }

                Frame frame=new Frame.Builder().setBitmap(mBitmap).build();
                SparseArray<Face> sparseArray=faceDetector.detect(frame);

                for (int i=0; i<sparseArray.size(); i++){
                    Face face=sparseArray.valueAt(i);
                    float x1=face.getPosition().x;
                    float y1=face.getPosition().y;
                    float x2=face.getWidth();
                    float y2=face.getHeight();
                    RectF rectF=new RectF(x1,y1,x2,y2);
                    canvas.drawRoundRect(rectF,2,2,boxPaint);
                }
                imgDetect.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));

            }
        });

    }
}