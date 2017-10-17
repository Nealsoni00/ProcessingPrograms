float y = 0;
float m = 0; 
float v = 0;
float x = 200;
float vx = 5;
float vi = 40;

void setup() {
  size(800,800);
  background(255);
}
void draw(){
 background(255);
 //image(gif, 0, 0);
  textSize(30);
  fill(0);
  noStroke();
  
   if (m > 0){
   ellipse(x,y,15,15);
   y = y + v;
   v = v + 1.0;
   x += vx;
   println(vx); 
   fill(255,0,0);
   if (y >= height){
     v *= -1;
     y = height; 
   }
   if (y <= 0){
     y = 0;
     v *= -1;
   } 
   if (x >= width-50){
     vx *= -1; 
     x = width-50;
   }
  
   if (x <= 0){
      vx *= -1; 
      x = 50;

   }
   }
}
void mousePressed(){
    m = m + 1;
    if (m > 2 || m == 2){
     //y += - 100; 
      v = -20;
    }
    else {
    y = 200;
    v = 0;
    x = 200;
    vx = random(10,25);
    }
}