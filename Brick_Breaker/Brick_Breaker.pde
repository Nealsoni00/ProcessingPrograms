ArrayList<Brick> bricks = new ArrayList<Brick>();
int brickCount = 0;
int brickWidth = 100;
int brickHight = 50;
int brickX = 0;
int brickY = 0;


void setup() {
  size(700, 500);
  frameRate(60);
  for (int i = 0; i < 10; i++) {
    for (int j = 1; j <= 10; i++) {
      brickX = (brickWidth*j);
      bricks.add(new Brick());
      println("new brick added: " + j);
    }
    brickY = brickY + (i*brickHight);
    println(brickX);
    println(brickY);
  }
  for (int i = 0; i<bricks.size(); i++) {
    bricks.get(i).initialize(brickX, brickY);
    bricks.get(i).draw();
    println("brick: " + i + " drawn");
  }

}


void draw() {
  background(255);


  
}




class Brick {
  int x;
  int y;
  Brick() {
  }
  void initialize(int xi, int yi) {
    x = xi;
    y = yi;
  }
  void draw() {
    fill(0, 0, 0);
    rect(x, y, brickWidth, brickHight);
  }
}
int paddleX;
int paddleY;
class Paddle {
  int paddleWidth = 100;
  int paddleHight = 25;
  Paddle(int paddleX, int paddleY) {
  }
  void getX() {
  }
  void getY() {
  }
}
int ballX;
int ballY;
class Ball {
  int ballWidth = 10;
  int ballHight = 10;
  Ball() {
  }
  void draw() {
    fill(0, 0, 0);
    ellipse(ballX, ballY, ballWidth, ballHight);
  }
}
