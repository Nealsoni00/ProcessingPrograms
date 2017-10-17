

import shapes3d.utils.*;
import shapes3d.animation.*;
import shapes3d.*;
import processing.opengl.*;
//Terrain variables
Terrain terrain;
Box skybox;
Cone[] droids;
Tube tube;


TerrainCam cam;
int camHoversAt = 8;

float terrainSize = 1000;
float horizon = 400;

long time;
float camSpeed;
int count;
boolean clicked;

// List of image files for texture
String[] textures = new String[] {
  "grid01.png", "tartan.jpg", "rouge.jpg", 
  "globe.jpg", "sampler01.jpg"
};

//Projectile Variables _________________
Projectile ball;
//float windz=random(-0.05, 0.05);
//float windx=random(-.03, .03);
float windz=-.04;
float windx=0;
float drag=0;//or .000015
FloatList points;

void setup() {
  size(1400, 1000, P3D);
  cursor(CROSS);
    frameRate(60);

  //terrain stuff
  terrain = new Terrain(this, 60, terrainSize, horizon);
  terrain.usePerlinNoiseMap(0, 40, 0.15f, 0.15f);
  terrain.setTexture("grass2.jpg", 4);
  terrain.tag = "Ground";
  terrain.tagNo = -1;
  terrain.drawMode(S3D.TEXTURE);

  skybox = new Box(this, 1000, 500, 1000);
  skybox.setTexture("back.jpg", Box.FRONT);
  skybox.setTexture("front.jpg", Box.BACK);
  skybox.setTexture("left.jpg", Box.LEFT);
  skybox.setTexture("right.jpg", Box.RIGHT);
  skybox.setTexture("sky.jpg", Box.TOP);
  skybox.visible(false, Box.BOTTOM);
  skybox.drawMode(S3D.TEXTURE);
  skybox.tag = "Skybox";
  skybox.tagNo = -1;



  tube = new Tube(this, 10, 10);
  tube.setSize(1, 1, 1, 1, 10);
  tube.moveTo(getRandomPosOnTerrain(terrain, terrainSize, 15.5));
  tube.tagNo = 0;
  tube.setTexture(textures[1]);
  tube.drawMode(S3D.TEXTURE);
  tube.fill(color(0, 0, 0), Tube.S_CAP);
  tube.fill(color(0, 0, 0), Tube.E_CAP);
  tube.drawMode(S3D.SOLID, Tube.BOTH_CAP);

  camSpeed = 10;
  cam = new TerrainCam(this);
  cam.adjustToTerrain(terrain, Terrain.WRAP, camHoversAt);
  cam.camera();
  cam.speed(camSpeed);
  cam.forward.set(cam.lookDir());
  // Tell the terrain what camera to use
  terrain.cam = cam;
  time = millis();
  //Projectile stuff _____________________________________
  frameRate(60);
  ellipseMode(CENTER);
  ball = new Projectile(mstopf(20), 45, 0.01);//10 m/s = .166666 m/f = 3.33333 pix/frame
  points = new FloatList();
}

void draw() {
  updateBall();
  background(0);
  // Get elapsed time
  long t = millis() - time;
  time = millis();

  // Update shapes on terrain
  update(t/1000.0);

  // Update camera speed and direction
  if (mousePressed) 
    processMousePressed();
  if (keyPressed) 
    processKeyPressed(t);
  // Clicked on artefact?
  if (clicked)
    processMouseClick();

  // Calculate amount of movement based on velocity and time
  cam.move(t/1000.0);
  // Adjust the cameras position so we are over the terrain
  // at the given height.
  cam.adjustToTerrain(terrain, Terrain.WRAP, camHoversAt);
  // Set the camera view before drawing
  cam.camera();

  tube.draw();
  terrain.draw();

  // Get rid of directional lights so skybox is evenly lit.
  skybox.moveTo(cam.eye().x, 0, cam.eye().z);
  skybox.draw();
}
//Projetile Methods _________________________________________
void updateBall() {
  if (ball.pos.x>1000 || ball.pos.y>700) {
    ball.accel.x=0;
    ball.accel.y=msstopff(-9.8);
    ball.vel.z=0;
    ball.vel.x=ball.vi*cos(ball.angle*(3.14159265/180));
    ball.vel.y=-ball.vi*sin(ball.angle*(3.14159265/180));
    ball.vel.z=0;
    ball.pos.x=mtop(.1);
    ball.pos.y=height-mtop(10);
    ball.pos.z=0;
    windx=random(-.03, .03);
    windz=random(-0.05, 0.05);
    windz=-.04;
    // windx=-.01;
  }


  for (int i=0; i<points.size (); i+=3) {
    stroke(30, 200, 10);
    strokeWeight(3);
    point(points.get(i), points.get(i+1), points.get(i+2));
  }

  drawText();
  ball.draw();
  //  drag=constrain(drag, 0, .000015);
  drag=constrain(drag, 0, 0.00009);
  windx=constrain(windx, -.01, .01);
  ball.drag.y = (drag*ball.vel.y*ball.vel.y)/ball.mass;
  ball.drag.y = constrain(ball.drag.y, -99999, -ball.accel.y);

  if (ball.vel.y>0) {
    ball.accel.y=msstopff(-9.8)+ball.drag.y;
  } else {
    ball.accel.y=msstopff(-9.8)-ball.drag.y;
  }

  //ball.accel.y=ball.accel.y+ball.drag.y; //(ball.mass*msstopff(9.8)-2*ball.vel.y)/ball.mass
  ball.accel.y=constrain(ball.accel.y, msstopff(-120), 0);
  ball.vel.y=ball.vel.y-ball.accel.y;
  ball.pos.y=ball.pos.y+ball.vel.y;


  ball.drag.x = (drag*ball.vel.x*ball.vel.x)/ball.mass;
  // ball.drag.x = constrain(ball.drag.x, -windx, abs(windx));
  if (ball.vel.x>0) {
    ball.accel.x = windx-ball.drag.x;
  } else {
    ball.accel.x = windx+ball.drag.x;
  }
  ball.vel.x=ball.vel.x+ball.accel.x;
  ball.pos.x=ball.pos.x+ball.vel.x;


  ball.drag.z = (drag*ball.vel.z*ball.vel.z)/ball.mass;//.000004
  ball.drag.z = constrain(ball.drag.z, 0, abs(windz));
  if (ball.vel.z>0) {
    ball.accel.z = windz-ball.drag.z;
  } else {
    ball.accel.z = windz+ball.drag.z;
  }
  ball.vel.z=ball.vel.z+ball.accel.z;
  ball.pos.z=ball.pos.z+ball.vel.z;

  points.append(ball.pos.x);
  points.append(ball.pos.y);
  points.append(ball.pos.z);
}
float mtop(float v) {
  return(v*20);
}
float mstopf(float v) {
  return(v*20/60);
}
float msstopff(float a) {
  return(a*20/60/60);
}
void drawText() {
  text("X: "+nf(ball.pos.x, 3, 2)+ " m", 10, 10);
  println(nf(ball.pos.x, 3, 2));
  text("X velocity: "+nf(convertVel("x"), 2, 2)+" m/s", 10, 40);
  text("X acceleration: "+nf(ball.accel.x/20*60*60, 2, 2)+" m/s^2", 10, 90);

  text("Y: "+nf(height-ball.pos.y, 3, 2)+ " m", 230, 10);
  text("Y velocity: "+nf(-convertVel("y"), 2, 2)+" m/s", 230, 40);
  text("Y acceleration: "+nf(ball.accel.y/20*60*60, 2, 2)+" m/s^2", 230, 90);

  text("Z: "+nf(ball.pos.z, 3, 2)+ " m", 460, 10);
  text("Z velocity: "+nf(convertVel("z"), 2, 2)+" m/s", 460, 40);
  text("Z acceleration: "+nf(ball.accel.z/20*60*60, 2, 2)+" m/s^2", 460, 90);

  text("Percent Drag: "+str(int(drag/0.00009*100))+"%", 690, 10);
  text("Wind X: "+str(int(windx/.01*100))+"%", 690, 40);
  text("20 Pixels = 1 Meter ", 690, 90);
}
float convertVel(String axis) {
  if (axis.equals("x")) {
    return ball.vel.x/20*60;
  }  
  if (axis.equals("y")) {
    return ball.vel.y/20*60;
  }  
  if (axis.equals("z")) {
    return ball.vel.z/20*60;
  } else {
    return 0.0;
  }
}
void keyPressed() {
  if (keyCode== UP) {
    drag+=.000008;
  }
  if (keyCode== DOWN) {
    drag-=.000008;
  }
  if (keyCode == LEFT) {
    windx-=.001;
  }
  if (keyCode == RIGHT) {
    windx+=.001;
  }
  if (keyCode == SHIFT) {
    points.clear();
  }
}

void processMousePressed() {
  float achange = (mouseX - pmouseX) * PI / width;
  // Keep view and move directions the same
  cam.rotateViewBy(achange);
  cam.turnBy(achange);
}

void processKeyPressed(long t) {
  if (key == 'W' || key =='w' || key == 'P' || key == 'p') {
    camSpeed += (t/100.0);
    cam.speed(camSpeed);
  } else if (key == 'S' || key =='s' || key == 'L' || key == 'l') {
    camSpeed -= (t/100.0);
    cam.speed(camSpeed);
  } else if (key == ' ') {
    camSpeed = 0;
    cam.speed(camSpeed);
  }
}


void processMouseClick() {
  clicked = false; // reset
  Shape3D selected = Shape3D.pickShape(this, mouseX, mouseY);
  println(selected);
  if (selected != null) {
    if (selected.tagNo > textures.length)
      selected .fill(color(random(128, 255), random(128, 255), random(128, 255)));
    else if (selected.tagNo >= 0) {
      selected.tagNo = (selected.tagNo + 1) % textures.length;
      selected.setTexture(textures[selected.tagNo]);
    }
  }
}

public void update(float time) {
  PVector np = new PVector(float(nf(ball.pos.x, 3, 2)),float(nf(height-ball.pos.y, 3, 2)),float(nf(ball.pos.z, 3, 2)));
  //tube.rotateBy(time*radians(25.6f), time*radians(6.871f), time*radians(17.3179f));
  tube.moveTo(ball.pos);
  println(np);
  box(10*5, 10, 10);
}

public PVector getRandomPosOnTerrain(Terrain t, float tsize, float height) {
  PVector p = new PVector(0, 0, 0);
  p.y = t.getHeight(p.x, p.z) - height;
  return p;
}
public PVector getRandomVelocity(float speed) {
  PVector v = new PVector(random(-10000, 10000), 0, random(-10000, 10000));
  v.normalize();
  v.mult(speed);
  return v;
}


public void mouseClicked() {
  clicked = true;
}
class Projectile {
  PMatrix3D matrix = new PMatrix3D();
  float angle;
  float mass;
  float vi;
  int radius = 10;
  //Cone arrow = new Cone(this,40);
  PVector pos = new PVector(mtop(.1), height-mtop(10), 0);
  PVector vel = new PVector();
  PVector accel = new PVector(msstopff(-9.8), 0, 0);
  PVector drag = new PVector();
  Projectile(float vii, float anglee, float masss) {
    angle=anglee;
    vi=vii;
    vel.x = vi*cos(angle*(3.14159265/180));
    vel.y = -vi*sin(angle*(3.14159265/180));
    mass=masss;
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
  void updateMatrix() {
    matrix.m03 = pos.x; 
    matrix.m13 = pos.y; 
    matrix.m23 = pos.z;
  }
  void drawShape() {
    stroke(255, 255, 255, 128);
    strokeWeight(2); 
    fill(255);
    
    //translate(0, 0, 0);
    //rotateX(-atan(vel.y/vel.z));
    //rotateY(atan(vel.z/vel.y));
    
//    rotateZ(atan(vel.y/vel.x));
//
//    // rotateZ(atan(vel.z));
//    box(radius*5, radius, radius);
//    //    arrow.setSize(radius, radius, radius*10);
//    //    cone.moveTo(pos);
//    translate(-radius*2.5, 0, 0);
//    box(radius*.4, radius*.4, radius*2);
//    box(radius*.4, radius*2, radius*.4);
//    translate(0, 0, 0);
  }
  void roll(float rotX, float rotY, float rotZ) {
    matrix.rotateY(radians(rotY));  
    matrix.rotateX(radians(rotX));  
    matrix.rotateZ(radians(rotZ));
  }
}


