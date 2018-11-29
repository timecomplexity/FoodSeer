package com.blastbeatsandcode.seefood.view;

import com.blastbeatsandcode.seefood.model.SFImage;

import java.util.ArrayList;

/*
 * SFView defines the interface that the views will implement.
 */
public interface SFView {
    void uploadImage();
    void viewGallery();
    void displayHelp();
    void takePicture();
    void update();
}
