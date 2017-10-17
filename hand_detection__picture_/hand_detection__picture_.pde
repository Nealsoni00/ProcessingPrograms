import gab.opencv.*;
import java.awt.Rectangle;
import processing.video.*;

Capture video;
OpenCV opencv;
Rectangle[] hands;
int w = 2;
void setup() {


  size(1280/w, 720/w);
  video = new Capture(this, 1280/w, 720/w, 30);
  opencv = new OpenCV(this, 1280/w, 720/w);
 
  opencv.loadCascade("closed_frontal_palm.xml"); 
  //opencv.loadCascade("hand.xml"); 
  //opencv.loadCascade("hand1.xml");
  //opencv.loadCascade("closed_frontal_palm.xml");
  //opencv.loadCascade("haarcascade_frontalface_alt.xml");
  video.start();
}

void draw() {
  opencv.loadImage(video);
  image(video, 0, 0 );
  Rectangle[] hands = opencv.detect();
  noFill();
  stroke(255, 0, 0);
  strokeWeight(3);
  for (int i = 0; i < hands.length; i++) {
    rect(hands[i].x, hands[i].y, hands[i].width, hands[i].height);
  }
}
void captureEvent(Capture c) {
  c.read();
}
