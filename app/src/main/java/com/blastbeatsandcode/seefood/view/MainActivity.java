package com.blastbeatsandcode.seefood.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.blastbeatsandcode.seefood.R;
import com.blastbeatsandcode.seefood.controller.SFController;
import com.blastbeatsandcode.seefood.model.SFImage;
import com.blastbeatsandcode.seefood.utils.FileUtils;
import com.blastbeatsandcode.seefood.utils.Messages;
import com.blastbeatsandcode.seefood.utils.SFConstants;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;

import java.io.File;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SFView {

    // view elements in order of position top to bottom
    private static ImageButton buttonHelp;
    private static ImageButton buttonCamera;
    private static ImageButton buttonUpload;
    private static ImageView imageMainResult;
    private static SeekBar seekbarMainResult;
    private static TextView textMainResult;
    private static TableLayout tableGallery;
    private static TableLayout tableGallery2;
    private static Button buttonLoadMore;
    private static ProgressBar spinner;

    // To track which images we've loaded into the app...
    private int positionFactor = 0;
    private boolean addToLeftTableNext = true;
    private int currentImageSetSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign all view elements

        buttonHelp = (ImageButton)findViewById(R.id.buttonHelp);
        buttonCamera = (ImageButton)findViewById(R.id.buttonCamera);
        buttonUpload = (ImageButton)findViewById(R.id.buttonUpload);
        imageMainResult = (ImageView)findViewById(R.id.imageMainResult);
        seekbarMainResult = (SeekBar)findViewById(R.id.seekbarMainResult);
        seekbarMainResult.setEnabled(false); // make the seekbar frozen
        textMainResult = (TextView)findViewById(R.id.textMainResult);
        tableGallery = (TableLayout)findViewById(R.id.tableGallery);
        tableGallery2 = (TableLayout)findViewById(R.id.tableGallery2);
        buttonLoadMore = (Button)findViewById(R.id.buttonLoadMore);
        spinner = (ProgressBar)findViewById(R.id.progressBar);

        // Hide the spinner to start with
        spinner.setVisibility(View.GONE);

        // start all listeners

        helpListener();
        cameraListener();
        uploadListener();
        loadMoreListener();

        // Register with the view
        SFController.getInstance().registerView(this);
    }

    /*
     * Puts an image into the gallery
     */
    private void populateGallery(SFImage image) {
        TableRow row = (TableRow)LayoutInflater.from(MainActivity.this).inflate(R.layout.attrib_row, null);

        try {
            // Add image from DB

            try{
                ((ImageView) row.findViewById(R.id.galleryImage)).setImageBitmap(image.getImageBitmap());

                // RESIZE IMAGE HERE. Change this if it doesn't play nicely with other resolutions
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(500, 500);
                layoutParams.gravity = Gravity.CENTER;
                ((ImageView) row.findViewById(R.id.galleryImage)).setLayoutParams(layoutParams);
            } catch (Exception e) { //FIXME: it hits this a lot!!! WHYYYYYYYY!!!!!!!!!!!!???????????????
                System.out.println("IMAGE WAS NULL!!!!!!!!");
                System.out.println(e);
            }

            // Add in food/not food graphics
            TextView t = row.findViewById(R.id.galleryText);
            t.setText("this shouldnt be visible");

            SeekBar s = row.findViewById(R.id.gallerySeekbar);
            s.setEnabled(false);

            appropriateView(image.getFoodConfidence(), image.getNotFoodConfidence(), s, t);

            if (addToLeftTableNext){
                tableGallery.addView(row);
                addToLeftTableNext =false;
                System.out.println("added to left. do it again?"  + addToLeftTableNext);
            } else {
                tableGallery2.addView(row);
                addToLeftTableNext =true;
                System.out.println("added to right. add lefft next? " + addToLeftTableNext);
            }


        } catch (Exception e) {
            System.out.println("Could not process image!");
            System.out.println(e);
        }
    }

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
        System.out.println("percent" + percent);
        s.setProgress(Math.round(percent)); // progress can be between -50 and 50 to fit 100 units
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

    public void loadMoreListener(){
        buttonLoadMore.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        // Call the Upload Image method
                        loadMore();
                    }
                }
        );
    }

    public void loadMore(){
        spinner.setVisibility(View.VISIBLE);
        SFController.getInstance().getBatchImages();
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

                String r = SFController.getInstance().sendImageToAI(path, "alex_test");
                Messages.makeToast(getApplicationContext(), r);
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
        }
    }

    @Override
    public void viewGallery() {
        // i dont think this function has a use
    }

    @Override
    public void takePicture() {
        // Start the activity for taking a picture
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, SFConstants.TAKE_PICTURE_REQUEST_CODE);
    }


    // this method is run on start and after clicking load more
    @Override
    public void update() {
        spinner.setVisibility(View.VISIBLE);
        // Get our image set
        ArrayList<SFImage> currentImageSet = SFController.getInstance().getCurrentImageSet();
        if (currentImageSetSize > currentImageSet.size()) {
            positionFactor = 1;
            tableGallery.removeAllViews();
        }

        if (currentImageSetSize == currentImageSet.size()){
            Messages.makeToast(this, "Out of images!");
            return;
        }
        currentImageSetSize = currentImageSet.size();

        // Set the main image to the image at the end of the list
        if (currentImageSet.size() > 0)
            imageMainResult.setImageBitmap(currentImageSet.get(0).getImageBitmap());
            appropriateView(currentImageSet.get(0).getFoodConfidence(), currentImageSet.get(0).getNotFoodConfidence(), seekbarMainResult, textMainResult);


        // Populate the rest of the images
        System.out.println(positionFactor);
        for (int currentPos = 1 + positionFactor; currentPos < currentImageSet.size(); currentPos++) {
            populateGallery(currentImageSet.get(currentPos));

        }

        // Move past the first 10 items in list
        if (currentImageSet.size() != 1)
            positionFactor += 10;

        spinner.setVisibility(View.GONE);
    }
}


