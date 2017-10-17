boolean galileoMove = false;
int gx = 0;
float ypos = 100;
float speed = 0;
PImage LeaningTowerOfPisa;
PImage galileo;
PImage man;
int people = 0;
float suncolorR = 255;
float suncolorG = 255;
float suncolorB = 0;
float y = 0;
float m = 0; 
float v = 0;
float x = 200;
float vx = random(10, 25);
float vi = 40;
int score = 0;
int highscore = 0;
boolean ballDropped = false;
boolean gameStart = false;
void setup() {
  size(800, 800);
  background(0, 150, 205);
  LeaningTowerOfPisa = loadImage("LeaningTowerOfPisa.jpg");
  galileo = loadImage("galileo.jpg");
  man = loadImage("man.jpg");
}

void draw() {
  //drawings

  background(0, 150, 205);
  fill(suncolorR, suncolorG, suncolorB);
  ellipse(100, 100, 75, 75);
  stroke(0);
  fill(100, 255, 50);
  rect(0, 600, 800, 200);
  fill(255);
  noStroke();
  ellipse(150, 490, 100, 100);
  rect(100, 500, 100, 100);
  fill(0, 150, 205, 150);
  rect(115, 515, 75, 25);
  strokeWeight(2);
  stroke(255);
  line(150, 415, 150, 500);
  fill(255);
  stroke(0);
  ellipse(450, 400, 200, 200);
  line(450, 275, 450, 300);
  triangle(300, 400, 400, 400, 350, 350);
  triangle(400, 400, 500, 400, 450, 350);
  triangle(500, 400, 600, 400, 550, 350);
  rect(300, 400, 300, 200);
  fill(0, 150, 205, 150);
  rect(310, 420, 100, 50);
  rect(415, 420, 100, 50);
  galileo.resize(75, 75);
  image(galileo, 550 - gx, 150);
  image(LeaningTowerOfPisa, 500, 100);
  if (galileoMove == true) {
    gx += 2;
  }
  if (gx > 50) {
    galileoMove = false;
    fill(0); 
    ellipse(500, ypos, 15, 15);
    ypos += speed;
    speed += 0.6;
  }
  if ((ypos > 600) || (ypos == 600)) {
    ypos = 600;
    speed *= -.6;
  }
  //randomSeed(20);
  while ( people < 100) {
    man.resize(20, 50);
    int m1 = millis();
      if (m1%100 == 0) {
        image(man, random(800), random(600, 800));
        people += 1;
      }
  }
  if (people == 100) {
    people = 0;
  }



  //background(255);
  if (gameStart) {
    textSize(30);
    fill(0);
    noStroke();
    fill(255, 0, 0);
    rect(width-50, y-50, 25, 125);
    rect(25, mouseY, 25, 125);
    fill(0);
    text(score, width/2, 30);
    if (m > 0) {
      ellipse(x, y, 15, 15);
      y = y + v;
      v = v + 1.0;
      v = v;
      x += vx;
      fill(255, 0, 0);
      if (y >= height) {
        v *= -1;
        y = height;
      }
      if (y <= 0) {
        y = 0;
        v *= -1;
      } 
      if (x >= width-50) {
        vx *= -1; 
        x = width-50;
      }

      if (x <= 50 && y >mouseY && y < mouseY + 125) {
        vx *= -1; 
        x = 50;
        score = score + 1;
        println(score);
      }
      if (score >= highscore) {
        highscore = 0;
        highscore += score;
      }
      if (x <= 0) {
        fill(0);
        text("GAMEOVER CLICK ANYWHERE TO REPLAY", 115, 400);    
        vx = random(10, 25);
        v = 0;
        m = 0;
      }
      fill(255, 0, 0); 
      fill(0);
    }
    text(highscore, 215, 30);
    text("HIGHSCORE:", 30, 30);
  }
}
void mousePressed() {
  if ((mouseX > 500) && (mouseX < 700) && (mouseY > 100) && (mouseY < 650)) {
    galileoMove = true;
    gameStart = true;
  }
  if ((mouseX < 175) && (mouseY < 175)) {
    suncolorR = random(255);
    suncolorG = random(255);
    suncolorB = random(255);
  }
  score = 0;
  m = m + 1;

  if (m > 2 || m == 2) {
    //y += - 100; 
    //v = -20;
  } else {
    y = 200;
    v = 0;
    x = 500;
    vx = random(10, 25);
  }
}

