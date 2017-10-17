int h;
void setup() {
  size(400, 400);
  noStroke();
 
  h = height/2; // Height of the two half gradients
}
 
void draw() {
  // Map mouseY to gradient intensity
  int cFrom = (int) map(mouseY, 0,height, 0,180);
  int cTo = (int) map(mouseY, 0,height, 80,255);
 
  // Draw a bunch of rectangles on top of each other to simulate gradient
  for (int i=h; i>0; i-=1) {
 
    color from = color(cFrom, 0, 0);
    color to = color(cTo, 0, 0);
 
    // Interpolate the color value from i for upper half
    color inter = lerpColor(from, to, map(i, h, 0, 1.0, 0.0));
    fill(inter);
    rect(0, 0, width, i);
 
    // Reverse-interpolate the color value from i for lower half
    inter = lerpColor(from, to, map(i, h, 0, 0.0, 1.0));
    fill(inter);  
    rect(0, height/2, width, i);
  }
}