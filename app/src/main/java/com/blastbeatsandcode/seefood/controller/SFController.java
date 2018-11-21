package com.blastbeatsandcode.seefood.controller;


import android.widget.Toast;

import com.blastbeatsandcode.seefood.model.SFImage;
import com.blastbeatsandcode.seefood.utils.Messages;
import com.blastbeatsandcode.seefood.view.SFView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Queue;

/*
 * SFController is the controller for the SeeFood app.
 * Handles communicating between the views and the SF Model.
 * */
public class SFController {

    // TODO: Add a collection of views? Do we need this?
    // TODO: Do we need a reference to a specific model object in this secenario?

    // Hold a collection of views so they may be referenced
    private ArrayList<SFView> _views;

    // Hold a collection of views so they may be referenced
    private ArrayList<File> _selectedImages;

    // Collection of the images from the server
    private Queue<SFImage> _imagesFromServer;

    // Connection to the server object
    private ServerConn _conn = new ServerConn();

    // Instance of the SFController
    private static SFController instance = null;

    // Hold the last item currently in the DB
    private int _lastItem = -1;

    // TODO: Do we need this???
    // Hold a reference to the SFModel
    //private SFModel _m;

    /*
     * Private constructor to create the instance if it does not already exist
     * Instead, we register the model outside of the constructor
     * For proper initialization
     * */
    private SFController()
    {
        _views = new ArrayList<SFView>();
        _selectedImages = new ArrayList<File>();
        _lastItem = _conn.retrieveLastDBItemId();
    }

    // Only create a new instance of the SF controller if it does not already exist.
    public static SFController getInstance()
    {
        if (instance == null)
        {
            instance = new SFController();
        }
        return instance;
    }

    // Return the images
    public ArrayList<SFImage> getImages() {
        // There's an off-by-one error or something here, so we have this
        int currentTarget = _lastItem + 1;
        ArrayList<SFImage> result = new ArrayList<>();
        for (int i = currentTarget; i > _lastItem - 9 && currentTarget >= 10; i--) {
            // Consume one item from our current target list
            currentTarget--;
            // Retrieve image data from the DB
            result.add(_conn.getSFImage(currentTarget));
        }

        return result;
    }

    /*
     * Add image to the list to upload
     */
    public void addImageToUpload(File image) {
        // Add the image to the list and update the views
        _selectedImages.add(image);
        update();
    }

    /*
     * Retrieve images in list to be uploaded
     */
    public ArrayList<File> getImagesToUpload() {
        return _selectedImages;
    }


    /**
     * Send image to AI for processing
     * @param imagePath Path to image file
     * @param sender Sender name
     * @return Result of AI processing
     */
    public String sendImageToAI(String imagePath, String sender) {
        // Remove the extra bit from the URI
        if (imagePath.contains("/storage/emulated/0"))
        {
            imagePath = imagePath.replace("/storage/emulated/0", "");
        }

        // Get the result of the AI processing
        String result = _conn.uploadImage(imagePath, sender);
        try {
            // Get image data
            String[] splitResult = result.split(",");
            String fileName = splitResult[0];
            String foodConf = splitResult[1];
            String notFoodConf = splitResult[2];
            String[] pathData = imagePath.split("\\.");
            String imageType = pathData[pathData.length - 1];

            // Save data to DB for later retrieval
            _conn.uploadToDB(fileName, foodConf, notFoodConf, imageType, sender);
        } catch (RuntimeException e) {
            System.out.println("Response not received...");
        }

        // Update our current last DB item
        _lastItem = _conn.retrieveLastDBItemId();

        return result;
    }

    public void createImage(String path, String fileType) {
        _conn.getImageBytes(path, fileType);
    }
//
//    public SFImage getImageFromServer() {
//        return new SFImage(new Image());
//    }


    /*
     * Add the model to the controller
     * */
    /* TODO: Do we need this???
    public void registerModel(SFModel model)
    {
        _m = model;
    } */

    /*
     * Add view to views collection
     */
    /* TODO: Do we need this?
    public void registerView(SFView v)
    {
        _views.add(v);
    }
    */

    /*
     * Update the views
     */
    public void update()
    {
        for(SFView v : _views)
        {
            v.update();
        }
    }
}
