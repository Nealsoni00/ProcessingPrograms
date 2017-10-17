import gab.opencv.*;
import processing.video.*;
import java.awt.*;
//import processing.sound.*;
Capture video;
OpenCV opencv;

//Amplitude amp;
//AudioIn out;

void setup() {
  size(1280, 480);
  video = new Capture(this, 640/2, 480/2, 30);
  opencv = new OpenCV(this, 640/2, 480/2);
  opencv.loadCascade("closed_frontal_palm.xml");  
  
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
    noFill();
    rect(faces[i].x+faces[i].width/2, faces[i].y + (faces[i].height/2), faces[i].width, faces[i].height);
    stroke(256, 256, 256);
    
    float distance = 140.42- 25.5648*log(faces[i].width);
    println("you are " + distance + " Inches away from the camera");
    
    drawAnimation(xface,yface, distance);
    drawPointer(xface,yface, distance);
  }
}

void captureEvent(Capture c) {
  c.read();
}
void drawPointer(int xface, int yface, float distance){
  float weight = 1/distance*300;
  fill(0);
  stroke(0, 0, 0);
  ellipse(xface +300, yface, 5, 5);
}
int i = 0;
void drawAnimation(int xface, int yface, float distance){
  fill(255,255,255);
  rect(640,0,640,500);
  ellipseMode(CENTER);
  rectMode(CENTER);
//+660
  //ears
  stroke(0);
  line(0+660/2, 0, (80+660)/2, 100/2);
  line(0+660/2, 0, (100+660)/2, 80/2);
  line((500+660)/2, 0, (400+660)/2, 80/2);
  line((500+660)/2, 0, (420+660)/2, 100/2);

  //body
  curve((250+660)/2, (330)/2, (100+660)/2, 80/2, (400+660)/2, 80/2, (250+660)/2, 330/2);

  //curvy face
  curve((300+660)/2, 100/2, (80+660)/2, 100/2, (100+660)/2, (350/2), (500+660)/2, 500/2);
  curve((200+660)/2, 400/2, (420+660)/2, 100/2, (400+660)/2, (350/2), 0+660/2, 200/2);

  curve(0+660/2, 500/2, (100+660)/2, 350/2, (150+660)/2, 450/2, (80+660)/2, 380/2);
  curve((500+660)/2, 500/2, (400+660)/2, 350/2, (350+660)/2, 450/2, (420+660)/2, 520/2);

  curve((250+660)/2, 150/2, (150+660)/2, 450/2, (350+660)/2, 450/2, (250+660)/2, 150/2);
  //eyes
  ellipse((150+660)/2, 150/2, 90/2, (40+1/distance*100)/2);

  ellipse((350+660)/2, 150/2, 90/2, (40+1/distance*100)/2);
  fill(170,xface,xface);
  
  
  ellipse((350+660)/2, 150/2, (25+1/distance*190)/2, (25+1/distance*190)/2);
  ellipse((150+660)/2, 150/2, (25+1/distance*190)/2, (25+1/distance*190)/2);

  //nose
  line((210+660)/2, 240/2, (290+660)/2, 240/2);
  line((210+660)/2, 240/2, (250+660)/2, 290/2);
  line((290+660)/2, 240/2, (250+660)/2, 290/2);

  //mouth
  line((180+660)/2, 350/2, (320+660)/2, 350/2);
  line((200+660)/2, 420/2, (300+660)/2, 420/2);
  line((180+660)/2, 350/2, (200+660)/2, 420/2);
  line((320+660)/2, 350/2, (300+660)/2, 420/2);

  //tongue
  noFill();
  curve((250+660)/2, (yface-200)/2, (195+660)/2, 350/2, (305+660)/2, 350/2, (250+660)/2, (yface-200)/2);
}
