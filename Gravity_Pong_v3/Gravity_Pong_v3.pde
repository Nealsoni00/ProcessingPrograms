float y = 0;
float m = 0; 
float v = 0;
float x = 200;
float vx = random(10,25);
float vi = 40;
int score = 0;
int highscore = 0;
PImage ball;
void setup() {
  size(1000,760);
  background(255);
  ball = loadImage("soccer.png");
  frameRate(60);

}
void draw(){
 background(255);
  textSize(30);
  fill(0);
  noStroke();
  
   if (m > 0){
     image(ball, x, y, 30, 30);
     //ellipse(x,y,25,25);
   y = y + v;
   v = v + 1.0;
   v = v;
   x += vx;
  //println(vx); 
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
  
   if (x <= 50 && y >mouseY && y < mouseY + 125){
      vx *= -1; 
      x = 50;
      score = score + 1;
      println(score);
      rect(30,mouseY,25,125);
   }
   if (score >= highscore){
     highscore = 0;
     highscore += score;
   }
  }  
  fill(255,0,0);
  rect(25,mouseY,25,125);
  fill(0);
  text(score,width/2, 30);
   
   if (x <= 0){
     fill(0);
     text("GAMEOVER CLICK ANYWHERE TO REPLAY", 115,400);
     
     vx = random(10,25);
     v = 0;
     m = 0;
     
    

   }

   fill(255,0,0);
  rect(width-50,y-50,25,125);
  fill(0);
 text(highscore,215,30);
 text("HIGHSCORE:",30,30);
}
   
void mousePressed(){
    score = 0;
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
