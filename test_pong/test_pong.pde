class Ball {
  float x, y;
  float vx, vy;
  float r;
  
  Ball() {
    r = 10;
  }
  
  void initialize(float xi, float yi) {
    x = xi;
    y = yi; 
    vy = 1.5;
    int[] sign = {-1, 1};
    vx = random(0.3, 1) * sign[round(random(1))]; // random number from -1 to -0.3 or 0.3 to 1
  }
  
  void checkCollisions() {   
    if (y < height) { // in between left edge and right edge
      if (y <= 0 ) // hit top edge or bottom edge
        vy = -vy;
      else if (x <= 0 || x > width) {
        vx = -vx;
      } else {        
        if (collideCorners(p1)|| collideLeftOrRight(p1)) { // corner and left or right edge
          vx = -vx;
        } else if (collideTopOrBottom(p1)){ // top or bottom edge
          vy = -vy;
        }
      }
    } else {
      lives--;
      startGame();
    }
    int i = 0;
    for (Bricks b: brick) {
      if (b != null){
        if (collideC(b)|| collideLorR(b)) { // corner and left or right edge
          vx = -vx;
          if (b.hit) {
            brick[i] = null;
            if (i < 20) {
              score += 5;
            } else if (i < 40) {
              score += 4;
            } else if (i < 60) {
              score += 3;
            } else if (i < 80) {
              score += 2;
            } else if (i < 100) {
              score += 1;
            }
          }
          b.hit = true;
          b.c += color(0,0,0,100);
        } else if (collideTorB(b)){ // top or bottom edge
          vy = -vy;
          if (b.hit) {
            brick[i] = null; //Make the brick element null if hit is already true
            if (i < 20) {
              score += 5;
            } else if (i < 40) {
              score += 4;
            } else if (i < 60) {
              score += 3;
            } else if (i < 80) {
              score += 2;
            } else if (i < 100) {
              score += 1;
            }
          }
          b.hit = true;
          b.c += color(0,0,0,100); //Opactiy change    
        }
      }
      i++;
    }
  }
  
  boolean collideTopOrBottom(Paddle p) {
    return((abs(p.y - p.h/2 - y) <= r || abs(p.y + p.h/2 - y) <= r) && x >= p.x - p.w/2 && x <= p.x + p.w/2);
  }
  
  boolean collideLeftOrRight(Paddle p) {
    return((abs(p.x - p.w/2 - x) <= r || abs(p.x + p.w/2 - x) <= r) && y >= p.y - p.h/2 && y <= p.y + p.h/2);
  }
  
  boolean collideCorners(Paddle p) {
    return(distance(x, y, p.x - p.w/2, p.y - p.h/2) <= r || distance(x, y, p.x + p.w/2, p.y - p.h/2) <= r || distance(x, y, p.x - p.w/2, p.y + p.h/2) <= r || distance(x, y, p.x + p.w/2, p.y + p.h/2) <= r);
  } 
  
  boolean collideTorB(Bricks brick) {
    return((abs(brick.y - brick.h/2 - y) <= r || abs(brick.y + brick.h/2 - y) <= r) && x >= brick.x - brick.w/2 && x <= brick.x + brick.w/2);
  }
  
  boolean collideLorR(Bricks brick) {
    return((abs(brick.x - brick.w/2 - x) <= r || abs(brick.x + brick.w/2 - x) <= r) && y >= brick.y - brick.h/2 && y <= brick.y + brick.h/2);
  }
  
  boolean collideC(Bricks brick) {
    return(distance(x, y, brick.x - brick.w/2, brick.y - brick.h/2) <= r || distance(x, y, brick.x + brick.w/2, brick.y - brick.h/2) <= r || distance(x, y, brick.x - brick.w/2, brick.y + brick.h/2) <= r || distance(x, y, brick.x + brick.w/2, brick.y + brick.h/2) <= r);
  } 
  
  
  boolean collide(Paddle p) {
    return(collideTopOrBottom(p) || collideLeftOrRight(p) || collideCorners(p));
  }
  
  void move() {
    x += vx;
    y += vy;
  }
  
  void draw() {
    ellipse(x, y, 2*r, 2*r);
  }
}

class Paddle {
  float x, y;
  float s;
  float w, h;
  boolean left, right;
  
  Paddle() {
    w = 100;
    h = 10;
    s = 1.5; // Paddle Speed
    
    left = false;
    right = false;
  }
  
  void initialize(float xi, float yi) {
    x = xi;
    y = yi;
  }
  
  void move() {
    if (left && x > (0 + w/2)) {
      x -= s;
      
      if (b.collide(this))
        b.y -= s+1;
    } else if (right && x < (width - w/2)) {
      x += s;
      
      if (b.collide(this))
        b.x += s+1;
    }
  }
  
  void draw() {
    rect(x, y, w, h);
  }
}


class Bricks {
  float x,y;
  float w, h;
  boolean hit;
  color c;
  Bricks(float inx, float iny, float inw, float inh, color inc, boolean inhit) {
    x = inx;
    y = iny;
    w = inw;
    h = inh;
    c = inc;
    hit = inhit;
  }
  
  void draw() {
    noStroke();
    fill(c);
    rect(x, y, w, h);
  }
}


// GLOBALS
Ball b;
Paddle p1;
Bricks[] brick;
int lives = 3;
int score = 0;

// GAME FUNCTIONS
void startGame() {
  p1.initialize(float(width)/2.0, height-10);
  b.initialize(float(width)/2.0, float(height)/2.0);
}

void endGame() {
  b.vx = 0;
  b.vy = 0;
  b.initialize(float(width)/2.0, float(height)/2.0);
  p1.initialize(float(width)/2.0, height-10);
}
  

float distance(float x1, float y1, float x2, float y2) {
  return(sqrt(sq(x2 - x1) + sq(y2 - y1)));
}

// EVENTS
void keyPressed() {
    if (keyCode == LEFT) {
      p1.left = true;
    } else if (keyCode == RIGHT) {
      p1.right = true;
    }
}

void keyReleased() {
    if (keyCode == LEFT) {
      p1.left = false;
    } else if (keyCode == RIGHT) {
      p1.right = false;
    }
}

void setup() {
  size(800, 600);
  ellipseMode(CENTER);
  rectMode(CENTER);
  frameRate(360);
  textSize(20);
  textAlign(CENTER);
  
  b = new Ball();
  p1 = new Paddle();
  brick = new Bricks[100];
  float wdth = width/10; //Standard Brick width
  float hght = height/30; //Standard Brick height
  float plusx = wdth/2; //offset from side of screen x axis
  float plusy = hght*3; //offset from side of screen x axis
  color col = color(0,0,0);
  for (int i=0; i<10; i++) {//Y
    for (int j=0; j<10; j++) {//X
      if (i<2) {
        col = color(255,0,0);
      } else if (i<4) {
        col = color(255,165,0);
      } else if (i<6) {
        col = color(230,230,0);
      } else if (i<8) {
        col = color(0,230,0);
      } else if (i<10) {
        col = color(0,200,200);
      }
      brick[i*10+j] = new Bricks(wdth*j+plusx, hght*i+plusy, wdth-2, hght-2, col, false);
    }
  }
  startGame();
}

void draw() {
  background(255);
  if (lives == 0) {
    endGame();
    textAlign(CENTER);
    textSize(50);
    text("Gameover", width/2, height/2+50);
    textSize(20);
  }
  // DRAW  
  stroke(0);
  fill(100);
  b.draw();
  p1.draw();
  for (Bricks b: brick) {
    if (b != null) {
      b.draw();
    }
  }
  fill(0);
  text("Lives: " + lives, 40, 25);
  text("Score: " + score, width-50, 25);
  b.checkCollisions();
  p1.move();
  b.move();  
}
