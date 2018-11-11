package com.blastbeatsandcode.seefood.controller;

import com.blastbeatsandcode.seefood.model.SFImage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*
 * The ServerConn class handles the connection between the SeeFood app
 * And the cloud server AI
 */
public class ServerConn {
    // Connection information for access to cloud
    private String _response;       // Response from server
    private byte[] _request;        // Data to send with a request
    private String _connInfo;       // String containing connection info to server

    public Queue<SFImage> getAllimages() {
        // TODO: Implement this by grabbing all of the images from the server
        Queue<SFImage> images = new LinkedList<SFImage>();

        return images;
    }

    /*
     * Send a request from passed in string
     * // TODO: Should we remove this as it is a security problem?
     */
    public String sendRequest(byte[] data) {
        // TODO: Implement this by sending a request to the server
        return "Sent request";
    }

    /*
     * Send the saved request
     */
    public String sendRequest() {
        // TODO: Implement this by ssending a request, but this time make sure it
        // is the request set in _request, it's more safe
        return "Sent member variable value of request";
    }

    /*
     * Set the connection request
     */
    public void setRequest(byte[] data) {
        _request = data;
    }

    /*
     * Connect to the server
     */
    private boolean connectToServer() {
        // TODO: Implement this, return whether we are connected or not
        return false;
    }
}
