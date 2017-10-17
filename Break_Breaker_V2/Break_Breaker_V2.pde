//classes
class Brick {
  int x, y, w, h; //initialize variables
  color c;
  int level;
  int lives = 2;
  int f = 255; //fill for the brick
  int rand; //this was once a "random" number but now it's changed by the collision type
  int a, b, d, e;//random cracks veriables
  Brick(int ix, int iy, int iw, int ih, color ic, int il) {
    x = ix;  //get all the variables into the class
    y = iy; 
    w = iw; 
    h = ih; 
    c = ic;
    level = il;
    rand = 2;//int(random(1,3)); //random type of crack (-\- vs \v/)
    a = int(random(-15, 15)); //make each crack in the brickunique
    b = int(random(-5, 5)); //same
    d = int(random(0, 15));//same
    e = int(random(-5, 5));
  }
  void draw() {
    stroke(0);
    strokeWeight(1);
    fill(c, f);
    if (lives>0) {
      rect(x, y, w, h);
      if (lives==1) {
        stroke(255);
        strokeWeight(2);
        if (rand == 1) { // "-\-" fracture
          line(x-w/2+d, y-h/2, x-5, y+b);
          line(x-5, y+b, x+5, y-2);
          line(x+5, y-2, x+w/2-d, y+h/2);
        } else if (rand == 2) { // "\v/" fracture
          line(x-w/2+d, y-h/2, x-w/4, y-h/4+5);
          line(x-w/4, y-h/4+5, x+e, y+h/2);
          line(x+e, y+h/2, x+b, y-3);
          line(x+e, y+h/2, x-b, y+3);
          line(x+e, y+h/2, x+w/2-d, y-h/2);
        } else if (rand == 3) { //left fracture "<-"
          line(x-w/2, y+b, x+b, y-h/2); 
          line(x-w/2, y+b, x+a, y+b);
          line(x-w/2, y+b, x+a, y+h/2);
          //line(x+b,y+h/2,x+w/2,y+b);
        } else if (rand == 4) { //right fracture "->"
          line(x+w/2, y+b, x+b, y-h/2); 
          line(x+w/2, y+b, x+a, y+b);
          line(x+w/2, y+b, x+a, y+h/2);
        }
      }
    }
  }
}
class Ball { //ball class
  int r; //initialize all the variables
  float vx, vy;
  int x, y;
  color c;
  Ball() {
    r = 10;
  }
  void initialize(int ix, int iy) { //reset all the variables
    x = ix;
    y = iy;
    vy = 5;
    int[] sign = {
      -1, 1
    };
    vx = random(7, 10) * sign[round(random(1))];
  }
  void draw() { //draw the ball
    fill(c);
    noStroke();
    ellipse(x, y, r, r);
  }
  void move() {
    x += vx;
    y += vy;
    // vy+=.03; //Gravity???
  }
  void checkCollisions() {   
    if (y <= 0) { // if it hits the top or bottom, it bouncces
      smaller ++;
      if (smaller == 1) {
        player.w = player.w/2;
      }
      vy = -vy;
    }
    if  (y > height) {
      vy = -vy;
      lives--;
      ball.initialize(400, 300);
    }
    if (x <= 0 || x > width) { //same with left or right.
      vx = -vx;
    }      
    if (collideCorners(player)||collideLeftOrRight(player) || collideTopOrBottom(player) ) { // corner and left or right edge
      collidePaddle(player);
      //println(vy);
    }
    for (int i = 0; i < 100; i++) {
      if (brick[i].lives > 0) {
        if (collideLeftB(brick[i])) { // corner and left or right edge
          vx = -vx;
          brick[i].lives = brick[i].lives - 1;
          score = score + 11-brick[i].level;
          brick[i].rand = 3; // change break type of brick
          println("Left");
        } else if (collideRightB(brick[i])) {
          vx = -vx;
          brick[i].lives = brick[i].lives - 1;
          score = score + 11-brick[i].level;
          brick[i].rand = 4; // change break type of brick so that it comes from the side
          brick[i].b = y-brick[i].y; 
          println("right");
        } else if (collideCornersB(brick[i])) {
          vx = -vx;
          brick[i].lives = brick[i].lives - 1;
          score = score + 11-brick[i].level; //add scorebased on the bricks level
          brick[i].rand = 1; // change break type of brick
        }
        if (collideTopOrBottomB(brick[i])) { // top or bottom edge
          vy = -vy;//abs(x-player.w/2+player.x)/100;
          brick[i].lives = brick[i].lives - 1;
          score = score + 11-brick[i].level; //add scorebased on the bricks level
          brick[i].e = x-brick[i].x;
        }
        if (brick[i].lives == 1) {
          brick[i].f = 100; // make the brick become liter when it is hit by the ball (lives < 2)
        }
      }
    }
  }
  //check to see if collisions with paddle and balls
  boolean collideTopOrBottom(Paddle p) { // top or bottom of the paddle collision checker
    return((abs(p.y - p.h/2 - y) <= r || abs(p.y + p.h/2 - y) <= r) && x >= p.x - p.w/2 && x <= p.x + p.w/2);
  }
  boolean collideLeftOrRight(Paddle p) { // left or right of the paddle collision checker
    return((abs(p.x - p.w/2 - x) <= r || abs(p.x + p.w/2 - x) <= r) && y >= p.y - p.h/2 && y <= p.y + p.h/2);
  }
  boolean collideCorners(Paddle p) { // corners of the paddle collision checker
    return(distance(x, y, p.x - p.w/2, p.y - p.h/2) <= r || distance(x, y, p.x + p.w/2, p.y - p.h/2) <= r || distance(x, y, p.x - p.w/2, p.y + p.h/2) <= r || distance(x, y, p.x + p.w/2, p.y + p.h/2) <= r);
  }  
  boolean collideTopOrBottomB(Brick p) { //top or bottom of the brick "p" collision checker
    return((abs(p.y - p.h/2 - y) <= r || abs(p.y + p.h/2 - y) <= r) && x >= p.x - p.w/2 && x <= p.x + p.w/2);
  }
  boolean collideLeftB(Brick p) { // left (just so the cracks are right
    return(abs(p.x - p.w/2 - x) <= r && y >= p.y - p.h/2 && y <= p.y + p.h/2);
  }
  boolean collideRightB(Brick p) { // right (same reason as above)
    return(abs(p.x + p.w/2 - x) <= r && y >= p.y - p.h/2 && y <= p.y + p.h/2);
  }
  boolean collideCornersB(Brick p) { // corners of the brick
    return(distance(x, y, p.x - p.w/2, p.y - p.h/2) <= r || distance(x, y, p.x + p.w/2, p.y - p.h/2) <= r || distance(x, y, p.x - p.w/2, p.y + p.h/2) <= r || distance(x, y, p.x + p.w/2, p.y + p.h/2) <= r);
  }
  //bounce off of paddle when one of the three above statements about the paddle are true
  void collidePaddle(Paddle p) {
    float projx = collide(x - r, x + r, p.x - p.w/2, p.x + p.w/2);

    float projy = collide(y - r, y + r, p.y - p.h/2, p.y + p.h/2);

    if (abs(projx) <= abs(projy)) { // hit paddle
      x += projx; // rectify collision
      vx = -vx; // bounce
    } else {
      y += projy; // rectify collision
      vy = -vy; // bounce
    }
  }
}
float distance(float x1, float y1, float x2, float y2) {  //distance formula method
  return(sqrt(sq(x2 - x1) + sq(y2 - y1)));
}
float collide(float c1, float c2, float c3, float c4) { // check if they colide. This is used just for the paddle as there was priviously some errors with glitching. This method fixes that
  if (c1 > c3 && c1 < c4) { // left edge overlap
    return(c4 - c1);
  } else if (c2 > c3 && c2 < c4) { // right edge overlap
    return(c3 - c2);
  }
  return(0); // no overlap
}
int smaller = 0; // for paddle size (to make it smaller only once)
class Paddle { //paddle class for the player
  int x = 0;
  int y = 570;
  int w = 100;
  int h = 15;
  float a, b;
  Paddle() {
    a = random(70, 130);  // random collors for the paddle (failed atempt at making it change collor when dead)
    b = random(100, 255);
  }
  void draw() {
    stroke(2);
    fill(a, a, b); 
    rect(x, y, w, h); //draw the paddle
  }
  void refresh() {
    if (godMode) { // for atonomus mode
      noCursor();
      textSize(10);
      text("GODE MODE ON", 10, 10);
      x = ball.x;
      image(mouse, ball.x, ball.y/2-ball.vx, 25, 20); //draws a cursor where the ball is (kind of pointless)
    } else {
      cursor();
      x = mouseX;
    }
    if (mousePressed) {  //if the mouse is pressed, it changes between godmode and not godmode
      if (!mouseWasPressed) { 
        mouseWasPressed = true;
        godMode = !godMode;
      }
    } else {
      mouseWasPressed = false;
    }
  }
}
// initailize variables and boolians
boolean mouseWasPressed = false;  // godMode boolean
boolean godMode = false; // same as above
PImage mouse; // atonomus image of a mac mouse
int score = 0;
int lives = 3;
Ball ball = new Ball(); // make a new ball
Brick[] brick; //make an array of bricks 
void setup() {
  size(801, 600); // set up all the text framerate...
  ellipseMode(CENTER);
  rectMode(CENTER);
  textAlign(CORNER);
  textSize(20);
  frameRate(60);
  mouse = loadImage("Pointer.png"); // load the pointer image
  brick = new Brick[100]; // define the brick array to have 100 slots
  //intro();
  reset(); //draw all the bricks
  gameStart(); //start the game
}
void gameStart() {
  ball.initialize(400, 300);
}
void reset() { //to reset bricks when you die or new level
  float bWidth = width/10; 
  float bHeight = height/30; 
  color c = color(0, 0, 0);
  int level = 1;
  for (int i=0; i<10; i++) {//y
    for (int j=0; j<10; j++) {//x
      if (i<2) {
        c = color(255, 0, 0); //red
      } else if (i<4) {
        c = color(255, 165, 0); //orange 
      } else if (i<6) {
        c = color(230, 230, 0); //yellow
      } else if (i<8) {
        c = color(0, 230, 0); //green
      } else if (i<10) {
        c = color(0, 200, 200); //blue
      }
      brick[i*10+j] = new Brick(int(bWidth*j+width/20), int(bHeight*i+30), int(bWidth-2), int(bHeight-2), c, level); //create new brick at a scalable location
    }
    level++;//level is used for points
  }
}


Paddle player = new Paddle(); //initalize the player paddle
void draw() {
  if (lives>0) {
    background(255);
    for (int i = 0; i < 100; i++) {
      brick[i].draw();
    }
    player.refresh(); //all the paddle commands here and below
    player.draw(); //same as above
    ball.draw();
    ball.move();
    ball.checkCollisions();
    textAlign(CORNER);
    textSize(20);
    fill(40, 12, 100);
    text("You have " + lives + " lives left", 0, height-5);
    text("Your Score is: " + score + " points", width-250, height-5);

    if (score == 1100) { // you won if statement (the game still goes on)
      text("YOU WON", width/2, height/2+50);
    }
  } else { // that means you lost
    cursor();
    textAlign(CENTER);
    stroke(1);
    textSize(100);
    fill(0);
    text("GAME OVER!", width/2, height/2+50);
    fill(150, 245, 222);
    rect(width/2, height/2+120, 150, 30);
    textSize(30);
    fill(0);
    text("mouse", width/2, height/2+95);
    text("here", width/2, height/2+130);
    text("to replay", width/2, height/2+160);
    if (mouseX < width/2+75 && mouseX > width/2-75 && mouseY < height/2+120+15 && mouseY>height/2+120) { //if the mouse is in that location, the game starts over
      lives = 3; 
      reset();
      gameStart();
      score = 0;
      player.w = 100;
      noCursor();
    }
    ball.initialize(width-10, height-10);
  }
}

void keyPressed() {
  int a = 0;
  if (looping) {  //Pauseing function
    textMode(CENTER);
    noLoop();
    textSize(40);
    println(a);
    fill(random(0, 255), random(0, 255), random(100, 255));
    text("Paused", width/2-60, height/2-20); //failed atempt at changing color
  } else {
    loop(); //when you do it again, it plays the game as the keypressed method runs independent of the main method
  }
}

