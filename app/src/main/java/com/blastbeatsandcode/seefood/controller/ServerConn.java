package com.blastbeatsandcode.seefood.controller;

import android.os.AsyncTask;
import android.os.Environment;

import com.blastbeatsandcode.seefood.model.SFImage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

/*
 * The ServerConn class handles the connection between the SeeFood app
 * And the cloud server AI
 */
public class ServerConn {
    // Connection information for access to cloud
    private String _response;             // Response from server
    private byte[] _request;              // Data to send with a request
    private String _connInfo;             // String containing connection info to server
    private CloseableHttpClient _client;  // Holds the actual connection object

    public ServerConn() {
        _client = HttpClients.createDefault();
    }

    public Queue<SFImage> getAllImages() {
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

    public String uploadImage(String imagePath, String sender) {
        HttpPost request = new HttpPost(
                "http://ec2-18-224-86-76.us-east-2.compute.amazonaws.com:5000/api/ai-decision");

        // Create an entity to send over POST
        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        entity.setCharset(Charset.defaultCharset());

        // Add our image, path relative to root
        File rootPath = Environment.getExternalStorageDirectory();
        entity.addBinaryBody("image", new File(rootPath + imagePath));

        // Add the name of the sender
        entity.addTextBody("sender", sender);

        // Set up the above entity to send
        request.setEntity(entity.build());

        try {
            // Send off to server
            CloseableHttpResponse response = _client.execute(request);

            // Give back the server response (confidence levels)
            return EntityUtils.toString(response.getEntity());
        } catch (IOException | ParseException e) {
            // If we're here, everything is broken
            return null;
        }
    }

    public String[] uploadImageBatch(String[] imagePaths, String sender) {
        // Hold on to our batch image responses
        String[] responses = new String[imagePaths.length];

        // Call our sendImage until we're out of images to send
        for (int i = 0; i < imagePaths.length; i++){
            responses[i] = uploadImage(imagePaths[i], sender);
        }

        // Return our responses
        return responses;
    }
}

// This needs to be brought out into its own async class to maintain a constant connection
class Connect extends AsyncTask {



    /**
     * Create the actual HTTP Client connection
     * @param objects
     * @return
     */
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            // Create the HTTP Client connection
            CloseableHttpClient hc = HttpClients.createDefault();
            HttpPost request = new HttpPost("http://ec2-18-224-86-76.us-east-2.compute.amazonaws.com:5000/api/ai-decision");

            // Return true if our connection is successful
            return true;
        } catch (Exception ex) {
            // False if connection fails
            return false;
        }
    }

}
