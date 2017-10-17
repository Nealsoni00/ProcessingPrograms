class Particle {
  float r = 2;
  PVector pos, speed, grav; 
  ArrayList tail;
  float splash = 5;
  int margin = 2;
  int taillength = 25;
  PVector startPos;
  int t1, t2;
  Particle(float tempx, float tempy, float tempz) {
    float _startx = tempx + random(-splash, splash);
    float _starty = tempy + random(-splash, splash);
    float _startz = tempz + random(-splash, splash);
    //_startx = constrain(_startx, 0, width);
    //_starty = constrain(_starty, 0, height);
    _startz = -_startz;
    //_startz = constrain(_startz, 0, 100);
    float xspeed = random(-3, 3);
    float yspeed = random(-3, 3);
    float zspeed = random(-3, 3);
    startPos = new PVector(_startx, _starty, _startz);
    pos = new PVector(_startx, _starty, _startz);
    speed = new PVector(xspeed, yspeed, zspeed);
    grav = new PVector(0, 0.02, 0);
    tail = new ArrayList();
    t1 = millis();
  }

  void run() {
    pos.add(speed);

    tail.add(new PVector(pos.x, pos.y, pos.z));
    if (tail.size() > taillength) {
      tail.remove(0);
    }
    //    if (pos.dist(startPos)>100){
    //      for (int i = 0; i < taillength; i ++){
    //        tail.remove(i);
    //      }
    //    }

    float damping = random(-0.5, -0.6);
    if (pos.x <-1990 || pos.x >= 1990) {
      speed.x *= damping;
    }
    if (pos.y > 700) {
      speed.y *= damping;
    }
    if (pos.z > 1990 || pos.z < -1990) {
      speed.z *= damping;
   }
  }

  void gravity() {
    speed.add(grav);
  }

  void update() {
    for (int i = 0; i < tail.size (); i++) {
      PVector tempv = (PVector)tail.get(i);
      noStroke();
      fill(6*i + 50);
      stroke(255);
      point(tempv.x, tempv.y, tempv.z);
    }
    t2 = millis();
  }
}

