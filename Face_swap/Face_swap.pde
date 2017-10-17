import gab.opencv.*;
import java.awt.Rectangle;

OpenCV opencv;
Rectangle[] faces;
PImage face;
void setup() {
  opencv = new OpenCV(this, "test.jpg");
  face = loadImage("test.jpg");
  size(600,600);

  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);  
  faces = opencv.detect();
}

void draw() {
  image(opencv.getInput(), 0, 0);

  noFill();
  stroke(0, 255, 0);
  strokeWeight(3);
  for (int i = 0; i < faces.length; i++) {
    rect(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
  }
}