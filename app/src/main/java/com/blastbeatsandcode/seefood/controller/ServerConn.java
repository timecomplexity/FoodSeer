package com.blastbeatsandcode.seefood.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.blastbeatsandcode.seefood.model.SFImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
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
    static int currentLast = -1;

    public ServerConn() {
        _client = HttpClients.createDefault();
    }

    public SFImage getSFImage(int currentTarget) {
        DBGetter g = new DBGetter(false, currentTarget);
        g.execute();
        try {
            g.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return g.getSfi();
    }

    public SFImage[] getSFImageBatch(int[] currentTarget) {
        // Create array of DB Getters to run in parallel
        DBGetter[] getters = new DBGetter[currentTarget.length];
        for (int i = 0; i < currentTarget.length; i++) {
            if (currentTarget[i] == 0) break;
            getters[i] = new DBGetter(false, currentTarget[i]);
            getters[i].execute();
        }

        // Block on each of the getters, return an array consisting of all SFIs
        SFImage[] images = new SFImage[currentTarget.length];
        for (int i = 0; i < getters.length; i++) {
            try {
                if (getters[i] == null) break;
                getters[i].get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            images[i] = getters[i].getSfi();
        }

        return images;
    }

    /*
     * Set the connection request
     */
    public void setRequest(byte[] data) {
        _request = data;
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

    public int retrieveLastDBItemId() {
        DBGetter g = new DBGetter(true);
        g.execute();
        try {
            g.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        currentLast = g.getCurrentLast();
        return currentLast;
    }

}

/*
 * Sender sends requests to the server
 */
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
            // Initialize the Driver class
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://ec2-18-224-86-76.us-east-2.compute.amazonaws.com:3306",
                    "root", "password");
            Statement stmt = con.createStatement();
            // Insert data into the table (this is disgusting!)
            String sql = "INSERT INTO image_data.image_data (path, foodconf, notfoodconf, type, " +
                    "sender) VALUES (\'" + fileName + "\'," + foodConf + "," + notFoodConf +
                    ",\'" + imageType + "\',\'" + sender + "\')";
            stmt.executeUpdate(sql);
            con.close();
            return null;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}


class DBGetter extends AsyncTask {
    private String result = "";
    private boolean forLast;
    private int idToSearch;
    private int currentLast;
    private SFImage sfi;

    DBGetter(boolean forLast) {
        this.forLast = forLast;
    }

    DBGetter(boolean forLast, int idToSearch) {
        this.forLast = forLast;
        this.idToSearch = idToSearch;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://ec2-18-224-86-76.us-east-2.compute.amazonaws.com:3306",
                    "root", "password");
            Statement stmt = con.createStatement();

            // Create a sql statement depending on whether we want the index of the last image
            //   or if we want to get the data related to some index
            String sql;
            if (forLast) {
                // Find the last ID if we don't have it
                sql = "SELECT id FROM image_data.image_data WHERE id=" +
                        "(SELECT MAX(id) FROM image_data.image_data)";
            } else {
                // Get data from last ID object otherwise
                sql = "SELECT * FROM image_data.image_data WHERE id=" + idToSearch;
            }

            // Get our results
            ResultSet rs = stmt.executeQuery(sql);

            if (forLast) {
                // Set the current last item number
                rs.next();
                currentLast = rs.getInt(1);
            } else {
                // Get all the data out of the DB query otherwise
                while (rs.next()) {
                    String imagePath = rs.getString(1);
                    float foodConf = rs.getFloat(2);
                    float notFoodConf = rs.getFloat(3);
                    String fileType = rs.getString(4);
                    String sender = rs.getString(5);
                    result = imagePath + " " + foodConf + " " + notFoodConf + " " + fileType + " "
                             + sender;

                    // Get the bitmap information
                    HttpPost request = new HttpPost(
                            "http://ec2-18-224-86-76.us-east-2.compute.amazonaws.com:5000/api/get-image");

                    // Create an entity to send over POST
                    MultipartEntityBuilder entity = MultipartEntityBuilder.create();
                    entity.setCharset(Charset.defaultCharset());

                    // Add the filepath
                    entity.addTextBody("filepath", imagePath);

                    // Set up the above entity to send
                    request.setEntity(entity.build());
                    Bitmap bmp = null;

                    // Send off to server
                    CloseableHttpResponse response = HttpClients.createDefault().execute(request);

                    // Give back the server response (confidence levels)
                    InputStream input = response.getEntity().getContent();
//                    System.out.println(input);
//                    System.out.println(response.getEntity().getContent());

                    // Hold on to our InputStream to use more than once
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = input.read(buffer)) > -1 ) {
                        baos.write(buffer, 0, len);
                    }
                    baos.flush();

                    // Duplicate ISs
                    InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
                    InputStream is2 = new ByteArrayInputStream(baos.toByteArray());

                    ///// For this, see https://developer.android.com/topic/performance/graphics/load-bitmap /////
                    // First decode with inJustDecodeBounds=true to check dimensions
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(is1, null, options);

                    // Calculate inSampleSize
                    options.inSampleSize = calculateInSampleSize(options, 300, 300);

                    // Create an image from the input stream
                    options.inJustDecodeBounds = false;
                    bmp = BitmapFactory.decodeStream(is2, null, options);

                    ///// End block of awesomeness /////

                    sfi = new SFImage(foodConf, notFoodConf, sender, fileType, imagePath, bmp);
                }
                con.close();
            }

            return null;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }

    // Really cool downsampling code
    // See https://developer.android.com/topic/performance/graphics/load-bitmap
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public int getCurrentLast() {
        return currentLast;
    }

    public SFImage getSfi() { return sfi; }

    public String getResult() {
        return result;
    }
}
