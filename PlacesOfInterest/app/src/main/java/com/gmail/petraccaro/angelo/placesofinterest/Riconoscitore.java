package com.gmail.petraccaro.angelo.placesofinterest;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

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
import java.util.List;

public class Riconoscitore extends IntentService {
    private ArrayList<String> paths;
    private ArrayList<String> Filteredpaths;
    private  int imageSizeX;
    private  int imageSizeY;
    /** face detector**/
    private FaceDetector faceDetector;
    private static final float IMAGE_MEAN = 128.0f;
    private static final float IMAGE_STD = 128.0f;
    public static Bitmap cropped;

    float[][]  ori_embedding = new float[1][192];
    float[][] test_embedding = new float[1][192];
    public static final String FILTER_ACTION_KEY = "Riconoscitore";
    protected Interpreter tflite;
    int i = 0;


    // MobileFaceNet
    private static final int TF_OD_API_INPUT_SIZE = 112;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public Riconoscitore() {
        super("Riconoscitore");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        try {
            tflite=new Interpreter(loadmodelfile(Riconoscitore.this));


            paths = intent.getStringArrayListExtra("paths");
            Filteredpaths = new ArrayList<>();
            // in prima posizione metto la uri della foto del profilo
            Bitmap bitmap = BitmapFactory.decodeFile((paths.get(0)));
            face_detector(paths.get(0),bitmap,"original");
            Thread.sleep(1000);
            paths.remove(paths.get(0));
            for(String path : paths){

                Bitmap bitmap2 = BitmapFactory.decodeFile(path);
                face_detector(path,bitmap2,"test");

                Thread.sleep(500);
            }







        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
       // Log.e("ricevo dalla conversione",String.valueOf(bitmaps.size()));
    }

    /** carimento del modello
     * @param activity**/
    private MappedByteBuffer loadmodelfile(Riconoscitore activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("mobile_face_net.tflite");
        // Log.e("FileDesc",fileDescriptor.toString());
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }
    /** calcolo della distanza tra i il volto presente nella foto del profilo e il volto individuatoo in una foto postata**/
    private  float calculate_distance(float[][] ori_embedding, float[][] test_embedding) {
        float sum = (float) 0.0;
        for(int i=0;i<192;i++){
            float diff = ori_embedding[0][i]-test_embedding[0][i];
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
     */
    public  void face_detector(final String path, final Bitmap bitmap, final String imagetype){

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

                                Log.e("sono il face detector","eccolo");
                                for (Face face : faces) {
                                    Rect bounds = faces.get(0).getBoundingBox();
                                    cropped = Bitmap.createBitmap(bitmap, bounds.left, bounds.top, bounds.width(), bounds.height());
                                    get_embaddings(cropped,imagetype);

                                    if(imagetype.equalsIgnoreCase("test")){
                                        float distance = calculate_distance(ori_embedding,test_embedding);

                                        Log.e("distance",String.valueOf(distance));
                                        if(distance<1.01){
                                            /** volto riconosciuto**/
                                            Filteredpaths.add(path);
                                            //BitmpasVolti.put(bitmap,distance);
                                           // distanza.add(distance+"");
                                            //bitmapArray.add(bitmap);

                                            //Detector.CustomAdapter1 customAdapter=new Detector.CustomAdapter1(bitmapArray,distanza,getApplicationContext());
                                            //gridView.setAdapter(customAdapter);

                                            Log.e("distace ok","distace ok, volti riconosciuti=" + "ciao");
                                        }
                                        else
                                            Log.e("distace not ok","distace not ok");
                                    }

                                    sendCashbackInfoToClient();

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
    public  void get_embaddings(Bitmap bitmap,String imagetype){

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

    private void sendCashbackInfoToClient(){
        if(i++ == paths.size()){
            Intent intent = new Intent();
            intent.setAction(FILTER_ACTION_KEY);

            intent.putStringArrayListExtra("arrayFilesFiltered",Filteredpaths);
            sendBroadcast(intent);
        }

    }
}
