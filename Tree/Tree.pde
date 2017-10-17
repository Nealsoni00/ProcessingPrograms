// This program demonstrates the use of recursion to draw trees.
// Written by Dr. Morgan

// Note that the frameRate was set to 1 frame per second, which
// gives the viewer some time to appreciate the picture before 
// the next one is drawn.
void setup() {
 size(800,720);
 strokeWeight(2);
 frameRate(1); 
}

void draw() {
  background(255);
  
  // This loop draws 10 trees and randomized horizontal locations.
  // Note that the drawTree function is the recursive function.
  for(int i=0; i<10; i++) drawTree(random(0.1*width, 0.9*width), 0.95*height, random(60, 150), random(-3,3));
  
  // draws the ground as a simple rectangle.
  fill(120,40,40);
  rectMode(CORNERS);
  rect(0,0.95*height, width, height);
}


// Recursive function to draw a tree.  This function only draws
// a single branch (the line command below), but it calls itself
// three times to draw three more branches at the end point of the
// current branch, and then each of those branches calls the 
// function three more times, etc.

// The inputs are the x,y location of where to start the branch, 
// the length of the branch, and the angle the branch is drawn.
// An angle of 0 degrees is straight up.
void drawTree(float x1, float y1, float size, float angle) {

  // Base case.  If the size gets too small or the angle of the 
  // branch too steep, then return without drawing anything.
  if (size <= 5. || abs(angle)>80) return; else {
    
    // This produces a color proportional to the size of the branch,
    // with large branches drawn as brown and small branches as 
    // green.
    float h = random(0, 50);
    float r = size/200 * 165 + h;
    float g = 42 + h;
    float b = size/200 * 42 + h;
    stroke(r, g, b);
    
    // large branches are thick, small branches are thin
    float n = size/8.0;
    if (n<1) n = 1;
    strokeWeight(n);

    // compute where to end the current branch
    float x2 = x1 - size*sin(angle*PI/180.);
    float y2 = y1 - size*cos(angle*PI/180.);
    
    // draw the branch
    line(x1,y1,x2,y2);
    
    // recursive call that draws three more branches using the
    // ending point of the current branch (x2,y2) as the starting
    // point of the new branches.
    drawTree(x2,y2, size*random(0.5, 0.9), angle+random(15,35));
    drawTree(x2,y2, size*random(0.5, 0.9), angle-random(15,35));
    drawTree(x2,y2, size*random(0.5, 0.9), angle-random(0,15));

  }
  
  
}
