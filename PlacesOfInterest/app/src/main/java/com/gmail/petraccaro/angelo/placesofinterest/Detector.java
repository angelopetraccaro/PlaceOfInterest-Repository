package com.gmail.petraccaro.angelo.placesofinterest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Detector extends AppCompatActivity {
    public ArrayList<String> distanza=new ArrayList<String>();
    ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
    protected Interpreter tflite;
    private  int imageSizeX;
    private  int imageSizeY;
    /** face detector**/
    private FaceDetector faceDetector;
    private static final float IMAGE_MEAN = 128.0f;
    private static final float IMAGE_STD = 128.0f;

    // MobileFaceNet
    private static final int TF_OD_API_INPUT_SIZE = 112;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    public Bitmap oribitmap,testbitmap;
    public static Bitmap cropped;
    Uri imageuri;
    ImageView oriImage,testImage;
    Button buverify;
    TextView result_text;
    private Button btnDetec;
    private ImageView ImgViewFotoProfilo;
    private final ArrayList<String> arrayofuri = new ArrayList<>();

    float[][]  ori_embedding = new float[1][192];
    float[][] test_embedding = new float[1][192];
    ArrayList<Bitmap> arrayBitmap = new ArrayList<>();
    /** contiene i bitmaps per ogni volto riconosciuto con la rispettiva distanza **/
    Map<Bitmap,Float> BitmpasVolti = new HashMap<>();

    //....
    GridView gridView;
    String [] distance={"distance 1","distance 2","distance 3","distance 4"};
    int[] imagesGrid= {R.drawable.angelo,R.drawable.angelo,R.drawable.angelo,R.drawable.angelo};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        gridView=findViewById(R.id.grid);
        ImgViewFotoProfilo = findViewById(R.id.imgFace);

        Intent i = getIntent();
        final String[] uriProfilo = {i.getStringExtra("uri")};
        Log.e("uriProfilo", uriProfilo[0]);
        Picasso.get().load(uriProfilo[0]).into(ImgViewFotoProfilo);
        final Bitmap BitMapFotoDelProfilo = ((BitmapDrawable)ImgViewFotoProfilo.getDrawable()).getBitmap();
        try {
            tflite=new Interpreter(loadModelFile(this));

        } catch (IOException e) {
            e.printStackTrace();
        }
                DatabaseReference myRef;
                FirebaseDatabase db  = FirebaseDatabase.getInstance();
                myRef  = db.getReference("photos");

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayofuri.clear();

                        for(DataSnapshot ds: snapshot.getChildren()){
                            final ElementoLista els = ds.getValue(ElementoLista.class);
                            arrayofuri.add(els.getUrl_foto());
                         }
                        arrayBitmap.clear();
                        for(String key: arrayofuri)
                            Picasso.get().load(key).into( new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                  //  Log.e("sto convertendo","converto");
                                    arrayBitmap.add(bitmap);
                                    if(arrayBitmap.size()>=arrayofuri.size()) {
                                        faceDetector(BitMapFotoDelProfilo,"original", uriProfilo[0]);
                                        for(int i = 0; i< arrayBitmap.size(); i++)
                                            faceDetector(arrayBitmap.get(i),"test",arrayofuri.get(i));
                                    }
                                }
                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }
                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    /** calcolo della distanza tra i il volto presente nella foto del profilo e il volto individuatoo in una foto postata**/
    private  float calculateDistance(float[][] ori_embedding, float[][] test_embedding) {
        float sum = (float) 0.0;
        float diff = (float) 0.0;
        for(int i=0;i<192;i++){
            diff = ori_embedding[0][i]-test_embedding[0][i];
            sum += diff*diff;
        }
        return (float)Math.sqrt(sum);
    }
    /** preprocessing sulle immagini**/
    private  TensorImage loadImage(final Bitmap bitmap, TensorImage inputImageBuffer ) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }
    /** carimento del modello **/
    private  MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("mobile_face_net.tflite");
       // Log.e("FileDesc",fileDescriptor.toString());
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    /**
     * @return paramentri di normalizzazione
     */
    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }

    /**
     * Riconosce i volti presenti in una foto.
     * @param bitmap bitmap che contiene il volto da riconoscere
     * @param imagetype tipo dell'immagine( usato per differenziare l'immagine del profilo da quelle di test)
     * @param uri uri dell'immagine che il face detector processa( usata per le verifche, dopo la possiamo anche eliminare)
     */
    public  void faceDetector(final Bitmap bitmap, final String imagetype, final String uri){

        final InputImage image = InputImage.fromBitmap(bitmap,0);

        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                        .build();


        FaceDetector detector = FaceDetection.getClient(options);

        detector.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<Face>>() {
                            @Override
                            public void onSuccess(List<Face> faces) {

                                Log.e("sono il face detector",uri);
                                for (Face face : faces) {
                                    Rect bounds = faces.get(0).getBoundingBox();
                                    cropped = Bitmap.createBitmap(bitmap, bounds.left, bounds.top, bounds.width(), bounds.height());
                                    getEmbaddings(cropped,imagetype);

                                    if(imagetype.equalsIgnoreCase("test")){
                                        float distance = calculateDistance(ori_embedding,test_embedding);

                                        Log.e("distance",String.valueOf(distance));
                                        if(distance<1.01){
                                            /** volto riconosciuto**/
                                            //BitmpasVolti.put(bitmap,distance);
                                            distanza.add(distance+"");
                                            bitmapArray.add(bitmap);

                                            CustomAdapter1 customAdapter=new CustomAdapter1(bitmapArray,distanza,getApplicationContext());
                                            gridView.setAdapter(customAdapter);

                                            Log.e("distace ok","distace ok, volti riconosciuti=" + BitmpasVolti.size());
                                        }
                                        else
                                            Log.e("distace not ok","distace not ok");
                                    }
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
    }

    /**
     * Estrae per il volto passato i suoi parametri caratteristici
     * @param bitmap bitmap del volto riconosciuto
     * @param imagetype tipo dell'immagine( usato per differenziare l'immagine del profilo da quelle di test)
     */
    public  void getEmbaddings(Bitmap bitmap, String imagetype){

        TensorImage inputImageBuffer;
        float[][] embedding = new float[1][192];

        int imageTensorIndex = 0;
        int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];
        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

        inputImageBuffer = new TensorImage(imageDataType);

        inputImageBuffer = loadImage(bitmap,inputImageBuffer);

        tflite.run(inputImageBuffer.getBuffer(),embedding);

        if(imagetype.equals("original")){
            Log.e("img type", imagetype);

            ori_embedding=embedding;

        }
        else if (imagetype.equals("test")){
            Log.e("entro per test", imagetype);

            test_embedding=embedding;
        }

    }


    public class CustomAdapter1 extends BaseAdapter {

        private String[] imagesName;
        private int[] imagesPhoto;
        private Context context;
        private LayoutInflater layoutInflater;

        private ArrayList<Bitmap> bitmap;
        private ArrayList<String> distanza;

        public CustomAdapter1(String[] imagesName, int[] imagesPhoto, Context context) {
            this.imagesName = imagesName;
            this.imagesPhoto = imagesPhoto;
            this.context = context;
            this.layoutInflater =  (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        public CustomAdapter1(ArrayList<Bitmap>  bitmap, ArrayList<String> distanza, Context context) {
            this.bitmap = bitmap;
            this.distanza=distanza;
            this.context = context;
            this.layoutInflater =  (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            //return imagesPhoto.length;
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

            textGrid.setText(distanza.get(position));
            imageGrid.setImageBitmap(bitmap.get(position));

            return view;
        }
    }
}