class Projectile {
  //projectile deffinitions 
  PMatrix3D matrix = new PMatrix3D();
  FloatList points;
  float angle, mass;
  float vi;
  int radius = 10, launchCount = 0;
  //Cone arrow = new Cone(this,40);
  //vectors
  PVector pos   = new PVector(mtop(.1), height-mtop(10), 0);
  PVector vel   = new PVector();
  PVector accel = new PVector(msstopff(-9.8), 0, 0);
  PVector drag  = new PVector();
  //Exploding variables
  ArrayList plist = new ArrayList();
  int MAX = 30;

  Projectile(float vii, float anglee, float masss) {
    angle  = anglee;
    vi     = vii;
    vel.x  = vi*cos(angle*(3.14159265/180));
    vel.y  = -vi*sin(angle*(3.14159265/180));
    mass   = masss;
    points = new FloatList();
  }
  void draw() {
    fill(0);
    stroke(0);
    //ellipse(pos.x, pos.y, 10+(pos.z/60), 10+(pos.z/60));
    pushMatrix(); 
    updateMatrix(); 
    applyMatrix(matrix);
    drawShape();
    popMatrix();
  }
  void next() {
    if (pos.x>1990 || pos.y>700 || pos.z > 1990 || pos.z < -1990) {
      explode();
      resetBall();
    }
    //  drag=constrain(drag, 0, .000015);

    if (vel.y>0) {
      accel.y=msstopff(-9.8)+drag.y;
    } else {
      accel.y=msstopff(-9.8)-drag.y;
    }
    //accel.y=accel.y+drag.y; //(mass*msstopff(9.8)-2*vel.y)/mass
    accel.y = constrain(accel.y, msstopff(-120), 0);
    vel.y   = vel.y-accel.y;
    pos.y   = pos.y+vel.y;


    drag.x = (dragC*vel.x*vel.x)/mass;
    // drag.x = constrain(drag.x, -windx, abs(windx));
    if (vel.x>0) {
      accel.x = wind.x-drag.x;
    } else {
      accel.x = wind.x+drag.x;
    }
    vel.x  = vel.x+accel.x;
    pos.x  = pos.x+vel.x;
    drag.z = (dragC*vel.z*vel.z)/mass;//.000004
    drag.z = constrain(drag.z, 0, abs(wind.z));
    if (vel.z>0) {
      accel.z = wind.z-drag.z;
    } else {
      accel.z = wind.z+drag.z;
    }
    vel.z=vel.z+accel.z;
    pos.z=pos.z+vel.z;

    points.append(pos.x);
    points.append(pos.y);
    points.append(pos.z);
    dragC=constrain(dragC, 0, 0.00009);
    wind.x=constrain(wind.x, -.01, .01);
    drag.y = (dragC*vel.y*vel.y)/mass;
    drag.y = constrain(drag.y, -99999, -accel.y);
    //update previous explotions
    updateExploded();
  }
  void points() {
    for (int i=0; i<points.size (); i+=3) {
      stroke(30, 200, 10);
      strokeWeight(3);
      point(points.get(i), points.get(i+1), points.get(i+2));
    }
  }
  void resetBall() {
    accel.x = 0;
    accel.y = msstopff(-9.8);
    vel.z   = 0;
    vel.x   = vi*cos(angle*(3.14159265/180));
    vel.y   = -vi*sin(angle*(3.14159265/180));
    vel.z   = 0;
    pos.x   = mtop(.1);
    pos.y   = height-mtop(10);
    pos.z   = 0;
    wind.z = random(-.05, .05);
    launchCount ++;
  }
  void updateMatrix() {
    matrix.m03 = pos.x; 
    matrix.m13 = pos.y; 
    matrix.m23 = pos.z;
  }
  void drawShape() {
    stroke(255, 255, 255, 128);
    strokeWeight(2); 
    fill(255);

    if (vel.z <0) {
      rotateY(atan(vel.x/vel.z)+PI/2);
      rotateZ(atan(vel.y/vel.x));
    } else if (vel.z > 0) {
      rotateY(atan(vel.x/vel.z)+PI/2);
      rotateZ(PI-atan(vel.y/vel.x));
    } else {
      rotateZ(atan(vel.y/vel.x));
    }
    rotateX(millis()/100);

    box(radius*5, radius, radius);
    //    arrow.setSize(radius, radius, radius*10);
    // cone.moveTo(pos);
    translate(-radius*2.5, 0, 0);
    box(radius*.4, radius*.4, radius*2);
    box(radius*.4, radius*2, radius*.4);
    translate(0, 0, 0);
  }
  void roll(float rotX, float rotY, float rotZ) {
    matrix.rotateY(radians(rotY));  
    matrix.rotateX(radians(rotX));  
    matrix.rotateZ(radians(rotZ));
  }
  void explode() {
    for (int i = 0; i < MAX; i ++) {
      plist.add(new Particle(pos.x, pos.y, -pos.z)); // fill ArrayList with particles
      println(pos);
      if (plist.size() > 5*MAX) {
        plist.remove(0);
      }
    }
  }
  void updateExploded() {
    for (int i = 0; i < plist.size (); i++) {
      Particle p = (Particle) plist.get(i); 
      //makes p a particle equivalent to ith particle in ArrayList
      p.run();
      p.update();
      p.gravity();
    }
    while (plist.size () > 100) {
      for (int i = 0; i < plist.size (); i++) {
        plist.remove(i);
      }
    }
//    while (plist.size () > 0) {
//      for (int i = 0; i < plist.size (); i++) {
//        if (plist.get(i).t2-plist.get(i).t1 > 1000){
//          plist.remove(i);
//        }
//      }
//    }
  }
  void clearExplosion() {
    boolean clearall = true;
    while (plist.size () > 0) {
      for (int i = 0; i < plist.size (); i++) {
        plist.remove(i);
      }
    }
  }
}

