import gab.opencv.*;
import processing.video.*;
import java.awt.*;

Capture video;
OpenCV opencv;

void setup() {
  size(1280, 480);
  video = new Capture(this, 640/2, 480/2, 30);
  opencv = new OpenCV(this, 640/2, 480/2);
  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);  

  video.start();
}

void draw() {
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

void captureEvent(Capture c) {
  c.read();
}
