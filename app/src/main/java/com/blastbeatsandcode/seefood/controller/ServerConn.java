package com.blastbeatsandcode.seefood.controller;

import android.os.AsyncTask;
import android.os.Environment;

import com.blastbeatsandcode.seefood.model.SFImage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

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

    /**
     * Upload single image for AI decision
     * @param imagePath Path to the image, relative to root
     * @param sender Sender name
     * @return JSON String of conf/non-conf values
     */
    public String uploadImage(String imagePath, String sender) {
        Sender s = new Sender(imagePath, sender);
        s.execute();
        try {
            s.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return s.result;
    }

    /**
     * Batch upload images to the AI
     * @param imagePaths The paths of each individual image
     * @param sender The name of the sender (the same for each image)
     * @return String array of each AI decision on each image
     */
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

    public void uploadToDB(String fileName, String foodConf, String notFoodConf, String imageType,
                           String sender) {
        DBSender s = new DBSender(fileName, foodConf, notFoodConf, imageType, sender);
        s.execute();
    }

    public void retrieveFromDB(SFImage sfi) {
        //pass
    }

}

class Sender extends AsyncTask {
    private final String filePath;
    private final String sender;
    public String result;

    Sender(String filePath, String sender) {
        this.filePath = filePath;
        this.sender = sender;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        HttpPost request = new HttpPost(
                "http://ec2-18-224-86-76.us-east-2.compute.amazonaws.com:5000/api/ai-decision");

        // Create an entity to send over POST
        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        entity.setCharset(Charset.defaultCharset());

        // Add our image, path relative to root
        File rootPath = Environment.getExternalStorageDirectory();
        entity.addBinaryBody("image", new File(rootPath + filePath));

        // Add the name of the sender
        entity.addTextBody("sender", sender);

        // Set up the above entity to send
        request.setEntity(entity.build());

        try {
            // Send off to server
            CloseableHttpResponse response = HttpClients.createDefault().execute(request);

            // Give back the server response (confidence levels)
            result =  EntityUtils.toString(response.getEntity());
            return null;
        } catch (IOException | ParseException e) {
            // If we're here, everything is broken
            return null;
        }
    }
}

class DBSender extends AsyncTask {

    private String fileName;
    private String foodConf;
    private String notFoodConf;
    private String imageType;
    private String sender;

    DBSender (String fileName, String foodConf, String notFoodConf, String imageType, String sender) {
        this.fileName = fileName;
        this.foodConf = foodConf;
        this.notFoodConf = notFoodConf;
        this.imageType = imageType;
        this.sender = sender;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            System.out.println("errors abound");
            // Initialize the Driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://ec2-18-224-86-76.us-east-2.compute.amazonaws.com:3306", "root", "password");
            Statement stmt = con.createStatement();
            // Insert data into the table
            stmt.executeQuery("INSERT INTO image_data.image_data (\'" + fileName +
                    "\',\'" + foodConf + "\',\'" + notFoodConf + "\',\'" + imageType + "\',\'" + sender + "\')\'");
            con.close();
            return null;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
