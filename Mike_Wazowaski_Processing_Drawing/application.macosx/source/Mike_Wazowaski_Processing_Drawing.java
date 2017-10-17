import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Mike_Wazowaski_Processing_Drawing extends PApplet {
  public void setup() {
size(210,280);
background(255);
stroke(0);

//green
fill(0, 153, 51);
//body
ellipse(88,88,156, 176);
//eye
ellipse(88,45,76,88);
//white
fill(255,255,255);
//fill(105,245,255); //blueish white
ellipse(88,45,76,80);
fill(0, 153, 51);
ellipse(88,45,36,36);
//black for retna
fill(0, 51, 0);
ellipse(88,45,16,16);
//mouth
arc(88, 123, 56, 42, PI, 2*PI);
arc(88, 123, 56, 20,0,PI);
//ears
fill(0, 153, 51);
line(40,17,43,0);
line(43,0,55,8);
line(120,6,132,0);
line(132,0,134,19);
fill(255,255,255);
//arms
//right arm
line(167,80,179,183);
line(162,122,168,183);
arc(173,183, 11, 6, 0, PI);
//left arm
line(14,120,15,192);
arc(10,140,20,120, PI/2+.54f,3*PI/2);
arc(10,190, 11, 6, 0, PI);
//left leg
line(79,177,73,210);
line(60, 173 ,60, 210);
line(60,210,67,240);
line(73,210,80,240);
arc(73,240, 12,7, 0, PI);
//right leg
line(114,170, 125, 210);
line(134,161,138,210);
line(138,210,135, 240);
line(125,210,122, 240);
arc(129,240, 11,7, 0, PI);
    noLoop();
  }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Mike_Wazowaski_Processing_Drawing" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
