void setup(){
  size(600,600);
  background(250);
  
}
float i;
float x;
float z;
void draw(){
  //randome fun colors!
//  red = random(250);
//  green = random(250);
//  blue = random(250);
float red,green,blue;

  for (red = 0; red <= 255; red++) {
      for (green = 0; green <= 255; green++) {
          for (blue = 0; blue <= 255; blue++) {
            stroke(0);
            fill(red, green, blue);
            rectMode(CENTER);
            rect(mouseX,mouseY,50,50);
          }
      }
  }
  /*
  stroke(0);
  fill(red, green, blue);
  rectMode(CENTER);
  rect(mouseX,mouseY,50,50);
  */
  //background(255);//This has to be in the main loop so that it resets the background after every rect draw
  
  //body
  
  
 
  
 
  
   
}
