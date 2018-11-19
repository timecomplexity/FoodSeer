package com.blastbeatsandcode.seefood.model;

import android.media.Image;

public class SFImage {
    private Image _image;
    private float _foodConfidence;
    private float _notFoodConfidence;
    private String _sender;
    private String _fileType;
    private String _imagePath;

    // Constructor for before having the actual image
    public SFImage (float foodConfidence, float notFoodConfidence, String sender,
                    String fileType, String imagePath)
    {
        this._foodConfidence = foodConfidence;
        this._notFoodConfidence = notFoodConfidence;
        this._sender = sender;
        _imagePath = imagePath;
        _fileType = fileType;
    }

    // Constructor for SFImage with all of the components
    SFImage (Image image, float foodConfidence, float notFoodConfidence, String sender,
             String fileType, String imagePath)
    {
        this._image = image;
        this._foodConfidence = foodConfidence;
        this._notFoodConfidence = notFoodConfidence;
        this._sender = sender;
        _imagePath = imagePath;
        _fileType = fileType;
    }


    // Constructor for SFImage with only image given
    SFImage(Image image)
    {
        this._image = image;
    }

    public String getImagePath() { return _imagePath; }

    // Returns the sender of the SFImage
    public String getSender()
    {
        return _sender;
    }

    public void setSender(String sender)
    {
        this._sender = sender;
    }

    // Returns the food confidence of the SFImage
    public float getFoodConfidence()
    {
        return _foodConfidence;
    }

    public void setFoodConfidence(float foodConfidence)
    {
        this._foodConfidence = foodConfidence;
    }

    // Returns the not food confidence of SFImage
    public float getNotFoodConfidence()
    {
        return _notFoodConfidence;
    }

    public void setNotFoodConfidence(float notFoodConfidence)
    {
        this._notFoodConfidence = notFoodConfidence;
    }

    public String getFileType() {return _fileType;}

    // Returns the image component of the SFImage
    public Image getImage()
    {
        return _image;
    }

    public void setImage(Image image) {
        _image = image;
    }

    // Generates a confidence graphic based on the food confidence and not food confidence values
    public void generateConfidenceGraphic() {
        // Ensure that the confidences are set
        try {
            System.out.println("Create confidence graphic");
            // TODO: Implement this
        } catch(Exception e) {
            System.out.println("Values for food confidence have not been set!");
        }
    }
}
