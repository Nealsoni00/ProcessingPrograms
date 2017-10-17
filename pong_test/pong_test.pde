float ballX = 20;
float ballY = 20;
float ballR = 10;
float dX = random(1, 2);
float dY = random(1, 2);
float paddleX;
float paddleY = 10;
float paddleW = 10;
float paddleH = 30;
 
void setup() {
  size(400, 400);
  paddleX = 385;
}
 
void draw() {
  background(255, 255, 255);
  ellipse(ballX, ballY, 2 * ballR, 2 * ballR);
 
  rect(paddleX, paddleY, paddleW, paddleH);
 
  if (ballRight() > width) {
    fill(255, 0, 0, 100);
    rect(0, 0, width, height);
    noLoop();
  }
 
  if (collision()) {
    dX = -dX; // if dX == 2, it becomes -2; if dX is -2, it becomes 2
  }
 
  if (ballLeft() < 0) {
    dX = -dX; // if dX == 2, it becomes -2; if dX is -2, it becomes 2
  }
 
  if (ballBottom() > height) {
    dY = -dY; // if dY == 2, it becomes -2; if dY is -2, it becomes 2
  }
 
  if (ballTop() < 0) {
    dY = -dY; // if dY == 2, it becomes -2; if dY is -2, it becomes 2
  }
 
  ballX = ballX + dX;
  ballY = ballY + dY;
}
 
boolean collision() {
  boolean returnValue = false; // assume there is no collision
  if ((ballRight() >= paddleX) && (ballLeft() <= paddleX + paddleW)) {
    if ((ballBottom() >= paddleY) && (ballTop() <= paddleY + paddleH)) {
      returnValue = true;
    }
  }
  return returnValue;
}
 
float ballLeft() {
  return ballX - ballR;
}
 
float ballRight() {
  return ballX + ballR;
}
 
float ballTop() {
  return ballY - ballR;
}
 
float ballBottom() {
  return ballY + ballR;
}
 
// based on code from http://processing.org/reference/keyCode.html
void keyPressed() {
  if (key == CODED) {
    if (keyCode == UP) {
      paddleY = paddleY - paddleH;
    } else if (keyCode == DOWN) {
      paddleY = paddleY + paddleH;
    }
  }
}
