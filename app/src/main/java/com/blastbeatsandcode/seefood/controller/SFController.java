package com.blastbeatsandcode.seefood.controller;


import android.os.AsyncTask;

import com.blastbeatsandcode.seefood.model.SFImage;
import com.blastbeatsandcode.seefood.utils.Messages;
import com.blastbeatsandcode.seefood.view.SFView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
    private ArrayList<SFImage> _imagesFromServer;

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
        _imagesFromServer = new ArrayList<SFImage>();
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
    public void getBatchImages() {
        // Get our batch images
        new Thread(new Runnable() {
            @Override
            public void run() {
            int[] targets = new int[10];
            // Get an array of targets to hit
            for (int i = 0; i < 10 && _lastItem >= 10; i++) {
                targets[i] = _lastItem--;
            }

            SFImage[] images = _conn.getSFImageBatch(targets);
            for (SFImage image : images) {
                if (image == null) break;
                addToImagesFromServer(image);
            }

            Update u = new Update();
            u.execute();
            }
        }).start();
    }

    private void addToImagesFromServer(SFImage image) {
        _imagesFromServer.add(image);
    }

    public void getSingleImage() {
        // Retrieve image data from the DB
        SFImage img = _conn.getSFImage(_lastItem);
        addToImagesFromServer(img);

        // Move last item back down one (consume the image)
        _lastItem--;
    }

    /*
     * Return the last image from the server
     */
    public SFImage getLastImage() {
        if (_imagesFromServer.size() <= 0)
        {
            getSingleImage();
        }

        // Last image should be last thing in the array list
        return _imagesFromServer.get(_imagesFromServer.size() - 1);
    }

    public ArrayList<SFImage> getCurrentImageSet() {
        return _imagesFromServer;
    }

    /*
     * Add image to the list to upload
     */
    public void addImageToUpload(File image) {
        // Add the image to the list and update the views
        _selectedImages.add(image);
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

        // Clear current images and update view
        _imagesFromServer.clear();
        update();

        return result;
    }

    public void registerView(SFView view) {
        _views.add(view);
        //update();
        new Update().execute();
    }

    /*
     * Update the views
     */
    public void update()
    {
        System.out.println("in update");
        if (_imagesFromServer.isEmpty()) {
            // TODO: Get as many images as necessary to populate views
            getSingleImage();
        }
        for(SFView v : _views)
        {
            v.update();
        }
    }

}

class Update extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }

    @Override
    protected void onPostExecute(Object l) {
        SFController.getInstance().update();
        System.out.println("we are here");
    }
}


