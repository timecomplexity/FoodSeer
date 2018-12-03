# FoodSeer

**Basha'Aer Almadaoji, Jackson Bush, Adam Horvath-Smith, and Alex Silcott, 12-4-2018**

_Course project for CEG 4110, Wright State University, Fall 2018._

This app gives your phone the mystic power of seeing food. Literally! With the FoodSeer app, your phone will be able to tell you whether or not any photo contains some food object with reasonable accuracy. Using highly advanced modern artificial intelligence technologies, this app will quickly return a decision as to whether or not your photo contains food. No more do you need to rely on an expensive fortune-teller or palm reader to tell you whether you will be seeing food in your day--as with everything, there's now an app for that!

# Running this app on your phone

To run this app on your phone, simply download and load the apk file above onto your phone. _No external resources/files are needed to run the app on your device._ After downloading the apk onto your computer, connect your phone to it with a USB cable. You may have to enable file sharing on your phone to be able to access its file system on your computer. Then, simply move the apk anywhere onto your phone's storage and, from your phone's file explorer, run the apk. Follow the prompts to install the app. (You may have to allow your phone to install from untrusted sources.) Once the installer is done, open the app and enjoy!

# How to use this app

It is important to note that using this app requires an internet connection. Your phone needs to be able to speak with mystic spirits in the cloud to be able to tell whether your photo contains food!

The FoodSeer app is designed to be simple, fun, and easy to use. Upon opening the app, users are presented with a main menu. This displays the most recently uploaded images from other users of the app, as well as giving the user the option to upload their own images. Users can choose to upload an image either from the photos already taken and stored on their device, or they can choose to take a new photo and upload it directly to the FoodSeer AI.

The main screen of the FoodSeer app allows the user to scroll through a gallery of previously-uploaded photos from other users. When you upload an image, the app will automatically update the main screen to include your photo and the decision the AI made as to whether it contains food. For every photo, the AI's decision is displayed both in text (using such phrases as "Not Food", "Hard to Say", and "Food!") and graphically, using a bar to show the relative confidence that the AI thinks your photo contains food. Should you want to see even _more_ food that what is displayed to you at first, you have the option to load more images from our near-endless supply.

If you ever find yourself stuck using the FoodSeer app, there is a built-in help function right on the main screen. Simply click the '?' icon in the bottom left corner, and a simple user guide comes right up!

_A glance into the psychic's globe..._

_The most recent photo..._                                                                                                 |  _...and several others_
:-------------------------------------------------------------------------------------------------------------:|:-------------------------:
![](https://github.com/blastbeatsandcode/FoodSeer/raw/master/screenshots/Screenshot_2018-12-02-22-49-16.png)  |  ![](https://github.com/blastbeatsandcode/FoodSeer/raw/master/screenshots/Screenshot_2018-12-02-22-49-27.png)

# Design and Implementation

This app was written in Java using the Android Studio IDE. Cloning this repository will clone an Android Studio project, which you can easily open on your own computer.

The AI used on the backend of this project was deployed to an Amazon Web Services EC2 instance. Communications between the end-user device and the EC2 instance rely on HTTP requests. The API for the AI was exposed on the EC2 instance using the Python Flask library. See more at http://flask.pocoo.org/. Communications from the device to the EC2 inscance were enabled using the Apache HTTP Components library for Java. See more at https://hc.apache.org/. Photos are stored on the EC2 instance using a MySQL database. See more at https://www.mysql.com/. The photo chooser is a library specifically designed to allow a user to choose multiple images. See the repo at https://github.com/darsh2/MultipleImageSelect. The AI used for this project was provided to us by the course professor, Dr. Derek Doran. This is publicly available at https://github.com/wsu-wacs/seefood.

The app uses a loose Model-View-Controller design pattern. This is evidenced in the structure of the source code files, which are split into 'Model', 'View', and 'Controller' folders. Additionally, there is a folder for 'Utils' classes. Code deployed to the EC2 instance is stored in 'ai-server-files'.

# Acknowledgments

Aside from the resources noted above, it should be noted that this is a class project for CEG 4110 (Introduction to Software Engineering) at Wright State University. The app idea is not our own; requirements were specified in the project outline given to the class by Dr. Derek Doran. The design and implementation of this app, aside from the above-referenced libraries and packages, is completely our own.
