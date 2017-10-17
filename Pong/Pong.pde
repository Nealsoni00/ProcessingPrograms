float ballX = 20;
float ballY = 25;
float dX = random(1, 2);
float dY = random(1, 2);
float paddleX;
int paddleY = 10;
float paddle2X = 20;
int paddle2Y = 380;


void setup(){
  size(400,400);
  frameRate(60);
  smooth();
  
  paddleX = width-15;
}
  


  
void draw(){
  background(255);
  fill(0,255,0);
  stroke(0);
  ellipse(ballX, ballY,10,10);
  rect(paddleX,paddleY,10,30);
  if (ballX > width || ballX < 0){
    dX = -dX;
  }

  if (ballY > height || ballY < 0){
    dY = -dY;
  }

  ballX = ballX + dX;
  ballY = ballY + dY;
}

void keyPressed(){
 if (key == CODED){
   if (key == UP){
     paddleY = paddleY - 30;
    }
  }else if (keyCode == DOWN){
    paddleY = paddleY + 30;
  }
}
  
