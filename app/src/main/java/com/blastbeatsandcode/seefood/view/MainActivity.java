package com.blastbeatsandcode.seefood.view;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.blastbeatsandcode.seefood.R;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    // view elements in order of position top to bottom
    private static ImageButton buttonHelp;
    private static ImageButton buttonCamera;
    private static ImageButton buttonUpload;
    private static View imageMainResult;
    private static TextView textNoneUploadedYet;
    private static SeekBar seekbarMainResult;
    private static TextView textMainResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign all view elements

        buttonHelp = (ImageButton)findViewById(R.id.buttonHelp);
        buttonCamera = (ImageButton)findViewById(R.id.buttonCamera);
        buttonUpload = (ImageButton)findViewById(R.id.buttonUpload);
        imageMainResult = (View)findViewById(R.id.imageMainResult);
        textNoneUploadedYet = (TextView)findViewById(R.id.textNoneUploadedYet);
        seekbarMainResult = (SeekBar)findViewById(R.id.seekbarMainResult);
        textMainResult = (TextView)findViewById(R.id.textMainResult);

        // start all listeners

        helpListener();
        cameraListener();
        uploadListener();

        // initialize

        initialize();

    }

    public void initialize(){
        //TODO
        // set imageMainResult to first most recent image from db (or return now if theres none)
        // hide textNoneUploadedYet
        // based on main image, set seekbarMainResult and textMainResult
        // populate the gallery below
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
