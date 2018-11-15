package com.blastbeatsandcode.seefood.view;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.blastbeatsandcode.seefood.R;
import com.blastbeatsandcode.seefood.controller.SFController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SFView {

    public static final int REQUEST_CODE_FOR_IMAGE_SELECTION = 0;
    public static final int REQUEST_CODE_FOR_CAMERA = 1;
    // view elements in order of position top to bottom
    private static ImageButton buttonHelp;
    private static ImageButton buttonCamera;
    private static ImageButton buttonUpload;
    private static ImageView imageMainResult;
    private static TextView textMainImageCoverup;
    private static SeekBar seekbarMainResult;
    private static TextView textMainResult;
    private static TableLayout tableGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign all view elements

        buttonHelp = (ImageButton)findViewById(R.id.buttonHelp);
        buttonCamera = (ImageButton)findViewById(R.id.buttonCamera);
        buttonUpload = (ImageButton)findViewById(R.id.buttonUpload);
        imageMainResult = (ImageView)findViewById(R.id.imageMainResult);
        textMainImageCoverup = (TextView)findViewById(R.id.textMainImageCoverup);
        seekbarMainResult = (SeekBar)findViewById(R.id.seekbarMainResult);
        seekbarMainResult.setEnabled(false); // make the seekbar frozen
        textMainResult = (TextView)findViewById(R.id.textMainResult);
        tableGallery = (TableLayout)findViewById(R.id.tableGallery);

        // start all listeners

        helpListener();
        cameraListener();
        uploadListener();

        // initialize

        initialize();
        //Image[] gallery = new Image[10];
        //TODO have this ^ come from somehwere else and be filled with
        // the latest x (10) images excluding the most recent one
        // also these might want to be an seeFoodImage objects which have data about foodness rather than just
        // images so populating the gallery is easier :)
        //populateGallery(gallery);
        appropriateView(5,seekbarMainResult,textMainResult ); //TODO remove later

        //TODO: remove this
        /////////////////////////////////////////
        // TEST CODE -- REMOVE FROM PRODUCTION //
        /////////////////////////////////////////
        //SFController c = SFController.getInstance();
        //String r = c.sendImageToAI("/DCIM/Drawings/09082018161409.png", "adam_test");
        //Toast toast = Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT);
        //toast.show();
        // END TEST CODE //

    }

    public void initialize(){ // a lot of this should probably be done by controller
        //TODO
        // set imageMainResult to first most recent image from db (or return now if theres none)
            // imageMainResult.setImageResource(R.drawable.my_image); // have first image passed here
        // hide textNoneUploadedYet
        // based on main image, set seekbarMainResult and textMainResult
            // appropriateView(get this somehow,seekbarMainResult,textMainResult );
        // populate the gallery
    }

    private void populateGallery(ArrayList<Bitmap> gallery) {
        int count = 0;
        for (Bitmap i: gallery){ //for each image in gallery array
            TableRow row = (TableRow)LayoutInflater.from(MainActivity.this).inflate(R.layout.attrib_row, null);
            ((ImageView)row.findViewById(R.id.galleryImage)).setImageBitmap(gallery.get(count));
            ((TextView)row.findViewById(R.id.galleryText)).setText("test");
            ((SeekBar)row.findViewById(R.id.gallerySeekbar)).setEnabled(false);
            tableGallery.addView(row);
            // TODO populate more based on object's attributes
            count++;
        }
        // TODO add a button to view more

    }

    // for now, foodness is a double out of 10, 0 being not food and 10 being food
    // this function sets the color of text, the content of text, and the seekbar percent
    private void appropriateView(double foodness, SeekBar s, TextView t ){
        // useing just 3 colors for now
        if (foodness < 3.5){
            t.setTextColor(Color.RED);
            t.setText("Not Food");
            s.setProgress((int)foodness*10);
        } else if (foodness>= 3.5 && foodness < 7.5){
            t.setTextColor(Color.YELLOW);
            t.setText("Hard to Say");
            s.setProgress((int)foodness*10);
        } else {
            t.setTextColor(Color.GREEN);
            t.setText("Food!");
            s.setProgress((int)foodness*10);
        }
    }

    public void helpListener(){
        buttonHelp.setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View v) {
                    // Call the Display Help method
                    displayHelp();
                }
            }
        );
    }

    public void cameraListener(){
        buttonCamera.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        // Call the Take Picture Method
                        takePicture();
                    }
                }
        );
    }

    public void uploadListener(){
        buttonUpload.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        // Call the Upload Image method
                        uploadImage();
                    }
                }
        );
    }

    @Override
    public void displayHelp() {
        String helpText = "This app is quite simple. To start, first tap either"+
        " the camera or the upload button.\nNext, take a picture or select a picture to upload. Your"+
        " image will be processed by an AI and tested for how likely it is to be food! When the processing"+
        " is finished, your latest image will appear and show how \"food\" it is! To see previously uploaded "+
        " images, just scroll down.\n\nHappy SeeFooding!";
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true); //allow user to close popup
        builder.setTitle("Welcome to SeeFood!");
        builder.setMessage(helpText);
        builder.setNegativeButton("Great!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void uploadImage() {
        // invoke image gallery with implicit intent
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        // specify where to find image
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirectoryPath);
        photoPickerIntent.setDataAndType(data, "image/*"); // accepts all image types
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // allow selection of multiple items
        startActivityForResult(photoPickerIntent, REQUEST_CODE_FOR_IMAGE_SELECTION);
    }

    @Override
    public void viewGallery() {

    }

    @Override
    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE_FOR_CAMERA);
    }

    @Override //called after takePicture() method and after uploadImage()
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            ArrayList<Bitmap> images = new ArrayList<Bitmap>();
            if (requestCode == REQUEST_CODE_FOR_IMAGE_SELECTION){ // user is trying to upload image


//                Uri imageUri = data.getData(); // the address of the image
//                InputStream inputStream; //declare stream to read image data
//                try { // exception could be that image is missing after selection
//                    inputStream = getContentResolver().openInputStream(imageUri);
//                    images.add(BitmapFactory.decodeStream(inputStream));
//                    processImages(images);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "Unable to open image from internal storage", Toast.LENGTH_LONG).show();


                int count = data.getClipData().getItemCount();
                Uri imageUri = data.getData();
                InputStream inputStream; //declare stream to read image data
                for(int i = 0; i < count; i++) {
                    imageUri = data.getClipData().getItemAt(i).getUri();
                    try { // exception could be that image is missing after selection
                        inputStream = getContentResolver().openInputStream(imageUri);
                        images.add(BitmapFactory.decodeStream(inputStream));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unable to open image from internal storage", Toast.LENGTH_LONG).show();
                    }
                }
                processImages(images);
            } else if (requestCode == REQUEST_CODE_FOR_CAMERA){
                try {
                    images.add((Bitmap)data.getExtras().get("data"));
                    processImages(images);
                } catch (Exception e) { // bad code
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to process photo that was taken", Toast.LENGTH_LONG).show();
                }
            }
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    public void processImages(ArrayList<Bitmap> images){
        textMainImageCoverup.setText("Processing...");
        imageMainResult.setImageBitmap(images.get(images.size()-1)); //set last image as main image
        //TODO: replace above line to send pictures to db
        textMainImageCoverup.setText(""); // get rid of status message when picture is finished processing
        textMainImageCoverup.setText(Integer.toString(images.size()));
        populateGallery(images);
    }

    @Override
    public void update() {

    }
}
