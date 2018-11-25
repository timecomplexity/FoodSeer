package com.blastbeatsandcode.seefood.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
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

import com.blastbeatsandcode.seefood.R;
import com.blastbeatsandcode.seefood.controller.SFController;
import com.blastbeatsandcode.seefood.controller.ServerConn;
import com.blastbeatsandcode.seefood.model.SFImage;
import com.blastbeatsandcode.seefood.utils.FileUtils;
import com.blastbeatsandcode.seefood.utils.Messages;
import com.blastbeatsandcode.seefood.utils.SFConstants;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

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

        // TODO: remove the below test case
        appropriateView(1, 1,seekbarMainResult,textMainResult );

        // TODO: Update this so it doesn't crash the app when the server isn't running
        try {
            ServerConn sc = new ServerConn();
            System.out.println("before");
            sc.retrieveFromDB();
            System.out.println("after");
        } catch (Exception e) {
            Messages.makeToast(getApplicationContext(), "Server is not running!");
        }
    }

    public void initialize(){ // a lot of this should probably be done by controller
        //TODO
        // set imageMainResult to first most recent image from db (or return now if theres none)
        // hide textNoneUploadedYet
        // based on main image, set seekbarMainResult and textMainResult
            // appropriateView(...);
        // get 10 or 15 latest images from db into an arraylist
        // populate the gallery with that arraylist

    }

    private void populateGallery(ArrayList<SFImage> gallery) { //FIXME: this required importing SFImage from model. is that okay?
        int count = 0;
        for (SFImage i: gallery){ //for each image in gallery array
            TableRow row = (TableRow)LayoutInflater.from(MainActivity.this).inflate(R.layout.attrib_row, null);

            // boiler plate for converting android image to bitmap
            ByteBuffer buffer = gallery.get(count).getImage().getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

            ((ImageView)row.findViewById(R.id.galleryImage)).setImageBitmap(bitmapImage);

            TextView t = ((TextView)row.findViewById(R.id.galleryText));
            t.setText("this shouldnt be visible");

            SeekBar s = ((SeekBar)row.findViewById(R.id.gallerySeekbar));
            s.setEnabled(false);

            tableGallery.addView(row);

            appropriateView(gallery.get(count).getFoodConfidence(), gallery.get(count).getNotFoodConfidence(),s,t );
            count++;
        }
        // TODO add a button to view more

    }

    // for now, foodness is a double out of 10, 0 being not food and 10 being food
    // this function sets the color of text, the content of text, and the seekbar percent
    private void appropriateView(float foodness, float notFoodness, SeekBar s, TextView t ){
        // useing just 3 colors for now
        float f = foodness - notFoodness; // f is positive for food, negative for not food
        float certainty = 1;
        if (f < -1 * certainty ){
            t.setTextColor(Color.RED);
            t.setText("Not Food");
        } else if (f >= -1 * certainty && f <= certainty){
            t.setTextColor(Color.YELLOW);
            t.setText("Hard to Say");
        } else if (f > certainty) {
            t.setTextColor(Color.GREEN);
            t.setText("Food!");
        } else {
            t.setText("Something went wrong...");
        }
        float percent = (((f/3)*50)+50);
        s.setProgress(Math.round(percent)); // progress can be between -50 and 50 to fit 100 units
        //s.setThumb()
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
        Intent intent = new Intent(this, AlbumSelectActivity.class);
        //set limit on number of images that can be selected, default is 10
        //intent.putExtra(Constants.INTENT_EXTRA_LIMIT, numberOfImagesToSelect);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Handle what to do after images have been selected from the gallery
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            for (Object image : images) {
                // Send each image to the AI
                String path = ((com.darsh.multipleimageselect.models.Image) image).path;
                File imageFile = new File(path);
                //Messages.makeToast(getApplicationContext(), "IMAGE FILE PATH: " + path);
                SFController.getInstance().addImageToUpload(imageFile);


                /* UNCOMMENT THIS TO TEST SENDING TO THE AI ////////////////////////////////

                String r = SFController.getInstance().sendImageToAI(path, "alex_test");
                Messages.makeToast(getApplicationContext(), r);

                */
            }

            Messages.makeToast(getApplicationContext(), "Number of images in the list: " + SFController.getInstance().getImagesToUpload().size());
        } else if (requestCode == SFConstants.TAKE_PICTURE_REQUEST_CODE && resultCode == RESULT_OK && data!= null) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = FileUtils.getImageUri(getApplicationContext(), image);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File imageFile = new File(FileUtils.getRealPathFromURI(getContentResolver(), tempUri));
            SFController.getInstance().addImageToUpload(imageFile);
            Messages.makeToast(getApplicationContext(), "Number of images in list: " + SFController.getInstance().getImagesToUpload().size());

            // Send the image to the AI with the absolute path
            String absPath = imageFile.getAbsolutePath();
            System.out.println("ABSOLUTE PATH: " + absPath);
            String r = SFController.getInstance().sendImageToAI(absPath, "alex_test");
            Messages.makeToast(getApplicationContext(), r);
            //String r = SFController.getInstance().sendImageToAI(imageFile.getAbsolutePath(), "alex_test");
            //Messages.makeToast(getApplicationContext(), r);
        }
    }

    @Override
    public void viewGallery() {

    }

    @Override
    public void takePicture() {
        // Start the activity for taking a picture
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, SFConstants.TAKE_PICTURE_REQUEST_CODE);
    }


    @Override
    public void update() {
        // TODO: UPDATE THE VIEW
    }
}
