package com.blastbeatsandcode.seefood.controller;


import com.blastbeatsandcode.seefood.model.SFImage;
import com.blastbeatsandcode.seefood.view.SFView;

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

    // Collection of the images from the server
    private Queue<SFImage> _images;

    // Connection to the server object
    private ServerConn _conn;

    // Instance of the SFController
    private static SFController instance = null;

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
    public Queue<SFImage> getImages() {
        // TODO: Update images
        _images = _conn.getAllimages();

        return _images;
    }


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
