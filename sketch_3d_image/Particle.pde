class Particle {
  float r = 2;
  PVector pos,speed,grav; 
  ArrayList tail;
  float splash = 5;
  int margin = 2;
  int taillength = 25;

  Particle(float tempx, float tempy, float tempz) {
    float startx = tempx + random(-splash,splash);
    float starty = tempy + random(-splash,splash);
    float startz = tempz + random(-splash,splash);
    startx = constrain(startx,0,width);
    starty = constrain(starty,0,height);
    startz = constrain(startz,0,100);
    float xspeed = random(-3,3);
    float yspeed = random(-3,3);
    float zspeed = random(-3,3);

    pos = new PVector(startx,starty,startz);
    speed = new PVector(xspeed,yspeed,zspeed);
    grav = new PVector(0,0.02,0);
    
    tail = new ArrayList();
  }

  void run() {
    pos.add(speed);

    tail.add(new PVector(pos.x,pos.y,pos.z));
    if(tail.size() > taillength) {
      tail.remove(0);
    }

    float damping = random(-0.5,-0.6);
    if(pos.x > width - margin || pos.x < margin) {
      speed.x *= damping;
    }
    if(pos.y > height -margin) {
      speed.y *= damping;
    }
    if(pos.z > 100 -margin) {
      speed.z *= damping;
    }
  }

  void gravity() {
    speed.add(grav);
  }

  void update() {
    for (int i = 0; i < tail.size(); i++) {
      PVector tempv = (PVector)tail.get(i);
      noStroke();
      fill(6*i + 50);
      stroke(255);
      point(tempv.x,tempv.y,tempv.z);
    }
  }
}

