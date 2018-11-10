package com.blastbeatsandcode.seefood.view;

import android.graphics.Color;
import android.media.Image;
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

public class MainActivity extends AppCompatActivity {

    // view elements in order of position top to bottom
    private static ImageButton buttonHelp;
    private static ImageButton buttonCamera;
    private static ImageButton buttonUpload;
    private static ImageView imageMainResult;
    private static TextView textNoneUploadedYet;
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
        textNoneUploadedYet = (TextView)findViewById(R.id.textNoneUploadedYet);
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
        Image[] gallery = new Image[10];
        //TODO have this ^ come from somehwere else and be filled with
        // the latest x (10) images excluding the most recent one
        // also these might want to be an seeFoodImage objects which have data about foodness rather than just
        // images so populating the gallery is easier :)
        populateGallery(gallery);
        appropriateView(5,seekbarMainResult,textMainResult ); //TODO remove later

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

    private void populateGallery(Image[] gallery) {
        for (Image i: gallery){ //for each image in gallery array
            TableRow row = (TableRow)LayoutInflater.from(MainActivity.this).inflate(R.layout.attrib_row, null);
            ((ImageView)row.findViewById(R.id.galleryImage)).setImageResource(R.drawable.defaultimage);
            ((TextView)row.findViewById(R.id.galleryText)).setText("test");
            ((SeekBar)row.findViewById(R.id.gallerySeekbar)).setEnabled(false);
            tableGallery.addView(row);
            // TODO populate more based on object's attributes
        }

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
                    //TODO
                }
            }
        );
    }

    public void cameraListener(){
        buttonCamera.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        //TODO
                    }
                }
        );
    }

    public void uploadListener(){
        buttonUpload.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        //TODO
                    }
                }
        );
    }


}
