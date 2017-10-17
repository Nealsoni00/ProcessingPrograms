import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import gab.opencv.*; 
import processing.video.*; 
import java.awt.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class OpenCVLiveFaceDetection extends PApplet {





Capture video;
OpenCV opencv;

public void setup() {
  size(1280, 480);
  video = new Capture(this, 640/2, 480/2, 30);
  opencv = new OpenCV(this, 640/2, 480/2);
  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);  

  video.start();
}

public void draw() {
  scale(2);
  opencv.loadImage(video);

  image(video, 0, 0 );

  noFill();
  stroke(0, 255, 0);
  strokeWeight(3);
  Rectangle[] faces = opencv.detect();
  println(faces.length);

  for (int i = 0; i < faces.length; i++) {
    int xface = faces[i].x + (faces[i].width/2);
    int yface = faces[i].y + (faces[i].height/2);
    println(faces[i].x + "," + faces[i].y);
    rect(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
    stroke(256, 256, 256);
    //ellipse(xface, yface, 1, 1);
  }
}

public void captureEvent(Capture c) {
  c.read();
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "OpenCVLiveFaceDetection" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
