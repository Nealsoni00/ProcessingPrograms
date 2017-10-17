import gab.opencv.*;
import processing.video.*;
import java.awt.*;
//import processing.sound.*;
//openCV face detection setup
Capture video;
OpenCV opencv;
OpenCV nose;
//i could not figure out how to make him say "Ow" using the audio library (ant command does not build it)
//Amplitude amp;
//AudioIn out;
//Pow image for clicking
PImage pow;

void setup() {
  
  size(1280, 480);
  video = new Capture(this, 640, 480, 60);
  opencv = new OpenCV(this, 640, 480);
  //nose = new OpenCV(this, 640/2, 480/2);
  //nose.loadCascade("haarcascade_mcs_nose.xml");  
  opencv.loadCascade("haarcascade_frontalface_alt.xml");

  //start video
  video.start();
  
  //load pow image for when u click
  pow = loadImage("POW.png");
}

void draw() {
 
  
  opencv.loadImage(video);
  //nose.loadImage(video);
  //reverse image
  
  pushMatrix();
  scale(-1,1);
  //image(video, 0, 0); //while this line is disabled, the image is not inverted
  popMatrix();
  
  video.read();
  scale(1);
  image(video, 0, 0 );
  //set info for rectangle drawn over the face
  noFill();
  stroke(0, 255, 0);
  strokeWeight(3);
  Rectangle[] faces = opencv.detect();
  //Rectangle[] noses = nose.detect();
  println(faces.length);

  for (int i = 0; i < faces.length; i++) {
    noStroke();
    fill(255,255,255);
    rect(1280,0,1280,960);
    strokeWeight(3);
    stroke(0,255,0);
    //define the center point of the face in the variable xface, yface
    int xface = faces[i].x + (faces[i].width/2);
    int yface = faces[i].y + (faces[i].height/2);
    //println(faces[i].x + "," + faces[i].y);
    //draw the rectange
    noFill();
    rect(faces[i].x+faces[i].width/2, faces[i].y + (faces[i].height/2), faces[i].width, faces[i].height);
    //rect(noses[i].x+noses[i].width/2, noses[i].y + (noses[i].height/2), noses[i].width, noses[i].height);

    stroke(256, 256, 256);
    //Calculate the distance using a logorithmic regression 
    float distance = 154.42- 25.5648*log(faces[i].width); //distance algotithem
    println("you are " + distance + " Inches away from the camera");
    //Draw the animation using the variables
    drawAnimation(xface,yface, distance);
    //drawPointer(xface,yface, distance);
  }
  //draw the "pow" when the mouse is clickeed
  if(mousePressed){
    image(pow, mouseX-40, mouseY-35,width/10, height/5);
  }
}
//run the camera using openCV
void captureEvent(Capture c) {
  c.read();
}
//debuging pointer
void drawPointer(int xface, int yface, float distance){
  float weight = 1/distance*300;
  fill(0);
  stroke(0, 0, 0);
  ellipse(xface +300, yface, 5, 5);
}
int i = 0;
void drawAnimation(int xshift2, int yshift2, float distance){
  //Xshift and yshift are used to move the animation on the x axis and y axis
  int xshift = xshift2 + 640;
  //yshift = yshift - 30;
  //int yshift = yshift2;
  int yshift = 200;
  
  fill(255,255,255);
  //rect(640,0,640,500);
  ellipseMode(CENTER);
  rectMode(CENTER);
  //green
  //draw Mike
  stroke(0,0,0);
  fill(0, 153, 51);
  //body
  ellipse(88+xshift,88+yshift,156, 176);
  //eye
  ellipse(88+xshift,45+yshift,76,88);
  //white
  fill(255,255,255);
  //fill(105,245,255); //blueish white
  ellipse(88+xshift,45+yshift,76,80);
  fill(0, 153, 51);
   //change the location of the retna using the face
  ellipse(88+xshift-(xshift2/22-10),45+yshift+(yshift2/10-24),44/*+abs(1/distance)*10*/,44/*+abs(1/distance)*10*/);
  //Change color of retna using distance, so as u move forward, it changes to black but as u move further away, it goes to green
  fill(0+distance*6, 51+distance*12, 0+distance*3);
  //change the location of the retna using the face
  ellipse(88+xshift-(xshift2/22-10),45+yshift+(yshift2/10-24),24/*+abs(1/distance)*10*/,24/*+abs(1/distance)*10*/);
  //mouth
  fill(0,153,0);
  arc(88+xshift, 123+yshift, 56, 42, PI, 2*PI);
  arc(88+xshift, 123+yshift, 56, 20, 0, PI);
  
  //ears
  fill(0, 153, 51);
  line(40+xshift,  17+yshift,  43+xshift,  0+yshift);
  line(43+xshift,  0+yshift,   55+xshift,  8+yshift);
  line(120+xshift, 6+yshift,   132+xshift, 0+yshift);
  line(132+xshift, 0+yshift,   134+xshift, 19+yshift);
  fill(255,255,255);
  //arms
  //right arm
  line(167+xshift, 80+yshift,   179+xshift, 183+yshift);
  line(162+xshift, 122+yshift,  168+xshift, 183+yshift);
  arc(173+xshift,183+yshift, 11, 6, 0, PI);
  //left arm
  line(14+xshift,  120+yshift,  15+xshift,  192+yshift);
  arc(10+xshift,   140+yshift,  20,120, PI/2+.54,3*PI/2);
  arc(10+xshift,   190+yshift,  11, 6, 0, PI);
  //left leg 
  line(40+xshift,  17+yshift,   43+xshift,  0+yshift);
  line(79+xshift,  177+yshift,  73+xshift,  210+yshift);
  line(60+xshift,  173+yshift,  60+xshift,  210+yshift);
  line(60+xshift,  210+yshift,  67+xshift,  240+yshift);
  line(73+xshift,  210+yshift,  80+xshift,  240+yshift);
  arc( 73+xshift,  240+yshift,  12,7, 0, PI);
  //right leg
  line(114+xshift, 170+yshift,  125+xshift, 210+yshift);
  line(134+xshift, 161+yshift,  138+xshift, 210+yshift);
  line(138+xshift, 210+yshift,  135+xshift, 240+yshift);
  line(125+xshift, 210+yshift,  122+xshift, 240+yshift);
  arc( 129+xshift, 240+yshift,  11, 7, 0, PI);
  //Draw the grass that grows as u move higher up
  for(int i = 640; i < 1280; i = i + 27){
    stroke(0,255,0);
    line(i,    480, i,    440+yshift2/random(10,20));
    line(i+9,  480, i+9,  430+yshift2/random(20,30));
    line(i+18, 480, i+18, 430+yshift2/random(10,30));
  }

}
