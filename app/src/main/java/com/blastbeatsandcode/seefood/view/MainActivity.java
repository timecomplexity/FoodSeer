package com.blastbeatsandcode.seefood.view;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

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
    private String androidId;
    private boolean imageJustUploaded;

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

        imageJustUploaded = false;

        // Get current device ID
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Hide the spinner to start with
        spinner.setVisibility(View.VISIBLE);

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
            } else {
                tableGallery2.addView(row);
                addToLeftTableNext =true;
            }


        } catch (Exception e) {
            Messages.makeToast(this, "Could not process image!");
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
        " images, just scroll down.\n\nYou will be seeing food in your future!";
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setCancelable(true); //allow user to close popup
        builder.setTitle("Welcome to FoodSeer!");
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
        startActivityForResult(intent, Constants.REQUEST_CODE);
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        // Reset the views when done
        imageJustUploaded = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Handle what to do after images have been selected from the gallery
                if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                    //The array list has the image paths of the selected images
                    ArrayList images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                    for (Object image : images) {
                        // Send each image to the AI
                        String path = ((com.darsh.multipleimageselect.models.Image) image).path;
                        SFController.getInstance().sendImageToAI(path, androidId);
                    }

                    // Reset the views when done
                    new NewImageUpdater().execute();
                } else if (requestCode == SFConstants.TAKE_PICTURE_REQUEST_CODE && resultCode == RESULT_OK && data!= null) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    Uri tempUri = FileUtils.getImageUri(getApplicationContext(), image);

                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    File imageFile = new File(FileUtils.getRealPathFromURI(getContentResolver(), tempUri));

                    // Send the image to the AI with the absolute path
                    String absPath = imageFile.getAbsolutePath();
                    SFController.getInstance().sendImageToAI(absPath, androidId);

                    // Reset the views when done
                    new NewImageUpdater().execute();
                } else {
                    new NewImageUpdater(true).execute();
                }
            }
        }).start();
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
        spinner.setVisibility(View.VISIBLE);
    }

    public static void hideSpinner() { spinner.setVisibility(View.GONE); }

    // this method is run on start and after clicking load more
    @Override
    public void update() {
        // Get our image set
        ArrayList<SFImage> currentImageSet = SFController.getInstance().getCurrentImageSet();
        if (currentImageSetSize > currentImageSet.size()) {
            positionFactor = 0;
            tableGallery.removeAllViews();
            tableGallery2.removeAllViews();
        }

        if (currentImageSetSize == currentImageSet.size() && !imageJustUploaded){
            Messages.makeToast(this, "Out of images!");
            spinner.setVisibility(View.GONE);
            return;
        }
        currentImageSetSize = currentImageSet.size();

        // Set the main image to the image at the end of the list
        if (currentImageSet.size() > 0)
            imageMainResult.setImageBitmap(currentImageSet.get(0).getImageBitmap());
        appropriateView(currentImageSet.get(0).getFoodConfidence(), currentImageSet.get(0).getNotFoodConfidence(), seekbarMainResult, textMainResult);


        // Populate the rest of the images
        for (int currentPos = 1 + positionFactor; currentPos < currentImageSet.size(); currentPos++) {
            populateGallery(currentImageSet.get(currentPos));
        }

        // Move past the first 10 items in list and keep up the spinner
        if (currentImageSet.size() != 1) {
            positionFactor += 10;
            spinner.setVisibility(View.GONE);
        }

        // Reset our flag for recent image upload
        imageJustUploaded = false;
    }
}

class NewImageUpdater extends AsyncTask {
    private boolean hideSpinner;

    NewImageUpdater() {}
    NewImageUpdater(boolean hideSpinner) { this.hideSpinner = hideSpinner; }

    @Override
    protected Object doInBackground(Object[] objects) { return null; }

    @Override
    protected void onPostExecute(Object l) {
        // This bit is kind of stupid, but I don't know a better way to hide it
        if (hideSpinner) {
            MainActivity.hideSpinner();
            return;
        }
        SFController.getInstance().clearAndUpdate();
    }
}
